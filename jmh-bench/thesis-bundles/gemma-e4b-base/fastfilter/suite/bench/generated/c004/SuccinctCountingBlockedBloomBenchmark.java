package bench.generated.c004;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.Random;
import org.fastfilter.bloom.count.SuccinctCountingBlockedBloom;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class SuccinctCountingBlockedBloomBenchmark {

    private static final int ENTRY_COUNT = 10000;
    private static final int BITS_PER_KEY = 16;
    private static final int KEY_COUNT = 10000;

    private long[] keys;
    private SuccinctCountingBlockedBloom filter;
    private long keyPresent;
    private long keyAbsent;

    @Setup(Level.Trial)
    public void setup() {
        // 1. Generate keys
        keys = new long[KEY_COUNT];
        Random random = new Random(42); // Fixed seed for reproducibility
        for (int i = 0; i < KEY_COUNT; i++) {
            keys[i] = random.nextLong();
        }

        // 2. Select keys for testing
        keyPresent = keys[0];
        // Generate a key highly unlikely to be in the set
        keyAbsent = random.nextLong();

        // 3. Construct the filter once for the trial
        filter = SuccinctCountingBlockedBloom.construct(keys, BITS_PER_KEY);
    }

    // --- Construction Benchmarks ---

    @Benchmark
    public SuccinctCountingBlockedBloom benchmarkConstruction() {
        // Rebuild the filter for measurement to ensure clean state
        long[] keysForConstruction = new long[KEY_COUNT];
        Random random = new Random(42);
        for (int i = 0; i < KEY_COUNT; i++) {
            keysForConstruction[i] = random.nextLong();
        }
        return SuccinctCountingBlockedBloom.construct(keysForConstruction, BITS_PER_KEY);
    }

    // --- Lookup Benchmarks ---

    @Benchmark
    public boolean benchmarkMayContain_Hit(Blackhole bh) {
        // Use the pre-populated filter instance
        boolean result = filter.mayContain(keyPresent);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public boolean benchmarkMayContain_Miss(Blackhole bh) {
        // Use the pre-populated filter instance
        boolean result = filter.mayContain(keyAbsent);
        bh.consume(result);
        return result;
    }

    // --- Mutation Benchmarks ---

    @Benchmark
    public void benchmarkAdd(Blackhole bh) {
        // Since 'add' mutates the state, we must ensure the filter is clean before the operation.
        // We rebuild the filter state for every invocation to prevent state accumulation distortion.
        SuccinctCountingBlockedBloom freshFilter = SuccinctCountingBlockedBloom.construct(keys, BITS_PER_KEY);
        freshFilter.add(keyPresent);
        bh.consume(freshFilter);
    }

    @Benchmark
    public void benchmarkRemove(Blackhole bh) {
        // Since 'remove' mutates the state, we must ensure the filter is clean before the operation.
        SuccinctCountingBlockedBloom freshFilter = SuccinctCountingBlockedBloom.construct(keys, BITS_PER_KEY);
        freshFilter.remove(keyPresent);
        bh.consume(freshFilter);
    }

    // --- Information Retrieval Benchmarks ---

    @Benchmark
    public long benchmarkCardinality(Blackhole bh) {
        // Cardinality is read-only, so we can use the pre-populated filter
        long cardinality = filter.cardinality();
        bh.consume(cardinality);
        return cardinality;
    }

    @Benchmark
    public long benchmarkBitCount(Blackhole bh) {
        // Bit count is read-only, so we can use the pre-populated filter
        long bitCount = filter.getBitCount();
        bh.consume(bitCount);
        return bitCount;
    }
}
