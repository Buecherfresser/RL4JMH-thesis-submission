package bench.generated.c015;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.fastfilter.gcs.Sort;
import java.util.Random;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class SortBenchmark {

    private static final int ARRAY_SIZE = 4096;
    private Random random = new Random(42);

    // Input array for full sort
    private long[] dataFull;
    // Input array for subarray sort (needs to be larger than ARRAY_SIZE)
    private long[] dataSubarray;

    @Setup(Level.Trial)
    public void setupTrial() {
        // Initialize the base arrays once per trial
        dataFull = new long[ARRAY_SIZE];
        dataSubarray = new long[ARRAY_SIZE + 1024];
    }

    /**
     * Generates a fresh, unsorted array of longs.
     * This method is called per invocation because Sort.sortUnsigned is destructive.
     */
    @Setup(Level.Invocation)
    public void setupInvocation() {
        // Re-initialize dataFull for the current invocation
        for (int i = 0; i < ARRAY_SIZE; i++) {
            dataFull[i] = random.nextLong();
        }
        // Re-initialize dataSubarray for the current invocation
        for (int i = 0; i < dataSubarray.length; i++) {
            dataSubarray[i] = random.nextLong();
        }
    }

    /**
     * Benchmarks the full array unsigned sort: sortUnsigned(long[] data).
     */
    @Benchmark
    public void benchmarkFullArraySort(Blackhole bh) {
        // dataFull is guaranteed to be unsorted by @Setup(Level.Invocation)
        Sort.sortUnsigned(dataFull);
        bh.consume(dataFull);
    }

    /**
     * Benchmarks the subarray unsigned sort: sortUnsigned(long[] data, int offset, int len).
     * We sort a segment of size ARRAY_SIZE starting at offset 1024.
     */
    @Benchmark
    public void benchmarkSubarraySort(Blackhole bh) {
        int offset = ARRAY_SIZE / 2; // Start in the middle
        int len = ARRAY_SIZE / 2;    // Sort half the array
        
        // dataSubarray is guaranteed to be unsorted by @Setup(Level.Invocation)
        Sort.sortUnsigned(dataSubarray, offset, len);
        bh.consume(dataSubarray);
    }
}
