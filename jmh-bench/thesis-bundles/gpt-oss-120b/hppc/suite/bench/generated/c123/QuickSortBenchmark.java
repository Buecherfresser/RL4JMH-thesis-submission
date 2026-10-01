package bench.generated.c123;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import com.carrotsearch.hppc.sorting.QuickSort;
import java.util.Random;
import java.util.function.IntBinaryOperator;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class QuickSortBenchmark {

    @Param({"128", "1024", "65536"})
    public int size;

    private int[] source;

    @Setup(Level.Trial)
    public void setUp() {
        Random rnd = new Random(0x1234abcdL);
        source = new int[size];
        for (int i = 0; i < size; i++) {
            source[i] = rnd.nextInt();
        }
    }

    @Benchmark
    public int[] sortFullArray() {
        int[] arr = source.clone();
        IntBinaryOperator comparator = (i, j) -> Integer.compare(arr[i], arr[j]);
        QuickSort.sort(arr, comparator);
        return arr;
    }

    @Benchmark
    public int[] sortPartialArray() {
        int[] arr = source.clone();
        IntBinaryOperator comparator = (i, j) -> Integer.compare(arr[i], arr[j]);
        int to = size / 2;
        QuickSort.sort(arr, 0, to, comparator);
        return arr;
    }
}
