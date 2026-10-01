package bench.generated.c101;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.math.RoundingMode;
import org.decimal4j.scale.Scale5f;
import org.decimal4j.truncate.UncheckedRounding;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Scale5fBenchmark {

    private long factor;
    private long dividend;
    private int intFactor;
    private long unsignedDividend;
    private long value;
    private RoundingMode roundingMode;
    private UncheckedRounding truncationPolicy;

    @Setup(Level.Trial)
    public void setup() {
        factor = 123456789L;
        dividend = 987654321L;
        intFactor = 12345;
        unsignedDividend = 0xFFFFFFFFL; // 4294967295
        value = 123456789L;
        roundingMode = RoundingMode.HALF_UP;
        truncationPolicy = UncheckedRounding.HALF_UP;
    }

    @Benchmark
    public int getScale() {
        return Scale5f.INSTANCE.getScale();
    }

    @Benchmark
    public long getScaleFactor() {
        return Scale5f.INSTANCE.getScaleFactor();
    }

    @Benchmark
    public int getScaleFactorNumberOfLeadingZeros() {
        return Scale5f.INSTANCE.getScaleFactorNumberOfLeadingZeros();
    }

    @Benchmark
    public long multiplyByScaleFactor() {
        return Scale5f.INSTANCE.multiplyByScaleFactor(factor);
    }

    @Benchmark
    public java.math.BigInteger getScaleFactorAsBigInteger() {
        return Scale5f.INSTANCE.getScaleFactorAsBigInteger();
    }

    @Benchmark
    public java.math.BigDecimal getScaleFactorAsBigDecimal() {
        return Scale5f.INSTANCE.getScaleFactorAsBigDecimal();
    }

    @Benchmark
    public long getMaxIntegerValue() {
        return Scale5f.INSTANCE.getMaxIntegerValue();
    }

    @Benchmark
    public long getMinIntegerValue() {
        return Scale5f.INSTANCE.getMinIntegerValue();
    }

    @Benchmark
    public boolean isValidIntegerValue() {
        return Scale5f.INSTANCE.isValidIntegerValue(value);
    }

    @Benchmark
    public long multiplyByScaleFactorExact() {
        return Scale5f.INSTANCE.multiplyByScaleFactorExact(factor);
    }

    @Benchmark
    public long mulloByScaleFactor() {
        return Scale5f.INSTANCE.mulloByScaleFactor(intFactor);
    }

    @Benchmark
    public long mulhiByScaleFactor() {
        return Scale5f.INSTANCE.mulhiByScaleFactor(intFactor);
    }

    @Benchmark
    public long divideByScaleFactor() {
        return Scale5f.INSTANCE.divideByScaleFactor(dividend);
    }

    @Benchmark
    public long divideUnsignedByScaleFactor() {
        return Scale5f.INSTANCE.divideUnsignedByScaleFactor(unsignedDividend);
    }

    @Benchmark
    public long moduloByScaleFactor() {
        return Scale5f.INSTANCE.moduloByScaleFactor(dividend);
    }

    @Benchmark
    public String toStringValue() {
        return Scale5f.INSTANCE.toString(value);
    }

    @Benchmark
    public org.decimal4j.api.DecimalArithmetic getDefaultArithmetic() {
        return Scale5f.INSTANCE.getDefaultArithmetic();
    }

    @Benchmark
    public org.decimal4j.api.DecimalArithmetic getDefaultCheckedArithmetic() {
        return Scale5f.INSTANCE.getDefaultCheckedArithmetic();
    }

    @Benchmark
    public org.decimal4j.api.DecimalArithmetic getRoundingDownArithmetic() {
        return Scale5f.INSTANCE.getRoundingDownArithmetic();
    }

    @Benchmark
    public org.decimal4j.api.DecimalArithmetic getRoundingFloorArithmetic() {
        return Scale5f.INSTANCE.getRoundingFloorArithmetic();
    }

    @Benchmark
    public org.decimal4j.api.DecimalArithmetic getRoundingHalfEvenArithmetic() {
        return Scale5f.INSTANCE.getRoundingHalfEvenArithmetic();
    }

    @Benchmark
    public org.decimal4j.api.DecimalArithmetic getRoundingUnnecessaryArithmetic() {
        return Scale5f.INSTANCE.getRoundingUnnecessaryArithmetic();
    }

    @Benchmark
    public org.decimal4j.api.DecimalArithmetic getArithmetic() {
        return Scale5f.INSTANCE.getArithmetic(roundingMode);
    }

    @Benchmark
    public org.decimal4j.api.DecimalArithmetic getCheckedArithmetic() {
        return Scale5f.INSTANCE.getCheckedArithmetic(roundingMode);
    }

    @Benchmark
    public org.decimal4j.api.DecimalArithmetic getArithmeticTruncationPolicy() {
        return Scale5f.INSTANCE.getArithmetic(truncationPolicy);
    }

    @Benchmark
    public String toStringInstance() {
        return Scale5f.INSTANCE.toString();
    }
}
