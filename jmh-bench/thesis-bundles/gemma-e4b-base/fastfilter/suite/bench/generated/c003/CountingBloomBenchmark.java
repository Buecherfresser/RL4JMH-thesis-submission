package bench.generated.c003;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.Random;
import org.fastfilter.bloom.count.CountingBloom;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class CountingBloomBenchmark {

    private CountingBloom bloom;
    private long[] testKeys;
    private final double BITS_PER_KEY = 16.0;
    private final int KEY_COUNT = 50000;
    private final Random random = new Random(42);

    @Setup(Level.Trial)
    public void setup() {
        // 1. Generate input keys
        testKeys = new long[KEY_COUNT];
        for (int i = 0; i < KEY_COUNT; i++) {
            testKeys[i] = random.nextLong();
        }

        // 2. Construct the CountingBloom instance once for all benchmarks
        // This instance will be used for all operations (add, remove, mayContain, etc.)
        // Note: Since add/remove modify the state, the benchmark measures the cost
        // of the operation on a filter that is already populated.
        bloom = CountingBloom.construct(testKeys, BITS_PER_KEY);
    }

    // --- Construction Benchmark ---

    @Benchmark
    public CountingBloom benchmarkConstruction() {
        // Rebuild the filter from scratch for this measurement
        long[] keys = new long[KEY_COUNT];
        for (int i = 0; i < KEY_COUNT; i++) {
            keys[i] = random.nextLong();
        }
        return CountingBloom.construct(keys, BITS_PER_KEY);
    }

    // --- Lookup Benchmarks ---

    @Benchmark
    public boolean benchmarkMayContain() {
        // Cycle through keys to ensure different lookups
        long key = testKeys[random.nextInt(KEY_COUNT)];
        return bloom.mayContain(key);
    }

    // --- Insertion Benchmarks ---

    @Benchmark
    public void benchmarkAdd() {
        // Cycle through keys. This modifies the state of 'bloom'.
        long key = testKeys[random.nextInt(KEY_COUNT)];
        bloom.add(key);
    }

    // --- Deletion Benchmarks ---

    @Benchmark
    public void benchmarkRemove() {
        // Cycle through keys. This modifies the state of 'bloom'.
        long key = testKeys[random.nextInt(KEY_COUNT)];
        bloom.remove(key);
    }

    // --- Utility Benchmarks ---

    @Benchmark
    public long benchmarkCardinality() {
        return bloom.cardinality();
    }

    @Benchmark
    public long benchmarkBitCount() {
        return bloom.getBitCount();
    }
}
