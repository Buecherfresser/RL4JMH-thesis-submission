package bench.generated.c017;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import com.carrotsearch.hppc.Containers;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ContainersBenchmark {

    // Since Containers.randomSeed64() is a static method that relies on system state
    // and does not mutate state, we do not need complex @State fields for input setup.

    @Benchmark
    public void testRandomSeedGeneration(Blackhole bh) {
        long seed = Containers.randomSeed64();
        bh.consume(seed);
    }
}
