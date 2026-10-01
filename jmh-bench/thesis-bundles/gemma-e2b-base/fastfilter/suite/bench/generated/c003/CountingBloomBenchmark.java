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
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class CountingBloomBenchmark {

    private CountingBloom countingBloom;
    private long[] keys;
    private final int numKeys = 10000;
    private final double bitsPerKey = 8.0;

    @Setup
    public void setup() {
        // 1. Generate fixed keys for the Bloom filter construction
        Random random = new Random(42); // Fixed seed for reproducibility
        keys = new long[numKeys];
        for (int i = 0; i < numKeys; i++) {
            keys[i] = random.nextLong();
        }

        // 2. Construct the CountingBloom filter
        countingBloom = CountingBloom.construct(keys, bitsPerKey);
    }

    // --- Construction/Initialization Benchmarks ---

    @Benchmark
    public void benchmarkConstruction() {
        // This benchmark measures the cost of constructing the CountingBloom filter
        CountingBloom.construct(keys, bitsPerKey);
    }

    @Benchmark
    public long benchmarkCardinality() {
        // Measures the cost of calculating the cardinality
        return countingBloom.cardinality();
    }

    // --- Add/Remove Benchmarks ---

    @Benchmark
    public void benchmarkAdd(Blackhole bh) {
        // Measures the cost of adding a key
        countingBloom.add(keys[0]);
        bh.consume(null);
    }

    @Benchmark
    public void benchmarkRemove(Blackhole bh) {
        // Measures the cost of removing a key
        countingBloom.remove(keys[0]);
        bh.consume(null);
    }

    // --- Containment Benchmarks ---

    @Benchmark
    public void benchmarkMayContain_True(Blackhole bh) {
        // Measures lookup for an existing key
        countingBloom.mayContain(keys[0]);
        bh.consume(null);
    }

    @Benchmark
    public void benchmarkMayContain_False(Blackhole bh) {
        // Measures lookup for a non-existing key
        long nonExistentKey = randomLong();
        countingBloom.mayContain(nonExistentKey);
        bh.consume(null);
    }

    // --- Helper for generating non-existent keys ---

    private long randomLong() {
        Random random = new Random(System.nanoTime());
        return random.nextLong();
    }
}
