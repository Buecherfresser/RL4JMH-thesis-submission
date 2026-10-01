package bench.generated.c106;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.scale.Scales;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ScalesBenchmark {

    // Inputs for getScaleMetrics(int scale)
    private List<Integer> scalesToTest;

    // Inputs for findByScaleFactor(long scaleFactor)
    private List<Long> scaleFactorsToTest;

    @Setup
    public void setup() {
        // 1. Setup scales (0 to 18)
        scalesToTest = new ArrayList<>();
        for (int i = 0; i <= 18; i++) {
            scalesToTest.add(i);
        }

        // 2. Setup scale factors (matching the SCALE_FACTORS array in Scales)
        scaleFactorsToTest = new ArrayList<>();
        for (long factor : new long[]{
            1, 10, 100, 1000, 10000, 100000, 1000000, 10000000, 100000000, 1000000000,
            10000000000L, 100000000000L, 1000000000000L, 10000000000000L, 100000000000000L,
            1000000000000000L, 10000000000000000L, 100000000000000000L, 1000000000000000000L
        }) {
            scaleFactorsToTest.add(factor);
        }
    }

    @Benchmark
    public void getScaleMetrics_Lookup(Blackhole bh) {
        int scale = scalesToTest.get((int) (Math.random() * scalesToTest.size()));
        ScaleMetrics metrics = Scales.getScaleMetrics(scale);
        bh.consume(metrics);
    }

    @Benchmark
    public void findByScaleFactor_Lookup(Blackhole bh) {
        long factor = scaleFactorsToTest.get((int) (Math.random() * scaleFactorsToTest.size()));
        ScaleMetrics metrics = Scales.findByScaleFactor(factor);
        bh.consume(metrics);
    }
}
