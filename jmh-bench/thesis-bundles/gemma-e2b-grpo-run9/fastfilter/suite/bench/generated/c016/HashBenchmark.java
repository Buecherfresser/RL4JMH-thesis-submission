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

    @Benchmark
    public void benchmarkHash64(Blackhole bh) {
        // Test hash64 with arbitrary inputs and seeds.
        long x = 123456789012345L;
        long seed = 9876543210L;
        long result = Hash.hash64(x, seed);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkReduce(Blackhole bh) {
        // Test reduce function.
        int hash = 0xDEADBEEF;
        int n = 1000000;
        int result = Hash.reduce(hash, n);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkRandomSeed(Blackhole bh) {
        // Test randomSeed (reads static state).
        long result = Hash.randomSeed();
        bh.consume(result);
    }
}
