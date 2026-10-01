package bench.generated.c006;

import org.fastfilter.bloom.count.SuccinctCountingBloom;
import org.fastfilter.utils.Hash;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class SuccinctCountingBloomBenchmark {

    private SuccinctCountingBloom filter;
    private long[] keys;
    private final double bitsPerKey = 10.0;
    private final int keyCount = 10000;

    @Setup
    public void setup() {
        // 1. Generate a fixed set of keys for the structure
        Random random = new Random(42); // Fixed seed for reproducibility
        keys = new long[keyCount];
        for (int i = 0; i < keyCount; i++) {
            keys[i] = random.nextLong();
        }

        // 2. Construct the SuccinctCountingBloom filter
        // This is the most expensive operation and should be done once per trial.
        filter = SuccinctCountingBloom.construct(keys, bitsPerKey);
    }

    @Benchmark
    public void constructionTime(Blackhole bh) {
        // Re-construct the filter to measure construction time specifically.
        // We use a fresh set of keys for this specific measurement.
        Random random = new Random(42);
        long[] tempKeys = new long[keyCount];
        for (int i = 0; i < keyCount; i++) {
            tempKeys[i] = random.nextLong();
        }
        SuccinctCountingBloom tempFilter = SuccinctCountingBloom.construct(tempKeys, bitsPerKey);
        bh.consume(tempFilter);
    }

    @Benchmark
    public void addOperation(Blackhole bh) {
        // Add a key that is likely already in the set (to test the internal logic)
        long keyToAdd = keys[0];
        filter.add(keyToAdd);
        bh.consume(null);
    }

    @Benchmark
    public void removeOperation(Blackhole bh) {
        // Remove a key that was added during setup
        long keyToRemove = keys[1];
        filter.remove(keyToRemove);
        bh.consume(null);
    }

    @Benchmark
    public void mayContainHit(Blackhole bh) {
        // Test containment for a key known to be present
        long keyToTest = keys[500];
        boolean result = filter.mayContain(keyToTest);
        bh.consume(result);
    }

    @Benchmark
    public void mayContainMiss(Blackhole bh) {
        // Test containment for a key guaranteed not to be present
        long randomKey = new Random(100).nextLong();
        boolean result = filter.mayContain(randomKey);
        bh.consume(result);
    }

    @Benchmark
    public void cardinality(Blackhole bh) {
        long count = filter.cardinality();
        bh.consume(count);
    }
}
