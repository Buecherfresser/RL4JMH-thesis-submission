package bench.generated.c017;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import com.carrotsearch.hppc.Containers;
import java.security.AccessController;
import java.util.logging.Level;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ContainersBenchmark {

    /**
     * Benchmarks the static method Containers.randomSeed64(), which calculates
     * a pseudo-random seed based on system properties or time/identity hash codes.
     */
    @Benchmark
    public long benchmarkRandomSeed64(Blackhole bh) {
        long seed = Containers.randomSeed64();
        bh.consume(seed);
        return seed;
    }
}
