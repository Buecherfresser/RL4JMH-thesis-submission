package bench.generated.c005;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.Random;
import org.fastfilter.bloom.count.SuccinctCountingBlockedBloomRanked;
import org.fastfilter.utils.Hash;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class SuccinctCountingBlockedBloomRankedBenchmark {

    private long[] keys;
    private int bitsPerKey = 16;
    private long testKey;

    // State for read-only benchmarks (Trial scope)
    private SuccinctCountingBlockedBloomRanked readOnlyFilter;

    // State for mutation benchmarks (Invocation scope)
    private SuccinctCountingBlockedBloomRanked mutableFilter;

    @Setup(Level.Trial)
    public void setupTrial() {
        // Generate a fixed set of keys for testing
        keys = new long[1000];
        Random random = new Random(42); // Fixed seed for reproducibility
        for (int i = 0; i < keys.length; i++) {
            keys[i] = random.nextLong();
        }
        
        // Initialize the read-only filter once per trial
        readOnlyFilter = SuccinctCountingBlockedBloomRanked.construct(keys, bitsPerKey);
    }

    @Setup(Level.Invocation)
    public void setupInvocation() {
        // Generate a single test key for mutation/lookup tests
        testKey = Hash.hash64(new Random(42).nextLong(), 1);

        // Create a fresh filter instance for mutation tests to prevent state accumulation
        mutableFilter = SuccinctCountingBlockedBloomRanked.construct(keys, bitsPerKey);
    }

    @Benchmark
    public void benchmarkConstruction(Blackhole bh) {
        // Measure the time taken to construct the filter from scratch
        SuccinctCountingBlockedBloomRanked filter = SuccinctCountingBlockedBloomRanked.construct(keys, bitsPerKey);
        bh.consume(filter);
    }

    @Benchmark
    public void benchmarkAdd(Blackhole bh) {
        // Measure the time taken to add a single key
        mutableFilter.add(testKey);
        bh.consume(mutableFilter);
    }

    @Benchmark
    public void benchmarkRemove(Blackhole bh) {
        // Measure the time taken to remove a single key
        mutableFilter.remove(testKey);
        bh.consume(mutableFilter);
    }

    @Benchmark
    public void benchmarkMayContain(Blackhole bh) {
        // Measure the time taken for lookup (using the pre-built readOnlyFilter)
        boolean result = readOnlyFilter.mayContain(testKey);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkCardinality(Blackhole bh) {
        // Measure the time taken to calculate cardinality
        long cardinality = readOnlyFilter.cardinality();
        bh.consume(cardinality);
    }

    @Benchmark
    public void benchmarkGetBitCount(Blackhole bh) {
        // Measure the time taken to get the bit count
        long bitCount = readOnlyFilter.getBitCount();
        bh.consume(bitCount);
    }
}
