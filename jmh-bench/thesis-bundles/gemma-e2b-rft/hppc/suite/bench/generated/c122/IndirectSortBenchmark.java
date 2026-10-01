package bench.generated.c122;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.function.IntBinaryOperator;
import java.util.concurrent.TimeUnit;

import com.carrotsearch.hppc.sorting.IndirectSort;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class IndirectSortBenchmark {

    // State fields for inputs built in @Setup
    private int[] inputOrderArray;
    private int start;
    private int length;
    private IntBinaryOperator comparator;

    @Setup
    public void setup() {
        // Define a reasonably large size for the array to ensure measurable time
        this.length = 100000;
        this.start = 0;

        // Build the input array: [0, 1, 2, ..., length - 1]
        this.inputOrderArray = new int[length];
        for (int i = 0; i < length; i++) {
            this.inputOrderArray[i] = i;
        }

        // Define a simple comparator (identity comparison)
        this.comparator = (a, b) -> Integer.compare(a, b);
    }

    @Benchmark
    public void benchmarkMergesortArrayCopy(Blackhole bh) {
        // Tests: mergesort(int[] orderArray, IntBinaryOperator comparator)
        int[] result = IndirectSort.mergesort(this.inputOrderArray, this.comparator);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkMergesortRange(Blackhole bh) {
        // Tests: mergesort(int start, int length, IntBinaryOperator comparator)
        int[] result = IndirectSort.mergesort(this.start, this.length, this.comparator);
        bh.consume(result);
    }
}
