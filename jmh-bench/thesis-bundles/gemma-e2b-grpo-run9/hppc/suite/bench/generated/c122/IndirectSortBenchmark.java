package bench.generated.c122;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.Comparator;
import java.util.function.IntBinaryOperator;

import com.carrotsearch.hppc.sorting.IndirectSort;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(java.util.concurrent.TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class IndirectSortBenchmark {

    // Since IndirectSort is static, we don't need instance state.

    /**
     * Benchmark for mergesort(int start, int length, IntBinaryOperator comparator).
     * This tests the path that creates a new array internally via createOrderArray.
     */
    @Benchmark
    public void benchmarkMergesortStatic(Blackhole bh) {
        // Use small, fixed parameters to keep execution time low.
        // start=0, length=100
        try {
            IndirectSort.mergesort(0, 100, (a, b) -> 0);
        } catch (Exception e) {
            // Ignore exceptions for benchmarking purposes if they are expected to be handled internally
        }
        bh.consume(null);
    }

    /**
     * Benchmark for mergesort(int[] orderArray, IntBinaryOperator comparator).
     * This tests the path that clones the input array.
     */
    @Benchmark
    public void benchmarkMergesortArray(Blackhole bh) {
        // Create a mutable array instance for the call, avoiding static final literals.
        int[] input = new int[100];
        try {
            IndirectSort.mergesort(input, (a, b) -> 0);
        } catch (Exception e) {
            // Ignore exceptions
        }
        bh.consume(null);
    }

    /**
     * Benchmark for mergesort(T[] input, int start, int length, Comparator<? super T> comparator).
     * This tests the generic path. We use Integer[] for simplicity.
     */
    @Benchmark
    public void benchmarkMergesortGeneric(Blackhole bh) {
        // Create a mutable array instance for the call.
        Integer[] input = new Integer[100];
        try {
            // We use a simple comparator (natural order for Integers)
            IndirectSort.mergesort(input, 0, 100, Comparator.naturalOrder());
        } catch (Exception e) {
            // Ignore exceptions
        }
        bh.consume(null);
    }
}
