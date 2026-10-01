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

    // Since Scales is a final utility class with only static methods,
    // no instance fields are required.

    @Setup
    public void setup() {
        // No setup required for static utility methods.
    }

    @Benchmark
    public void testGetScaleMetrics(Blackhole bh) {
        try {
            // Test a valid scale (e.g., scale 10)
            ScaleMetrics metrics = Scales.getScaleMetrics(10);
            bh.consume(metrics);
        } catch (IllegalArgumentException e) {
            // Should not happen with valid input
        }
    }

    @Benchmark
    public void testGetScaleMetricsInvalid(Blackhole bh) {
        try {
            // Test an invalid scale (e.g., 20) which should throw
            Scales.getScaleMetrics(20);
        } catch (IllegalArgumentException e) {
            // Expected exception
        }
    }

    @Benchmark
    public void testFindByScaleFactorFound(Blackhole bh) {
        try {
            // Test a known scale factor (10000000000000L, which corresponds to Scale14f)
            ScaleMetrics metrics = Scales.findByScaleFactor(10000000000000L);
            bh.consume(metrics);
        } catch (Exception e) {
            // Should not happen
        }
    }

    @Benchmark
    public void testFindByScaleFactorNotFound(Blackhole bh) {
        try {
            // Test a scale factor that does not exist
            Scales.findByScaleFactor(99999999999999L);
        } catch (Exception e) {
            // Expected behavior: returns null, no exception thrown
        }
    }
}
