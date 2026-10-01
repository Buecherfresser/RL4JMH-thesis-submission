package bench.generated.c020;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.Random;
import org.fastfilter.xor.XorFuse8;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class XorFuse8Benchmark {

    private long[] keys;
    private XorFuse8 filter;
    private long keyIn;
    private long keyOut;

    private static final int KEY_COUNT = 10000;
    private static final Random RANDOM = new Random(42);

    @Setup(Level.Trial)
    public void setup() {
        // 1. Generate input keys
        keys = new long[KEY_COUNT];
        for (int i = 0; i < KEY_COUNT; i++) {
            keys[i] = RANDOM.nextLong();
        }

        // 2. Construct the filter once for lookup benchmarks
        // Note: Construction is heavy, so we do it in setup for lookup tests.
        // We will have a separate benchmark to measure construction time.
        filter = XorFuse8.construct(keys);

        // 3. Prepare test keys
        // Key known to be in the set (first key)
        keyIn = keys[0];
        
        // Key known not to be in the set (a random long unlikely to be in the set)
        keyOut = RANDOM.nextLong();
    }

    /**
     * Measures the time taken to construct the XorFuse8 filter.
     * This is a heavy operation and should be run separately from lookup tests.
     */
    @Benchmark
    public void benchmarkConstruction(Blackhole bh) {
        // We must create a new filter instance in every invocation to measure the full cost.
        XorFuse8 newFilter = XorFuse8.construct(keys);
        bh.consume(newFilter);
    }

    /**
     * Measures the time taken for a successful lookup (key is present).
     */
    @Benchmark
    public boolean benchmarkLookupHit(Blackhole bh) {
        boolean result = filter.mayContain(keyIn);
        bh.consume(result);
        return result;
    }

    /**
     * Measures the time taken for a failed lookup (key is absent).
     */
    @Benchmark
    public boolean benchmarkLookupMiss(Blackhole bh) {
        boolean result = filter.mayContain(keyOut);
        bh.consume(result);
        return result;
    }
}
