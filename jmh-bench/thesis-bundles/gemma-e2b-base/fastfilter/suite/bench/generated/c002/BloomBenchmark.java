package bench.generated.c002;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.fastfilter.bloom.Bloom;
import org.fastfilter.utils.Hash;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class BloomBenchmark {

    private Bloom bloomFilter;
    private List<Long> keysToInsert;
    private List<Long> keysToTest;
    private Random random;

    // Configuration constants
    private static final int NUM_KEYS = 100000;
    private static final double BITS_PER_KEY = 10.0;

    @Setup
    public void setup() {
        random = new Random(42); // Fixed seed for reproducibility
        keysToInsert = new ArrayList<>(NUM_KEYS);
        keysToTest = new ArrayList<>(NUM_KEYS);

        // 1. Generate keys for insertion
        for (int i = 0; i < NUM_KEYS; i++) {
            keysToInsert.add(random.nextLong());
        }

        // 2. Construct the Bloom filter using the keys
        // This measures the construction time.
        bloomFilter = Bloom.construct(keysToInsert.stream().mapToLong(Long::longValue).toArray(), BITS_PER_KEY);
    }

    @Benchmark
    public void testMayContainPresent(Blackhole bh) {
        // Test a key known to be present (from the insertion set)
        long key = keysToInsert.get(0);
        boolean result = bloomFilter.mayContain(key);
        bh.consume(result);
    }

    @Benchmark
    public void testMayContainAbsent(Blackhole bh) {
        // Test a key guaranteed not to be present (generate a new random key)
        long key = random.nextLong();
        boolean result = bloomFilter.mayContain(key);
        bh.consume(result);
    }

    @Benchmark
    public void testAddKey(Blackhole bh) {
        // Test adding a new key
        long key = random.nextLong();
        bloomFilter.add(key);
        // Consume nothing, as add is void
    }

    @Benchmark
    public void testGetBitCount(Blackhole bh) {
        // Test reading the bit count
        long count = bloomFilter.getBitCount();
        bh.consume(count);
    }
}
