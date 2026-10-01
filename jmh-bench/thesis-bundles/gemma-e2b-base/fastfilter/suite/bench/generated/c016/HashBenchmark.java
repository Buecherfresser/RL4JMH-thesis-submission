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
    private long testKey1;
    private long testKey2;
    private long testSeed1;
    private long testSeed2;
    private int reductionHash;
    private int reductionN;

    private Random random;

    @Setup
    public void setup() {
        // Initialize Random for reproducible seeds
        this.random = new Random(42L);

        // Setup inputs for hash64
        this.testKey1 = 0xDEADBEEFL;
        this.testKey2 = 0xCAFEBABEFL;
        this.testSeed1 = 123456789012345L;
        this.testSeed2 = 987654321098765L;

        // Setup inputs for reduce
        this.reductionHash = 0x12345678;
        this.reductionN = 1024;
    }

    @Benchmark
    public void hash64_withSeed1(Blackhole bh) {
        long result = Hash.hash64(testKey1, testSeed1);
        bh.consume(result);
    }

    @Benchmark
    public void hash64_withSeed2(Blackhole bh) {
        long result = Hash.hash64(testKey2, testSeed2);
        bh.consume(result);
    }

    @Benchmark
    public void reduce(Blackhole bh) {
        int result = Hash.reduce(reductionHash, reductionN);
        bh.consume(result);
    }

    @Benchmark
    public void randomSeed(Blackhole bh) {
        long result = Hash.randomSeed();
        bh.consume(result);
    }
}
