package bench.generated.c122;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.function.IntBinaryOperator;

import com.carrotsearch.hppc.sorting.IndirectSort;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class IndirectSortBenchmark {

    // --- State Fields ---
    private int start;
    private int length;
    private IntBinaryOperator comparator;
    private int[] inputOrderArray;

    // Constants for benchmark size
    private static final int ARRAY_SIZE = 10000;
    private static final int START_INDEX = 0;

    @Setup
    public void setup() {
        // Initialize parameters
        this.start = START_INDEX;
        this.length = ARRAY_SIZE;

        // Create a fixed comparator (e.g., identity comparator)
        this.comparator = (a, b) -> Integer.compare(a, b);

        // Build the input array once in Setup using the public API
        // IndirectSort.mergesort(start, length, comparator) creates [start, start+1, ..., start+length-1]
        this.inputOrderArray = IndirectSort.mergesort(start, length, comparator);
    }

    /**
     * Benchmark for mergesort(int start, int length, IntBinaryOperator comparator).
     * This method constructs the initial array internally and then sorts it.
     */
    @Benchmark
    public void testMergesortWithStartAndLength(Blackhole bh) {
        // Call the method under test. It handles array creation internally.
        int[] result = IndirectSort.mergesort(start, length, comparator);
        bh.consume(result);
    }

    /**
     * Benchmark for mergesort(int[] orderArray, IntBinaryOperator comparator).
     * This method takes an existing array, clones it, and sorts the clone.
     */
    @Benchmark
    public void testMergesortWithArray(Blackhole bh) {
        // Call the method under test using the pre-built array.
        int[] result = IndirectSort.mergesort(inputOrderArray, comparator);
        bh.consume(result);
    }
}
