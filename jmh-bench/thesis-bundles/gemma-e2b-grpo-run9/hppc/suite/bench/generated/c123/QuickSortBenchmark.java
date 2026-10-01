package bench.generated.c123;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import com.carrotsearch.hppc.sorting.QuickSort;
import java.util.function.IntBinaryOperator;
import java.util.Arrays;

@BenchmarkMode(Mode.AverageTime)
@State(Scope.Benchmark)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class QuickSortBenchmark {

    // Helper method to create a mutable copy of an array for sorting
    // Not strictly needed if we create new arrays in the benchmark, but kept for context.
    private int[] copyArray(int[] original) {
        return Arrays.copyOf(original, original.length);
    }

    // --- Benchmarks for QuickSort.sort(int[] array, IntBinaryOperator comparator) ---

    @Benchmark
    public void benchmarkSort_SmallArray(Blackhole bh) {
        // Small array to test the base case/insertion sort threshold
        int[] array = {5, 2, 8, 1, 9, 4, 7, 3, 6};
        try {
            QuickSort.sort(array, (i, j) -> Integer.compare(array[i], array[j]));
        } catch (Exception e) {
            // Ignore exceptions
        }
        bh.consume(null);
    }

    @Benchmark
    public void benchmarkSort_MediumArray(Blackhole bh) {
        // Medium array size, testing the main recursive logic
        int[] array = {5, 2, 8, 1, 9, 4, 7, 3, 6, 10, 11, 12, 13, 14, 15};
        try {
            QuickSort.sort(array, (i, j) -> Integer.compare(array[i], array[j]));
        } catch (Exception e) {
            // Ignore exceptions
        }
        bh.consume(null);
    }

    @Benchmark
    public void benchmarkSort_LargeArray(Blackhole bh) {
        // Larger array size to stress the partitioning and recursion logic
        int size = 10000;
        int[] array = new int[size];
        for (int i = 0; i < size; i++) {
            // Populate with random data to ensure the sort logic is exercised
            array[i] = (int) (Math.random() * size);
        }
        try {
            QuickSort.sort(array, (i, j) -> Integer.compare(array[i], array[j]));
        } catch (Exception e) {
            // Ignore exceptions
        }
        bh.consume(null);
    }
}
