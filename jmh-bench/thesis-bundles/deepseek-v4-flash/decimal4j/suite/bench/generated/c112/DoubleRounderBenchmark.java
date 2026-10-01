package bench.generated.c112;

import org.openjdk.jmh.annotations.*;
import java.util.concurrent.TimeUnit;
import java.math.RoundingMode;
import org.decimal4j.util.DoubleRounder;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class DoubleRounderBenchmark {

    private DoubleRounder rounder;
    private DoubleRounder rounderSamePrecision;
    private double value;
    private double exactValue;
    private int precision = 5;

    @Setup(Level.Trial)
    public void setup() {
        rounder = new DoubleRounder(precision);
        rounderSamePrecision = new DoubleRounder(precision);
        value = 123.456789;
        exactValue = 123.45678; // exactly 5 decimal places
    }

    // Instance round (default HALF_UP)
    @Benchmark
    public double instanceRoundDefault() {
        return rounder.round(value);
    }

    // Instance round with explicit HALF_UP
    @Benchmark
    public double instanceRoundHalfUp() {
        return rounder.round(value, RoundingMode.HALF_UP);
    }

    // Instance round with HALF_DOWN
    @Benchmark
    public double instanceRoundHalfDown() {
        return rounder.round(value, RoundingMode.HALF_DOWN);
    }

    // Instance round with CEILING
    @Benchmark
    public double instanceRoundCeiling() {
        return rounder.round(value, RoundingMode.CEILING);
    }

    // Instance round with FLOOR
    @Benchmark
    public double instanceRoundFloor() {
        return rounder.round(value, RoundingMode.FLOOR);
    }

    // Instance round with UP
    @Benchmark
    public double instanceRoundUp() {
        return rounder.round(value, RoundingMode.UP);
    }

    // Instance round with DOWN
    @Benchmark
    public double instanceRoundDown() {
        return rounder.round(value, RoundingMode.DOWN);
    }

    // Instance round with HALF_EVEN
    @Benchmark
    public double instanceRoundHalfEven() {
        return rounder.round(value, RoundingMode.HALF_EVEN);
    }

    // Instance round with UNNECESSARY (value is exactly representable)
    @Benchmark
    public double instanceRoundUnnecessary() {
        return rounder.round(exactValue, RoundingMode.UNNECESSARY);
    }

    // Static round with precision only
    @Benchmark
    public double staticRoundPrecision() {
        return DoubleRounder.round(value, precision);
    }

    // Static round with precision and rounding mode
    @Benchmark
    public double staticRoundPrecisionAndMode() {
        return DoubleRounder.round(value, precision, RoundingMode.HALF_UP);
    }

    // getPrecision()
    @Benchmark
    public int getPrecision() {
        return rounder.getPrecision();
    }

    // hashCode()
    @Benchmark
    public int hashCodeBenchmark() {
        return rounder.hashCode();
    }

    // equals() with a rounder of the same precision
    @Benchmark
    public boolean equalsBenchmark() {
        return rounder.equals(rounderSamePrecision);
    }

    // toString()
    @Benchmark
    public String toStringBenchmark() {
        return rounder.toString();
    }
}
