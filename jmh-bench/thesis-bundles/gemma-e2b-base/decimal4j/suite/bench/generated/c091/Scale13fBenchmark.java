package bench.generated.c091;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

import org.decimal4j.scale.Scale13f;
import org.decimal4j.api.DecimalArithmetic;
import org.decimal4j.truncate.OverflowMode;
import org.decimal4j.truncate.TruncationPolicy;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Scale13fBenchmark {

    // Inputs for arithmetic operations
    private long testFactor;
    private long testDividend;
    private long testUnsignedDividend;

    // Inputs for boundary testing
    private long maxIntegerValue;
    private long nearMaxIntegerValue;

    // Inputs for arithmetic path selection
    private DecimalArithmetic defaultArithmetic;
    private DecimalArithmetic checkedArithmetic;

    @Setup
    public void setup() {
        // Setup standard inputs
        testFactor = 1000000L;
        testDividend = 123456789L;
        testUnsignedDividend = 987654321L;

        // Setup boundary inputs based on Scale13f constants
        maxIntegerValue = Scale13f.INSTANCE.getMaxIntegerValue();
        nearMaxIntegerValue = maxIntegerValue - 1000L;

        // Setup arithmetic path objects
        defaultArithmetic = Scale13f.INSTANCE.getDefaultArithmetic();
        checkedArithmetic = Scale13f.INSTANCE.getDefaultCheckedArithmetic();
    }

    @Benchmark
    public void multiplyByScaleFactor(Blackhole bh) {
        long result = Scale13f.INSTANCE.multiplyByScaleFactor(testFactor);
        bh.consume(result);
    }

    @Benchmark
    public void mulloByScaleFactor(Blackhole bh) {
        int factor = 500000;
        long result = Scale13f.INSTANCE.mulloByScaleFactor(factor);
        bh.consume(result);
    }

    @Benchmark
    public void mulhiByScaleFactor(Blackhole bh) {
        int factor = 1000000;
        long result = Scale13f.INSTANCE.mulhiByScaleFactor(factor);
        bh.consume(result);
    }

    @Benchmark
    public void divideByScaleFactor(Blackhole bh) {
        long result = Scale13f.INSTANCE.divideByScaleFactor(testDividend);
        bh.consume(result);
    }

    @Benchmark
    public void divideUnsignedByScaleFactor(Blackhole bh) {
        long result = Scale13f.INSTANCE.divideUnsignedByScaleFactor(testUnsignedDividend);
        bh.consume(result);
    }

    @Benchmark
    public void multiplyByScaleFactorExact(Blackhole bh) {
        // Test multiplication near the maximum allowed value
        long factor = nearMaxIntegerValue;
        try {
            long result = Scale13f.INSTANCE.multiplyByScaleFactorExact(factor);
            bh.consume(result);
        } catch (ArithmeticException e) {
            // Should not happen with nearMaxIntegerValue, but handle defensively
            bh.consume(e);
        }
    }

    @Benchmark
    public void isValidIntegerValue(Blackhole bh) {
        long validValue = Scale13f.INSTANCE.getMaxIntegerValue();
        bh.consume(Scale13f.INSTANCE.isValidIntegerValue(validValue));
    }

    @Benchmark
    public void getScaleFactorAsBigDecimal(Blackhole bh) {
        java.math.BigDecimal result = Scale13f.INSTANCE.getScaleFactorAsBigDecimal();
        bh.consume(result);
    }

    @Benchmark
    public void toString(Blackhole bh) {
        long value = 123456789L;
        String result = Scale13f.INSTANCE.toString(value);
        bh.consume(result);
    }

    @Benchmark
    public void getDefaultArithmetic(Blackhole bh) {
        DecimalArithmetic result = Scale13f.INSTANCE.getDefaultArithmetic();
        bh.consume(result);
    }

    @Benchmark
    public void getCheckedArithmetic(Blackhole bh) {
        DecimalArithmetic result = Scale13f.INSTANCE.getDefaultCheckedArithmetic();
        bh.consume(result);
    }
}
