package bench;

import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import java.util.Random;
import org.openjdk.jmh.infra.Blackhole;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class MeterBenchmark {

    @Param({"4096"})
    public int size;

    private int[] xs;

    @Setup
    public void setup() {
        Random rng = new Random(42L);
        xs = new int[size];
        for (int i = 0; i < size; i++) xs[i] = rng.nextInt() | (rng.nextInt(2));
    }

    @Benchmark
    public void count(Blackhole bh) {
        bh.consume(Meter.count(xs));
    }
}
