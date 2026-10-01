package bench.generated.c122;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.Random;
import java.util.function.IntBinaryOperator;
import java.util.Comparator;
import com.carrotsearch.hppc.sorting.IndirectSort;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class IndirectSortBenchmark {

    private int size;
    private int[] values;               // backing data for int comparator
    private int[] orderArray;           // pre‑shuffled order array
    private Integer[] objectArray;      // generic objects to sort indirectly
    private IntBinaryOperator intComparator;
    private Comparator<Integer> objectComparator;

    @Setup(Level.Trial)
    public void setUp() {
        // Fixed size that keeps benchmark runtime reasonable.
        size = 2048;

        Random rnd = new Random(0x1234ABCDL);

        // Populate values used by the int comparator.
        values = new int[size];
        for (int i = 0; i < size; i++) {
            values[i] = rnd.nextInt();
        }

        // Comparator that compares indices based on the values array.
        intComparator = (a, b) -> Integer.compare(values[a], values[b]);

        // Prepare an order array (0..size‑1) in random order.
        orderArray = new int[size];
        for (int i = 0; i < size; i++) {
            orderArray[i] = i;
        }
        // Shuffle the order array.
        for (int i = size - 1; i > 0; i--) {
            int j = rnd.nextInt(i + 1);
            int tmp = orderArray[i];
            orderArray[i] = orderArray[j];
            orderArray[j] = tmp;
        }

        // Prepare generic object array.
        objectArray = new Integer[size];
        for (int i = 0; i < size; i++) {
            objectArray[i] = values[i]; // reuse the same int values as Integer objects.
        }

        // Natural order comparator for Integer objects.
        objectComparator = Comparator.naturalOrder();
    }

    @Benchmark
    public int[] benchmarkMergesortStartLength() {
        // Indirect sort over a range using the int comparator.
        return IndirectSort.mergesort(0, size, intComparator);
    }

    @Benchmark
    public int[] benchmarkMergesortOrderArray() {
        // Indirect sort given a pre‑built order array.
        return IndirectSort.mergesort(orderArray, intComparator);
    }

    @Benchmark
    public int[] benchmarkMergesortGeneric() {
        // Indirect sort for generic objects.
        return IndirectSort.mergesort(objectArray, 0, size, objectComparator);
    }
}
