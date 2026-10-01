package bench.generated.c016;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.fastfilter.utils.Hash;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class HashBenchmark {

    // State fields for inputs
    private long testKey;
    private long testSeed;
    private int testHash;
    private int testReductionFactor;

    // Setup method to prepare inputs
    @Setup
    public void setup() {
        // Build non-final inputs
        testKey = 123456789012345L;
        testSeed = 9876543210L;
        testHash = 0xDEADBEEF;
        testReductionFactor = 1024;
    }

    // Benchmark for hash64(long x, long seed)
    @Benchmark
    public void benchmarkHash64(Blackhole bh) {
        long result = Hash.hash64(testKey, testSeed);
        bh.consume(result);
    }

    // Benchmark for reduce(int hash, int n)
    @Benchmark
    public void benchmarkReduce(Blackhole bh) {
        int result = Hash.reduce(testHash, testReductionFactor);
        bh.consume(result);
    }

    // Benchmark for randomSeed()
    @Benchmark
    public void benchmarkRandomSeed(Blackhole bh) {
        long result = Hash.randomSeed();
        bh.consume(result);
    }

    // Benchmark for setSeed(long seed)
    @Benchmark
    public void benchmarkSetSeed(Blackhole bh) {
        Hash.setSeed(testSeed);
        // Consume something to ensure the call isn't optimized away
        bh.consume(1);
    }
}
