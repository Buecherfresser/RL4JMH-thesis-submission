package bench.generated.c123;

import com.carrotsearch.hppc.sorting.QuickSort;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.function.IntBinaryOperator;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class QuickSortBenchmark {

    private int[] array;
    private IntBinaryOperator comparator;
    private int arraySize;
    private Random random;

    @Setup(Level.Trial)
    public void setupTrial() {
        // Initialize common components once per trial
        arraySize = 10000;
        array = new int[arraySize];
        comparator = (a, b) -> Integer.compare(a, b);
        random = new Random(42); // Fixed seed for reproducibility
    }

    @Setup(Level.Invocation)
    public void setupInvocation() {
        // Reset the array state before every invocation to ensure independence
        for (int i = 0; i < arraySize; i++) {
            array[i] = random.nextInt(arraySize * 2);
        }
    }

    /**
     * Benchmarks QuickSort sorting the entire array.
     */
    @Benchmark
    public void benchmarkFullArraySort(Blackhole bh) {
        // The method modifies the array in place.
        QuickSort.sort(array, comparator);
        bh.consume(array);
    }

    /**
     * Benchmarks QuickSort sorting a specific sub-range of the array.
     */
    @Benchmark
    public void benchmarkSubRangeSort(Blackhole bh) {
        // Sort the middle 50% of the array
        int fromIndex = arraySize / 4;
        int toIndex = arraySize * 3 / 4;

        // The method modifies the array in place.
        QuickSort.sort(array, fromIndex, toIndex, comparator);
        bh.consume(array);
    }
}
