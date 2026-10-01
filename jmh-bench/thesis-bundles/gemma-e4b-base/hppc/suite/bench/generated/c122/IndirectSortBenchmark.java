package bench.generated.c122;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import com.carrotsearch.hppc.sorting.IndirectSort;
import java.util.function.IntBinaryOperator;
import java.util.Comparator;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class IndirectSortBenchmark {

    private static final int ARRAY_SIZE = 10000;
    private static final int START_INDEX = 0;

    // Input for mergesort(int start, int length, IntBinaryOperator comparator)
    // Although the method creates the array internally, we define the size/parameters.
    
    // Input for mergesort(int[] orderArray, IntBinaryOperator comparator)
    private int[] orderArray;

    // Input for mergesort(T[] input, int start, int length, Comparator<? super T> comparator)
    private Integer[] objectInputArray;
    private Comparator<Integer> objectComparator;

    @Setup(Level.Trial)
    public void setup() {
        // 1. Setup for mergesort(int start, int length, IntBinaryOperator comparator)
        // No specific input array needed, parameters are fixed.

        // 2. Setup for mergesort(int[] orderArray, IntBinaryOperator comparator)
        // Create a random array of indices
        orderArray = new int[ARRAY_SIZE];
        for (int i = 0; i < ARRAY_SIZE; i++) {
            orderArray[i] = i;
        }

        // 3. Setup for mergesort(T[] input, int start, int length, Comparator<? super T> comparator)
        // Create an array of objects (Integers)
        objectInputArray = new Integer[ARRAY_SIZE];
        for (int i = 0; i < ARRAY_SIZE; i++) {
            objectInputArray[i] = i;
        }
        
        // Define a comparator for the objects
        objectComparator = Comparator.naturalOrder();
    }

    /**
     * Benchmarks IndirectSort.mergesort(int start, int length, IntBinaryOperator comparator).
     * This method creates a new index array internally.
     */
    @Benchmark
    public int[] benchmarkMergesortIntIndices() {
        IntBinaryOperator comparator = (a, b) -> a - b;
        return IndirectSort.mergesort(START_INDEX, ARRAY_SIZE, comparator);
    }

    /**
     * Benchmarks IndirectSort.mergesort(int[] orderArray, IntBinaryOperator comparator).
     * This method clones the input array internally.
     */
    @Benchmark
    public int[] benchmarkMergesortIntArray() {
        IntBinaryOperator comparator = (a, b) -> a - b;
        return IndirectSort.mergesort(orderArray, comparator);
    }

    /**
     * Benchmarks IndirectSort.mergesort(T[] input, int start, int length, Comparator<? super T> comparator).
     * This is the generic version using object arrays.
     */
    @Benchmark
    public int[] benchmarkMergesortObjectArray() {
        // The comparator is set up in @Setup
        return IndirectSort.mergesort(objectInputArray, START_INDEX, ARRAY_SIZE, objectComparator);
    }
}
