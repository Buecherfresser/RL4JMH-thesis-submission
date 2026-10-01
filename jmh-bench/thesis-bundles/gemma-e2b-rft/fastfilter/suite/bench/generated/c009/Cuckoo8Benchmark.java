package bench.generated.c009;

import org.fastfilter.cuckoo.Cuckoo8;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Cuckoo8Benchmark {

    private Cuckoo8 filter;
    private long[] keys;
    private long[] testKeys;
    private final Random random = new Random(42); // Fixed seed for reproducibility

    // Setup phase: Build the filter and the key set once per trial
    @Setup
    public void setup() {
        // 1. Generate a set of keys for initial construction
        int initialKeyCount = 10000;
        keys = new long[initialKeyCount];
        for (int i = 0; i < initialKeyCount; i++) {
            keys[i] = random.nextLong();
        }

        // 2. Construct the Cuckoo8 filter
        // We use the static construct method provided by the library
        this.filter = Cuckoo8.construct(keys);

        // 3. Prepare a set of test keys for lookup/insertion benchmarks
        int testKeyCount = 10000;
        testKeys = new long[testKeyCount];
        for (int i = 0; i < testKeyCount; i++) {
            testKeys[i] = random.nextLong();
        }
    }

    @Benchmark
    public void mayContain_Hit(Blackhole bh) {
        long key = testKeys[random.nextInt(testKeys.length)];
        boolean result = filter.mayContain(key);
        bh.consume(result);
    }

    @Benchmark
    public void mayContain_Miss(Blackhole bh) {
        // Pick a key that is highly unlikely to be present (or just a random one)
        long key = random.nextLong();
        boolean result = filter.mayContain(key);
        bh.consume(result);
    }

    @Benchmark
    public void insert(Blackhole bh) {
        long key = testKeys[random.nextInt(testKeys.length)];
        filter.insert(key);
        // Void method, consume nothing specific, or consume a dummy if needed, 
        // but since it's void, we just ensure it runs.
    }

    @Benchmark
    public void getBitCount(Blackhole bh) {
        long count = filter.getBitCount();
        bh.consume(count);
    }
}
