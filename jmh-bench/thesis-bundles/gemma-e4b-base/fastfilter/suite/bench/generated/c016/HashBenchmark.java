package bench.generated.c016;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.fastfilter.utils.Hash;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class HashBenchmark {

    private long inputX;
    private long inputSeed;
    private int inputHash;
    private int inputN;

    @Setup
    public void setup() {
        // Setup inputs for hash64
        inputX = 123456789012345L;
        inputSeed = 9876543210987L;

        // Setup inputs for reduce
        inputHash = 0xDEADBEEF;
        inputN = 1024;
    }

    @Benchmark
    public void benchmarkHash64_Standard(Blackhole bh) {
        long result = Hash.hash64(inputX, inputSeed);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkHash64_ZeroInput(Blackhole bh) {
        long result = Hash.hash64(0L, inputSeed);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkHash64_ZeroSeed(Blackhole bh) {
        long result = Hash.hash64(inputX, 0L);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkRandomSeed(Blackhole bh) {
        long result = Hash.randomSeed();
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkReduce_Standard(Blackhole bh) {
        int result = Hash.reduce(inputHash, inputN);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkReduce_SmallN(Blackhole bh) {
        int result = Hash.reduce(inputHash, 10);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkReduce_LargeN(Blackhole bh) {
        int result = Hash.reduce(inputHash, 2000000);
        bh.consume(result);
    }
}
