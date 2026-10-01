package bench.generated.c013;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.Random;
import org.fastfilter.gcs.GolombCompressedSet;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1) @Warmup(iterations = 1, time = 1) @Measurement(iterations = 3, time = 1)
public class GolombCompressedSetBenchmark {

    private long[] keys;
    private GolombCompressedSet set;
    private long testKeyPresent;
    private long testKeyAbsent;

    private final int KEY_COUNT = 1000;
    private final int FINGERPRINT_BITS = 10;

    @Setup(Level.Trial)
    public void setup() {
        // 1. Generate input keys
        Random random = new Random(42); // Fixed seed for reproducibility
        keys = new long[KEY_COUNT];
        for (int i = 0; i < KEY_COUNT; i++) {
            keys[i] = random.nextLong();
        }

        // 2. Construct the set once for lookup benchmarks
        // Note: Construction is expensive, so we do it here for lookup tests.
        set = GolombCompressedSet.construct(keys, FINGERPRINT_BITS);

        // 3. Generate test keys
        // We use keys that are guaranteed to be in the set (from the input array)
        testKeyPresent = keys[KEY_COUNT / 2];
        
        // Generate a key that is highly unlikely to be in the set
        // (A random long that was not part of the input array)
        testKeyAbsent = random.nextLong();
    }

    /**
     * Benchmarks the construction time of the GolombCompressedSet.
     * Since construction is static and expensive, we must perform it inside the benchmark
     * loop, using the pre-generated keys.
     */
    @Benchmark
    public GolombCompressedSet benchmarkConstruction(Blackhole bh) {
        // Reconstruct the set for each invocation to measure the full cost
        GolombCompressedSet result = GolombCompressedSet.construct(keys, FINGERPRINT_BITS);
        bh.consume(result);
        return result;
    }

    /**
     * Benchmarks the lookup time for a key known to be present (Hit).
     */
    @Benchmark
    public boolean benchmarkLookupHit(Blackhole bh) {
        boolean result = set.mayContain(testKeyPresent);
        bh.consume(result);
        return result;
    }

    /**
     * Benchmarks the lookup time for a key known to be absent (Miss).
     */
    @Benchmark
    public boolean benchmarkLookupMiss(Blackhole bh) {
        boolean result = set.mayContain(testKeyAbsent);
        bh.consume(result);
        return result;
    }
}
