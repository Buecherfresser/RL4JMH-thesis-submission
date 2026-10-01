package bench.generated.c106;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.scale.Scales;
import org.decimal4j.scale.ScaleMetrics;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ScalesBenchmark {

    private int[] scales;
    private long[] factors;
    private int index;

    @Setup(Level.Trial)
    public void setup() {
        scales = new int[19];
        factors = new long[19];
        for (int i = 0; i <= 18; i++) {
            scales[i] = i;
            long factor = 1L;
            for (int j = 0; j < i; j++) {
                factor *= 10L;
            }
            factors[i] = factor;
        }
        index = 0;
    }

    private int nextScale() {
        int s = scales[index];
        index = (index + 1) % scales.length;
        return s;
    }

    private long nextFactor() {
        long f = factors[index];
        index = (index + 1) % factors.length;
        return f;
    }

    @Benchmark
    public ScaleMetrics benchmarkGetScaleMetrics() {
        return Scales.getScaleMetrics(nextScale());
    }

    @Benchmark
    public ScaleMetrics benchmarkFindByScaleFactor() {
        return Scales.findByScaleFactor(nextFactor());
    }
}
