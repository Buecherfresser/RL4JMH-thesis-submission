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

    private BlockedBloom bloomFilter;
    private long[] keys;
    private final int BITS_PER_KEY = 11;
    private final int KEY_COUNT = 10000;

    @Setup(Level.Trial)
    public void setupTrial() {
        // 1. Generate a fixed set of keys for the trial
        Random random = new Random(42); // Fixed seed for reproducibility
        keys = new long[KEY_COUNT];
        for (int i = 0; i < KEY_COUNT; i++) {
            keys[i] = random.nextLong();
        }

        // 2. Construct the BlockedBloom filter using the generated keys
        // This simulates the initial setup cost.
        bloomFilter = BlockedBloom.construct(keys, BITS_PER_KEY);
    }

    @Benchmark
    public void testMayContain_Positive(Blackhole bh) {
        // Test lookup for a key known to be in the set
        long keyToTest = keys[0];
        boolean result = bloomFilter.mayContain(keyToTest);
        bh.consume(result);
    }

    @Benchmark
    public void testMayContain_Negative(Blackhole bh) {
        // Test lookup for a key known NOT to be in the set
        // Generate a key guaranteed not to be in the initial set (by adding a large offset)
        long keyToTest = keys[KEY_COUNT * 2 + 1];
        boolean result = bloomFilter.mayContain(keyToTest);
        bh.consume(result);
    }

    @Benchmark
    public void testAdd(Blackhole bh) {
        // Test adding a new key
        long keyToAdd = keys[KEY_COUNT]; // Use a key that was not part of the initial construction set
        bloomFilter.add(keyToAdd);
    }

    @Benchmark
    public void testGetBitCount(Blackhole bh) {
        // Test reading the space used by the filter
        long count = bloomFilter.getBitCount();
        bh.consume(count);
    }
}
