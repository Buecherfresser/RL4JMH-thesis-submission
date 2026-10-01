package bench.generated.c021;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.fastfilter.Filter;
import org.fastfilter.xor.XorSimple;
import org.fastfilter.utils.Hash;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class XorSimpleBenchmark {

    private XorSimple filter;
    private long[] constructionKeys;
    private long[] lookupKeys;

    // Constants for input size
    private static final int KEY_COUNT = 10000;

    @Setup(Level.Trial)
    public void setup() {
        // 1. Generate keys for construction
        constructionKeys = new long[KEY_COUNT];
        Random random = new Random(42); // Fixed seed for reproducibility
        for (int i = 0; i < KEY_COUNT; i++) {
            constructionKeys[i] = random.nextLong();
        }

        // 2. Construct the filter instance
        filter = XorSimple.construct(constructionKeys);

        // 3. Generate keys for lookups (a mix of existing and non-existing keys)
        lookupKeys = new long[KEY_COUNT];
        Random lookupRandom = new Random(101);
        for (int i = 0; i < KEY_COUNT; i++) {
            // Use keys from the construction set for hits, and random keys for misses
            if (i % 3 == 0) {
                lookupKeys[i] = constructionKeys[i]; // Hit case
            } else {
                lookupKeys[i] = lookupRandom.nextLong(); // Miss case
            }
        }
    }

    @Benchmark
    public void benchmarkConstruction(Blackhole bh) {
        // Re-instantiate the filter to measure construction time accurately
        XorSimple tempFilter = XorSimple.construct(constructionKeys);
        bh.consume(tempFilter);
    }

    @Benchmark
    public void benchmarkMayContainHit(Blackhole bh) {
        // Test a key known to be in the set (using the first key from the construction set)
        long key = constructionKeys[0];
        boolean result = filter.mayContain(key);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkMayContainMiss(Blackhole bh) {
        // Test a key known to be absent (using a random key from the lookup set)
        long key = lookupKeys[1]; // Index 1 is likely a miss
        boolean result = filter.mayContain(key);
        bh.consume(result);
    }
}
