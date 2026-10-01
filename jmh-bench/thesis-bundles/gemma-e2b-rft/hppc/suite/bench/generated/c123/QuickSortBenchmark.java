package bench.generated.c123;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.function.IntBinaryOperator;
import java.util.Arrays;

import com.carrotsearch.hppc.sorting.QuickSort;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class QuickSortBenchmark {

    private int[] inputData;
    private int[] arrayCopy;
    private IntBinaryOperator comparator;

    // Fixed payload for testing
    private static final int[] FIXED_INPUT = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20};
    private static final IntBinaryOperator COMPARATOR_INT = (i, j) -> Integer.compare(i, j);
    private static final IntBinaryOperator COMPARATOR_DESC = (i, j) -> Integer.compare(j, i);

    @Setup
    public void setup() {
        // Initialize input data for sorting
        this.inputData = Arrays.copyOf(FIXED_INPUT, FIXED_INPUT.length);
        this.arrayCopy = Arrays.copyOf(FIXED_INPUT, FIXED_INPUT.length);
        this.comparator = COMPARATOR_INT;
    }

    @Benchmark
    public void testSortFullArray(Blackhole bh) {
        // Test the public static sort(int[] array, IntBinaryOperator comparator) method
        QuickSort.sort(this.arrayCopy, this.comparator);
        bh.consume(this.arrayCopy);
    }

    @Benchmark
    public void testSortSubRange(Blackhole bh) {
        // Test the public static sort(int[] array, int fromIndex, int toIndex, IntBinaryOperator comparator) method
        int fromIndex = 2;
        int toIndex = 10;
        QuickSort.sort(this.arrayCopy, fromIndex, toIndex, this.comparator);
        bh.consume(this.arrayCopy);
    }

    @Benchmark
    public void testSortWithDescendingComparator(Blackhole bh) {
        // Test sorting with a descending comparator
        QuickSort.sort(this.arrayCopy, COMPARATOR_DESC);
        bh.consume(this.arrayCopy);
    }

    @Benchmark
    public void testSortWithDifferentData(Blackhole bh) {
        // Test with a different set of data
        int[] data = {50, 10, 30, 20, 40, 5, 15, 25, 35, 45};
        int[] copy = Arrays.copyOf(data, data.length);
        IntBinaryOperator customComparator = (i, j) -> Integer.compare(data[i], data[j]);
        QuickSort.sort(copy, customComparator);
        bh.consume(copy);
    }

    @Benchmark
    public void testSortSmallRange(Blackhole bh) {
        // Test sorting a small range, potentially hitting the INSERTION_SORT_THRESHOLD
        int[] smallData = {5, 1, 4, 2, 3};
        int[] copy = Arrays.copyOf(smallData, smallData.length);
        IntBinaryOperator comparator = (i, j) -> Integer.compare(i, j);
        QuickSort.sort(copy, 0, 5, comparator);
        bh.consume(copy);
    }

    @Benchmark
    public void testSortRangeNearThreshold(Blackhole bh) {
        // Test a range size near the INSERTION_SORT_THRESHOLD (16)
        int[] data = new int[32];
        for (int i = 0; i < 32; i++) {
            data[i] = i;
        }
        int[] copy = Arrays.copyOf(data, data.length);
        IntBinaryOperator comparator = (i, j) -> Integer.compare(i, j);
        QuickSort.sort(copy, 0, 32, comparator);
        bh.consume(copy);
    }
}
