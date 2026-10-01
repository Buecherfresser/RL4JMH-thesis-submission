package bench.generated.c018;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import org.fastfilter.xor.Xor16;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class Xor16Benchmark {

    private Xor16 xorFilter;
    private long[] setupKeys;
    private final Random random = new Random(42);

    // Constants for setup
    private static final int KEY_COUNT = 10000;

    @Setup
    public void setup() {
        // 1. Generate a fixed set of keys for filter construction
        setupKeys = new long[KEY_COUNT];
        for (int i = 0; i < KEY_COUNT; i++) {
            setupKeys[i] = random.nextLong();
        }

        // 2. Construct the Xor16 filter once
        xorFilter = Xor16.construct(setupKeys);
    }

    @Benchmark
    public void testMayContain_Found(Blackhole bh) {
        long key = setupKeys[random.nextInt(KEY_COUNT)];
        boolean result = xorFilter.mayContain(key);
        bh.consume(result);
    }

    @Benchmark
    public void testMayContain_NotFound(Blackhole bh) {
        // Generate a key highly unlikely to be in the set (or just a random one)
        long key = random.nextLong();
        boolean result = xorFilter.mayContain(key);
        bh.consume(result);
    }

    @Benchmark
    public void testGetBitCount(Blackhole bh) {
        long count = xorFilter.getBitCount();
        bh.consume(count);
    }
}
