package bench.generated.c018;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.fastfilter.xor.Xor16;
import java.util.Random;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Xor16Benchmark {

    private long[] keys;
    private Xor16 filter;
    private long testKeyHit;
    private long testKeyMiss;

    @Setup(Level.Trial)
    public void setup() {
        // 1. Generate input keys
        int keyCount = 1000;
        keys = new long[keyCount];
        Random random = new Random(42); // Fixed seed for reproducibility

        for (int i = 0; i < keyCount; i++) {
            keys[i] = random.nextLong();
        }

        // 2. Select test keys
        // Key that is definitely in the set (first key)
        testKeyHit = keys[0];

        // Key that is highly unlikely to be in the set (a random long far from the set)
        // Since the keys are random, we pick a key that is unlikely to collide with the set.
        testKeyMiss = random.nextLong();
    }

    @Benchmark
    public void benchmarkConstruction(Blackhole bh) {
        // Constructing the filter is a heavy operation, we measure the time taken.
        Xor16 constructedFilter = Xor16.construct(keys);
        bh.consume(constructedFilter);
    }

    @Benchmark
    public void benchmarkLookupHit(Blackhole bh) {
        // Test lookup for a key known to be in the filter
        boolean result = filter.mayContain(testKeyHit);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkLookupMiss(Blackhole bh) {
        // Test lookup for a key known not to be in the filter
        boolean result = filter.mayContain(testKeyMiss);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkGetBitCount(Blackhole bh) {
        // Test the getter method
        long bitCount = filter.getBitCount();
        bh.consume(bitCount);
    }
}
