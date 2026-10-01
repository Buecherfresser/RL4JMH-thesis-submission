package bench.generated.c006;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.Random;
import org.fastfilter.bloom.count.SuccinctCountingBloom;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class SuccinctCountingBloomBenchmark {

    private static final double BITS_PER_KEY = 16.0;
    private static final int KEY_COUNT = 10000;
    private static final long TEST_KEY = 0xDEADBEEFCAFEF00DL;

    private long[] keys;

    // State for Construction Benchmark
    private SuccinctCountingBloom constructedBloom;

    // State for Mutation/Query Benchmarks (rebuilt per invocation)
    private SuccinctCountingBloom bloomInstance;

    @Setup(Level.Trial)
    public void setupTrial() {
        // Generate a fixed set of keys for construction
        keys = new long[KEY_COUNT];
        Random random = new Random(42); // Fixed seed for reproducibility
        for (int i = 0; i < KEY_COUNT; i++) {
            keys[i] = random.nextLong();
        }
    }

    @Setup(Level.Invocation)
    public void setupInvocation() {
        // Initialize a fresh instance for each invocation to ensure isolated measurements
        bloomInstance = SuccinctCountingBloom.construct(new long[]{TEST_KEY}, BITS_PER_KEY);
    }

    /**
     * Benchmarks the construction of the SuccinctCountingBloom from a large set of keys.
     */
    @Benchmark
    public SuccinctCountingBloom benchmarkConstruction() {
        // Since construction is expensive, we run it once per benchmark run (Trial scope)
        // We must ensure the state is initialized before running the benchmark loop.
        // We rely on the JMH harness running this method multiple times, but the setup
        // ensures the input keys are ready.
        return SuccinctCountingBloom.construct(keys, BITS_PER_KEY);
    }

    /**
     * Benchmarks adding a single key to an existing bloom instance.
     */
    @Benchmark
    public void benchmarkAddKey(Blackhole bh) {
        bloomInstance.add(TEST_KEY);
        bh.consume(bloomInstance);
    }

    /**
     * Benchmarks removing a single key from an existing bloom instance.
     */
    @Benchmark
    public void benchmarkRemoveKey(Blackhole bh) {
        bloomInstance.remove(TEST_KEY);
        bh.consume(bloomInstance);
    }

    /**
     * Benchmarks checking if a key may be contained in the bloom instance (lookup).
     */
    @Benchmark
    public boolean benchmarkMayContainKey(Blackhole bh) {
        boolean result = bloomInstance.mayContain(TEST_KEY);
        bh.consume(result);
        return result;
    }

    /**
     * Benchmarks retrieving the total number of bits used by the structure.
     */
    @Benchmark
    public long benchmarkGetBitCount(Blackhole bh) {
        long count = bloomInstance.getBitCount();
        bh.consume(count);
        return count;
    }

    /**
     * Benchmarks retrieving the cardinality (estimated number of elements) of the structure.
     */
    @Benchmark
    public long benchmarkCardinality(Blackhole bh) {
        long count = bloomInstance.cardinality();
        bh.consume(count);
        return count;
    }
}
