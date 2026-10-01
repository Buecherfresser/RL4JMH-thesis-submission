package bench.generated.c006;

import org.fastfilter.bloom.count.SuccinctCountingBloom;
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
    private final Random random = new Random(42);

    @Setup
    public void setup() {
        // 1. Generate a fixed set of keys for the Bloom filter construction
        int keyCount = 10000;
        keys = new long[keyCount];
        for (int i = 0; i < keyCount; i++) {
            keys[i] = random.nextLong();
        }

        // 2. Construct the SuccinctCountingBloom filter
        // This is the expensive setup operation.
        filter = SuccinctCountingBloom.construct(keys, bitsPerKey);
    }

    @Benchmark
    public void testAdd() {
        long keyToAdd = random.nextLong();
        filter.add(keyToAdd);
    }

    @Benchmark
    public void testRemove() {
        // Pick a key that is likely present (e.g., from the setup keys)
        long keyToRemove = keys[random.nextInt(keys.length)];
        filter.remove(keyToRemove);
    }

    @Benchmark
    public void testMayContainHit(Blackhole bh) {
        // Pick a key that is definitely present
        long keyToTest = keys[random.nextInt(keys.length)];
        boolean result = filter.mayContain(keyToTest);
        bh.consume(result);
    }

    @Benchmark
    public void testMayContainMiss(Blackhole bh) {
        // Pick a key that is highly unlikely to be present
        long keyToTest = random.nextLong();
        boolean result = filter.mayContain(keyToTest);
        bh.consume(result);
    }

    @Benchmark
    public void testCardinality(Blackhole bh) {
        long cardinality = filter.cardinality();
        bh.consume(cardinality);
    }
}
