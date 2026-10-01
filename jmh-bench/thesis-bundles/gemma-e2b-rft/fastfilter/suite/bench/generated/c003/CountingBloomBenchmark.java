package bench.generated.c003;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.Random;

import org.fastfilter.bloom.count.CountingBloom;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class CountingBloomBenchmark {

    private CountingBloom countingBloom;
    private long[] keys;
    private final int numKeys = 10000;
    private final double bitsPerKey = 8.0;

    @Setup
    public void setup() {
        // 1. Generate fixed keys for the Bloom filter
        Random random = new Random(42); // Fixed seed for reproducibility
        keys = new long[numKeys];
        for (int i = 0; i < numKeys; i++) {
            keys[i] = random.nextLong();
        }

        // 2. Construct the CountingBloom filter
        // This involves adding all keys to the filter during construction
        countingBloom = CountingBloom.construct(keys, bitsPerKey);
    }

    @Benchmark
    public void testMayContain(Blackhole bh) {
        // Test membership check for a key that is definitely in the set
        long key = keys[0];
        boolean result = countingBloom.mayContain(key);
        bh.consume(result);
    }

    @Benchmark
    public void testMayContainMiss(Blackhole bh) {
        // Test membership check for a key that is definitely NOT in the set
        // Generate a key far outside the initial set range
        Random random = new Random(101);
        long missKey = random.nextLong();
        boolean result = countingBloom.mayContain(missKey);
        bh.consume(result);
    }

    @Benchmark
    public void testAdd(Blackhole bh) {
        // Test adding a key that is already in the set (to test counter increment logic)
        long key = keys[0];
        countingBloom.add(key);
        bh.consume(null);
    }

    @Benchmark
    public void testRemove(Blackhole bh) {
        // Test removing a key that is in the set
        long key = keys[0];
        countingBloom.remove(key);
        bh.consume(null);
    }

    @Benchmark
    public void testCardinality(Blackhole bh) {
        // Test calculating the total number of set bits (cardinality)
        long cardinality = countingBloom.cardinality();
        bh.consume(cardinality);
    }

    @Benchmark
    public void testConstruction(Blackhole bh) {
        // Test the static construction method itself
        // We use a fresh set of keys for this specific test to measure construction time
        Random random = new Random(101);
        long[] constructionKeys = new long[numKeys];
        for (int i = 0; i < numKeys; i++) {
            constructionKeys[i] = random.nextLong();
        }
        CountingBloom.construct(constructionKeys, bitsPerKey);
        bh.consume(null);
    }
}
