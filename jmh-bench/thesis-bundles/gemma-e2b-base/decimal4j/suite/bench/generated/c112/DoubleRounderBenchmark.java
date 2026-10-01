package bench.generated.c112;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.math.RoundingMode;

import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.scale.Scales;
import org.decimal4j.util.DoubleRounder;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class DoubleRounderBenchmark {

    // --- State Fields ---
    private DoubleRounder instanceRounder;
    private DoubleRounder staticRounderPrecision;
    private DoubleRounder staticRounderMode;

    // Input data
    private double testValue1;
    private double testValue2;
    private int precision1;
    private int precision2;

    // --- Setup ---
    @Setup
    public void setup() {
        // 1. Setup ScaleMetrics for instance rounder
        ScaleMetrics scaleMetrics = Scales.getScaleMetrics(5); // Use a standard scale
        this.instanceRounder = new DoubleRounder(scaleMetrics);

        // 2. Setup Static Rounder (Precision only)
        this.staticRounderPrecision = new DoubleRounder(10);
        this.precision1 = 10;

        // 3. Setup Static Rounder (Precision + Mode)
        this.staticRounderMode = new DoubleRounder(18);
        this.precision2 = 18;

        // 4. Setup Test Values
        this.testValue1 = 123.4567890123456789;
        this.testValue2 = -987.654321;
    }

    // --- Benchmarks for Instance Rounding ---

    @Benchmark
    public void instanceRound_DefaultMode(Blackhole bh) {
        double result = instanceRounder.round(testValue1);
        bh.consume(result);
    }

    @Benchmark
    public void instanceRound_WithRoundingMode(Blackhole bh) {
        RoundingMode mode = RoundingMode.HALF_UP;
        double result = instanceRounder.round(testValue1, mode);
        bh.consume(result);
    }

    // --- Benchmarks for Static Rounding (Precision only) ---

    @Benchmark
    public void staticRound_PrecisionOnly(Blackhole bh) {
        double result = DoubleRounder.round(testValue1, precision1);
        bh.consume(result);
    }

    // --- Benchmarks for Static Rounding (Precision + Mode) ---

    @Benchmark
    public void staticRound_PrecisionAndMode(Blackhole bh) {
        RoundingMode mode = RoundingMode.HALF_EVEN;
        double result = DoubleRounder.round(testValue1, precision2, mode);
        bh.consume(result);
    }
}
