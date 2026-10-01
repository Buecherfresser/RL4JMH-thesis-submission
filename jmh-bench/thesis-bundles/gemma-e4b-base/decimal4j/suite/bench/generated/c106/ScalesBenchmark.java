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

    private int validScale;
    private long validScaleFactor;

    @Setup
    public void setup() {
        // Setup inputs for getScaleMetrics
        validScale = 5; 
        
        // Setup inputs for findByScaleFactor
        validScaleFactor = 100000L;
    }

    @Benchmark
    public ScaleMetrics benchmarkGetScaleMetrics_ValidScale(Blackhole bh) {
        ScaleMetrics result = Scales.getScaleMetrics(validScale);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public ScaleMetrics benchmarkFindByScaleFactor_ValidFactor(Blackhole bh) {
        ScaleMetrics result = Scales.findByScaleFactor(validScaleFactor);
        bh.consume(result);
        return result;
    }
}
