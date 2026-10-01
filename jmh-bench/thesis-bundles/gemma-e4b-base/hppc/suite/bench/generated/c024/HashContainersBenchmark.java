package bench.generated.c024;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import com.carrotsearch.hppc.HashContainers;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class HashContainersBenchmark {

    private double defaultLoadFactor;
    private double minLoadFactor;
    private double maxLoadFactor;

    @Setup(Level.Trial)
    public void setup() {
        // Use public constants from HashContainers
        defaultLoadFactor = HashContainers.DEFAULT_LOAD_FACTOR;
        minLoadFactor = HashContainers.MIN_LOAD_FACTOR;
        maxLoadFactor = HashContainers.MAX_LOAD_FACTOR;
    }

    @Benchmark
    public void testMaxElements_DefaultLoadFactor(Blackhole bh) {
        int result = HashContainers.maxElements(defaultLoadFactor);
        bh.consume(result);
    }

    @Benchmark
    public void testMaxElements_MinLoadFactor(Blackhole bh) {
        int result = HashContainers.maxElements(minLoadFactor);
        bh.consume(result);
    }

    @Benchmark
    public void testMaxElements_MaxLoadFactor(Blackhole bh) {
        int result = HashContainers.maxElements(maxLoadFactor);
        bh.consume(result);
    }
}
