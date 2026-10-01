package bench.generated.c007;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.fastfilter.Filter;
import org.fastfilter.bloom.count.SuccinctCountingBloomRanked;
import org.fastfilter.utils.Hash;
import java.util.Random;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class SuccinctCountingBloomRankedBenchmark {

    private SuccinctCountingBloomRanked filter;
    private long[] keys;
    private final int BITS_PER_KEY = 16;
    private final int KEY_COUNT = 100000;

    @Setup
    public void setup() {
        // 1. Generate a fixed set of unique keys for the filter
        Random random = new Random(42); // Fixed seed for reproducibility
        keys = new long[KEY_COUNT];
        for (int i = 0; i < KEY_COUNT; i++) {
            keys[i] = random.nextLong();
        }

        // 2. Construct the filter using the generated keys
        // Note: construct performs the initial population of the filter.
        filter = SuccinctCountingBloomRanked.construct(keys, BITS_PER_KEY);
    }

    @Benchmark
    public void testMayContain(Blackhole bh) {
        // Test containment for a key known to be in the set (e.g., the first key)
        long keyToCheck = keys[0];
        boolean result = filter.mayContain(keyToCheck);
        bh.consume(result);
    }

    @Benchmark
    public void testMayContainAbsent(Blackhole bh) {
        // Test containment for a key guaranteed not to be in the set (by generating a new random key)
        Random random = new Random(101); // Different seed for a different key
        long absentKey = random.nextLong();
        boolean result = filter.mayContain(absentKey);
        bh.consume(result);
    }

    @Benchmark
    public void testAddOperation(Blackhole bh) {
        // Test adding a key that is likely not already present (or cycling through keys)
        long keyToAdd = keys[0];
        filter.add(keyToAdd);
        bh.consume(null);
    }

    @Benchmark
    public void testRemoveOperation(Blackhole bh) {
        // Test removing a key that was added during setup
        long keyToRemove = keys[1];
        filter.remove(keyToRemove);
        bh.consume(null);
    }

    @Benchmark
    public void testCardinality(Blackhole bh) {
        // Measure the cost of calculating the total cardinality
        long count = filter.cardinality();
        bh.consume(count);
    }

    @Benchmark
    public void testGetBitCount(Blackhole bh) {
        // Measure the cost of getting the space used
        long bitCount = filter.getBitCount();
        bh.consume(bitCount);
    }
}
