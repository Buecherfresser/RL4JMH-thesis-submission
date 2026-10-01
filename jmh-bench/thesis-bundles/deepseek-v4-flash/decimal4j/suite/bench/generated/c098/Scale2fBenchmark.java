package bench.generated.c098;

import org.openjdk.jmh.annotations.*;
import java.util.concurrent.TimeUnit;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import org.decimal4j.api.DecimalArithmetic;
import org.decimal4j.scale.Scale2f;
import org.decimal4j.truncate.CheckedRounding;
import org.decimal4j.truncate.UncheckedRounding;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Scale2fBenchmark {

    private long factor;
    private long exactFactor;
    private int positiveIntFactor;
    private int negativeIntFactor;
    private long dividend;
    private long negativeDividend;
    private long unsignedDividend;
    private long validValue;
    private long invalidValue;
    private long longValueForToString;
    private RoundingMode roundingMode;
    private UncheckedRounding uncheckedPolicy;
    private CheckedRounding checkedPolicy;

    @Setup(Level.Trial)
    public void setUp() {
        factor = 123456789L;
        exactFactor = 123456789L;
        positiveIntFactor = 123456789;
        negativeIntFactor = -123456789;
        dividend = 1234567890123456789L;
        negativeDividend = -1234567890123456789L;
        unsignedDividend = 0x8000000000000000L;
        validValue = 123456789L;
        invalidValue = Long.MAX_VALUE;
        longValueForToString = 123456789L;
        roundingMode = RoundingMode.HALF_UP;
        uncheckedPolicy = UncheckedRounding.HALF_UP;
        checkedPolicy = CheckedRounding.HALF_UP;
    }

    @Benchmark
    public int benchmarkGetScale() {
        return Scale2f.INSTANCE.getScale();
    }

    @Benchmark
    public long benchmarkGetScaleFactor() {
        return Scale2f.INSTANCE.getScaleFactor();
    }

    @Benchmark
    public int benchmarkGetScaleFactorNumberOfLeadingZeros() {
        return Scale2f.INSTANCE.getScaleFactorNumberOfLeadingZeros();
    }

    @Benchmark
    public long benchmarkMultiplyByScaleFactor() {
        return Scale2f.INSTANCE.multiplyByScaleFactor(factor);
    }

    @Benchmark
    public BigInteger benchmarkGetScaleFactorAsBigInteger() {
        return Scale2f.INSTANCE.getScaleFactorAsBigInteger();
    }

    @Benchmark
    public BigDecimal benchmarkGetScaleFactorAsBigDecimal() {
        return Scale2f.INSTANCE.getScaleFactorAsBigDecimal();
    }

    @Benchmark
    public long benchmarkGetMaxIntegerValue() {
        return Scale2f.INSTANCE.getMaxIntegerValue();
    }

    @Benchmark
    public long benchmarkGetMinIntegerValue() {
        return Scale2f.INSTANCE.getMinIntegerValue();
    }

    @Benchmark
    public boolean benchmarkIsValidIntegerValueValid() {
        return Scale2f.INSTANCE.isValidIntegerValue(validValue);
    }

    @Benchmark
    public boolean benchmarkIsValidIntegerValueInvalid() {
        return Scale2f.INSTANCE.isValidIntegerValue(invalidValue);
    }

    @Benchmark
    public long benchmarkMultiplyByScaleFactorExact() {
        return Scale2f.INSTANCE.multiplyByScaleFactorExact(exactFactor);
    }

    @Benchmark
    public long benchmarkMulloByScaleFactorPositive() {
        return Scale2f.INSTANCE.mulloByScaleFactor(positiveIntFactor);
    }

    @Benchmark
    public long benchmarkMulloByScaleFactorNegative() {
        return Scale2f.INSTANCE.mulloByScaleFactor(negativeIntFactor);
    }

    @Benchmark
    public long benchmarkMulhiByScaleFactor() {
        return Scale2f.INSTANCE.mulhiByScaleFactor(positiveIntFactor);
    }

    @Benchmark
    public long benchmarkDivideByScaleFactorPositive() {
        return Scale2f.INSTANCE.divideByScaleFactor(dividend);
    }

    @Benchmark
    public long benchmarkDivideByScaleFactorNegative() {
        return Scale2f.INSTANCE.divideByScaleFactor(negativeDividend);
    }

    @Benchmark
    public long benchmarkDivideUnsignedByScaleFactor() {
        return Scale2f.INSTANCE.divideUnsignedByScaleFactor(unsignedDividend);
    }

    @Benchmark
    public long benchmarkModuloByScaleFactorPositive() {
        return Scale2f.INSTANCE.moduloByScaleFactor(dividend);
    }

    @Benchmark
    public long benchmarkModuloByScaleFactorNegative() {
        return Scale2f.INSTANCE.moduloByScaleFactor(negativeDividend);
    }

    @Benchmark
    public String benchmarkToStringLong() {
        return Scale2f.INSTANCE.toString(longValueForToString);
    }

    @Benchmark
    public DecimalArithmetic benchmarkGetDefaultArithmetic() {
        return Scale2f.INSTANCE.getDefaultArithmetic();
    }

    @Benchmark
    public DecimalArithmetic benchmarkGetDefaultCheckedArithmetic() {
        return Scale2f.INSTANCE.getDefaultCheckedArithmetic();
    }

    @Benchmark
    public DecimalArithmetic benchmarkGetRoundingDownArithmetic() {
        return Scale2f.INSTANCE.getRoundingDownArithmetic();
    }

    @Benchmark
    public DecimalArithmetic benchmarkGetRoundingFloorArithmetic() {
        return Scale2f.INSTANCE.getRoundingFloorArithmetic();
    }

    @Benchmark
    public DecimalArithmetic benchmarkGetRoundingHalfEvenArithmetic() {
        return Scale2f.INSTANCE.getRoundingHalfEvenArithmetic();
    }

    @Benchmark
    public DecimalArithmetic benchmarkGetRoundingUnnecessaryArithmetic() {
        return Scale2f.INSTANCE.getRoundingUnnecessaryArithmetic();
    }

    @Benchmark
    public DecimalArithmetic benchmarkGetArithmeticRoundingMode() {
        return Scale2f.INSTANCE.getArithmetic(roundingMode);
    }

    @Benchmark
    public DecimalArithmetic benchmarkGetCheckedArithmeticRoundingMode() {
        return Scale2f.INSTANCE.getCheckedArithmetic(roundingMode);
    }

    @Benchmark
    public DecimalArithmetic benchmarkGetArithmeticUncheckedTruncationPolicy() {
        return Scale2f.INSTANCE.getArithmetic(uncheckedPolicy);
    }

    @Benchmark
    public DecimalArithmetic benchmarkGetArithmeticCheckedTruncationPolicy() {
        return Scale2f.INSTANCE.getArithmetic(checkedPolicy);
    }

    @Benchmark
    public String benchmarkToString() {
        return Scale2f.INSTANCE.toString();
    }
}
