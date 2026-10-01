package bench.generated.c099;

import org.decimal4j.scale.Scale3f;
import org.decimal4j.truncate.UncheckedRounding;
import org.openjdk.jmh.annotations.*;

import java.math.RoundingMode;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Scale3fBenchmark {

    private long factor;
    private long exactFactor;
    private long dividend;
    private long unsignedDividend;
    private long moduloDividend;
    private int intFactor;
    private long validValue;
    private long invalidValue;
    private long toStringValue;
    private RoundingMode roundingMode;
    private UncheckedRounding truncationPolicy;

    @Setup(Level.Trial)
    public void setup() {
        factor = 123456789L;
        exactFactor = 123456L; // within safe range for exact multiplication
        dividend = 123456789L;
        unsignedDividend = 123456789L;
        moduloDividend = 123456789L;
        intFactor = 123456;
        validValue = 123456L;
        invalidValue = Long.MAX_VALUE;
        toStringValue = 123456789L; // represents 123456.789
        roundingMode = RoundingMode.HALF_UP;
        truncationPolicy = UncheckedRounding.HALF_UP;
    }

    @Benchmark
    public int benchGetScale() {
        return Scale3f.INSTANCE.getScale();
    }

    @Benchmark
    public long benchGetScaleFactor() {
        return Scale3f.INSTANCE.getScaleFactor();
    }

    @Benchmark
    public int benchGetScaleFactorNumberOfLeadingZeros() {
        return Scale3f.INSTANCE.getScaleFactorNumberOfLeadingZeros();
    }

    @Benchmark
    public java.math.BigInteger benchGetScaleFactorAsBigInteger() {
        return Scale3f.INSTANCE.getScaleFactorAsBigInteger();
    }

    @Benchmark
    public java.math.BigDecimal benchGetScaleFactorAsBigDecimal() {
        return Scale3f.INSTANCE.getScaleFactorAsBigDecimal();
    }

    @Benchmark
    public long benchGetMaxIntegerValue() {
        return Scale3f.INSTANCE.getMaxIntegerValue();
    }

    @Benchmark
    public long benchGetMinIntegerValue() {
        return Scale3f.INSTANCE.getMinIntegerValue();
    }

    @Benchmark
    public boolean benchIsValidIntegerValueValid() {
        return Scale3f.INSTANCE.isValidIntegerValue(validValue);
    }

    @Benchmark
    public boolean benchIsValidIntegerValueInvalid() {
        return Scale3f.INSTANCE.isValidIntegerValue(invalidValue);
    }

    @Benchmark
    public long benchMultiplyByScaleFactor() {
        return Scale3f.INSTANCE.multiplyByScaleFactor(factor);
    }

    @Benchmark
    public long benchMultiplyByScaleFactorExact() {
        return Scale3f.INSTANCE.multiplyByScaleFactorExact(exactFactor);
    }

    @Benchmark
    public long benchMulloByScaleFactor() {
        return Scale3f.INSTANCE.mulloByScaleFactor(intFactor);
    }

    @Benchmark
    public long benchMulhiByScaleFactor() {
        return Scale3f.INSTANCE.mulhiByScaleFactor(intFactor);
    }

    @Benchmark
    public long benchDivideByScaleFactor() {
        return Scale3f.INSTANCE.divideByScaleFactor(dividend);
    }

    @Benchmark
    public long benchDivideUnsignedByScaleFactor() {
        return Scale3f.INSTANCE.divideUnsignedByScaleFactor(unsignedDividend);
    }

    @Benchmark
    public long benchModuloByScaleFactor() {
        return Scale3f.INSTANCE.moduloByScaleFactor(moduloDividend);
    }

    @Benchmark
    public String benchToString() {
        return Scale3f.INSTANCE.toString(toStringValue);
    }

    @Benchmark
    public org.decimal4j.api.DecimalArithmetic benchGetDefaultArithmetic() {
        return Scale3f.INSTANCE.getDefaultArithmetic();
    }

    @Benchmark
    public org.decimal4j.api.DecimalArithmetic benchGetDefaultCheckedArithmetic() {
        return Scale3f.INSTANCE.getDefaultCheckedArithmetic();
    }

    @Benchmark
    public org.decimal4j.api.DecimalArithmetic benchGetRoundingDownArithmetic() {
        return Scale3f.INSTANCE.getRoundingDownArithmetic();
    }

    @Benchmark
    public org.decimal4j.api.DecimalArithmetic benchGetRoundingFloorArithmetic() {
        return Scale3f.INSTANCE.getRoundingFloorArithmetic();
    }

    @Benchmark
    public org.decimal4j.api.DecimalArithmetic benchGetRoundingHalfEvenArithmetic() {
        return Scale3f.INSTANCE.getRoundingHalfEvenArithmetic();
    }

    @Benchmark
    public org.decimal4j.api.DecimalArithmetic benchGetRoundingUnnecessaryArithmetic() {
        return Scale3f.INSTANCE.getRoundingUnnecessaryArithmetic();
    }

    @Benchmark
    public org.decimal4j.api.DecimalArithmetic benchGetArithmetic() {
        return Scale3f.INSTANCE.getArithmetic(roundingMode);
    }

    @Benchmark
    public org.decimal4j.api.DecimalArithmetic benchGetCheckedArithmetic() {
        return Scale3f.INSTANCE.getCheckedArithmetic(roundingMode);
    }

    @Benchmark
    public org.decimal4j.api.DecimalArithmetic benchGetArithmeticTruncationPolicy() {
        return Scale3f.INSTANCE.getArithmetic(truncationPolicy);
    }

    @Benchmark
    public String benchEnumToString() {
        return Scale3f.INSTANCE.toString();
    }
}
