package bench.generated.c001;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.Random;
import org.fastfilter.bloom.BlockedBloom;
import org.fastfilter.utils.Hash;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class BlockedBloomBenchmark {

    private long[] keys;
    private BlockedBloom bloomFilter;
    private long keyToAdd;
    private long keyToContain;

    private static final int KEY_COUNT = 1000;
    private static final int BITS_PER_KEY = 11;

    @Setup(Level.Trial)
    public void setup() {
        // 1. Generate input keys
        keys = new long[KEY_COUNT];
        Random random = new Random(42); // Fixed seed for reproducibility
        for (int i = 0; i < KEY_COUNT; i++) {
            keys[i] = random.nextLong();
        }

        // 2. Initialize the filter (Construction)
        // This is a heavy setup operation, done once per trial.
        bloomFilter = BlockedBloom.construct(keys, BITS_PER_KEY);

        // 3. Select keys for single operations
        keyToAdd = keys[0];
        keyToContain = keys[KEY_COUNT / 2];
    }

    @Benchmark
    public void constructBenchmark(Blackhole bh) {
        // Re-construct the filter using the pre-generated keys.
        // This measures the cost of the construction process itself.
        BlockedBloom newFilter = BlockedBloom.construct(keys, BITS_PER_KEY);
        bh.consume(newFilter);
    }

    @Benchmark
    public void addBenchmark(Blackhole bh) {
        // Add a single key to the existing filter instance.
        // Note: This mutates the state (bloomFilter).
        bloomFilter.add(keyToAdd);
        bh.consume(bloomFilter.getBitCount()); // Consume a result to prevent dead code elimination
    }

    @Benchmark
    public void mayContainBenchmark(Blackhole bh) {
        // Check for containment using the existing filter instance.
        boolean result = bloomFilter.mayContain(keyToContain);
        bh.consume(result);
    }

    @Benchmark
    public void getBitCountBenchmark(Blackhole bh) {
        // Measure the cost of retrieving metadata.
        long count = bloomFilter.getBitCount();
        bh.consume(count);
    }
}
