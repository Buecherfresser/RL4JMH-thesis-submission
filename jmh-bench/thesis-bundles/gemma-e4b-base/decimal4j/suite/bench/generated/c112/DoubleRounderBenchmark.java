package bench.generated.c112;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.math.RoundingMode;
import org.decimal4j.util.DoubleRounder;
import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.scale.Scales;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class DoubleRounderBenchmark {

    // --- Setup Fields ---
    private DoubleRounder rounderLowPrecision;
    private DoubleRounder rounderMediumPrecision;
    private DoubleRounder rounderHighPrecision;

    // Input values
    private double testValue1;
    private double testValue2;
    private double testValue3;

    @Setup(Level.Trial)
    public void setup() {
        // Setup Rounders for different precisions
        // Low precision (e.g., 2)
        rounderLowPrecision = new DoubleRounder(2);
        // Medium precision (e.g., 7)
        rounderMediumPrecision = new DoubleRounder(7);
        // High precision (e.g., 18)
        rounderHighPrecision = new DoubleRounder(18);

        // Setup test values
        testValue1 = 123.456789;
        testValue2 = 0.000001;
        testValue3 = 999999.999999;
    }

    // --- Constructor Benchmarks ---

    @Benchmark
    public DoubleRounder setup_Constructor_ByPrecision() {
        // Test construction using integer precision
        return new DoubleRounder(7);
    }

    @Benchmark
    public DoubleRounder setup_Constructor_ByScaleMetrics() {
        // Test construction using ScaleMetrics
        ScaleMetrics sm = Scales.getScaleMetrics(7);
        return new DoubleRounder(sm);
    }

    // --- Instance Method Benchmarks (round(double value)) ---

    @Benchmark
    public double instance_Round_DefaultRounding_LowPrecision(Blackhole bh) {
        // Uses default rounding (HALF_EVEN)
        double result = rounderLowPrecision.round(testValue1);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public double instance_Round_DefaultRounding_MediumPrecision(Blackhole bh) {
        double result = rounderMediumPrecision.round(testValue1);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public double instance_Round_DefaultRounding_HighPrecision(Blackhole bh) {
        double result = rounderHighPrecision.round(testValue1);
        bh.consume(result);
        return result;
    }

    // --- Instance Method Benchmarks (round(double value, RoundingMode roundingMode)) ---

    @Benchmark
    public double instance_Round_HalfUp_LowPrecision(Blackhole bh) {
        // Explicitly using HALF_UP
        double result = rounderLowPrecision.round(testValue1, RoundingMode.HALF_UP);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public double instance_Round_HalfDown_MediumPrecision(Blackhole bh) {
        // Explicitly using HALF_DOWN
        double result = rounderMediumPrecision.round(testValue1, RoundingMode.HALF_DOWN);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public double instance_Round_Ceiling_HighPrecision(Blackhole bh) {
        // Explicitly using CEILING
        double result = rounderHighPrecision.round(testValue1, RoundingMode.CEILING);
        bh.consume(result);
        return result;
    }

    // --- Static Method Benchmarks (round(double value, int precision)) ---

    @Benchmark
    public double static_Round_HalfUp_LowPrecision(Blackhole bh) {
        // Static method using HALF_UP
        double result = DoubleRounder.round(testValue1, 2, RoundingMode.HALF_UP);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public double static_Round_Default_MediumPrecision(Blackhole bh) {
        // Static method using default rounding (HALF_EVEN)
        double result = DoubleRounder.round(testValue1, 7);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public double static_Round_Unnecessary_HighPrecision(Blackhole bh) {
        // Testing UNNECESSARY mode
        double result = DoubleRounder.round(testValue2, 18, RoundingMode.UNNECESSARY);
        bh.consume(result);
        return result;
    }
}
