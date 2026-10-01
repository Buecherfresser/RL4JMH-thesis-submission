package bench.generated.c020;

import org.fastfilter.xor.XorFuse8;
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
public class XorFuse8Benchmark {

    private XorFuse8 filter;
    private long[] keys;
    private long[] lookupKeys;
    private static final int KEY_COUNT = 50000;
    private static final Random RANDOM = new Random(42);

    @Setup(Level.Trial)
    public void setupTrial() {
        // 1. Generate keys for construction
        keys = new long[KEY_COUNT];
        for (int i = 0; i < KEY_COUNT; i++) {
            keys[i] = RANDOM.nextLong();
        }

        // 2. Construct the filter (Expensive operation, done once per trial)
        System.out.println("Constructing XorFuse8 filter...");
        filter = XorFuse8.construct(keys);
        System.out.println("Construction complete. Bit Count: " + filter.getBitCount());

        // 3. Generate keys for lookups (can be reused across measurements)
        lookupKeys = new long[KEY_COUNT];
        for (int i = 0; i < KEY_COUNT; i++) {
            lookupKeys[i] = RANDOM.nextLong();
        }
    }

    @Benchmark
    public void benchmarkConstruction(Blackhole bh) {
        // Re-construct the filter to measure construction time
        XorFuse8 tempFilter = XorFuse8.construct(keys);
        bh.consume(tempFilter);
    }

    @Benchmark
    public void benchmarkMayContain_True(Blackhole bh) {
        // Test a key known to be in the set (using one of the setup keys)
        long key = keys[0];
        boolean result = filter.mayContain(key);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkMayContain_False(Blackhole bh) {
        // Test a key known not to be in the set (generate a random key unlikely to be present)
        long randomKey = RANDOM.nextLong();
        boolean result = filter.mayContain(randomKey);
        bh.consume(result);
    }
}
