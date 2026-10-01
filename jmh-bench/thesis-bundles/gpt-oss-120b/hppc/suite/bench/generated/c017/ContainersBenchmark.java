package bench.generated.c017;

import org.openjdk.jmh.annotations.*;
import java.util.concurrent.TimeUnit;
import com.carrotsearch.hppc.Containers;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ContainersBenchmark {

    @Setup(Level.Trial)
    public void setUp() {
        // No mutable state required for Containers.randomSeed64()
    }

    @Benchmark
    public long randomSeed64() {
        return Containers.randomSeed64();
    }
}
