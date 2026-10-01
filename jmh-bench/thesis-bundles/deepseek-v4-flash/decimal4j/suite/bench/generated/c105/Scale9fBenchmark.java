package bench.generated.c105;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import org.decimal4j.api.DecimalArithmetic;
import org.decimal4j.scale.Scale9f;
import org.decimal4j.truncate.CheckedRounding;
import org.decimal4j.truncate.TruncationPolicy;
import org.decimal4j.truncate.UncheckedRounding;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Scale9fBenchmark {

    private long factor;
    private long negativeFactor;
    private long overflowFactor;
    private long dividend;
    private long negativeDividend;
    private long unsignedDividend;
    private long validInteger;
    private long invalidInteger;
    private long longValue;
    private int unsignedIntFactor;
    private int positiveInt;
    private RoundingMode roundingMode;
    private TruncationPolicy uncheckedPolicy;
    private TruncationPolicy checkedPolicy;
    private long addLeft;
    private long addRight;
    private long mulLeft;
    private long mulRight;
    private long divLeft;
    private long divRight;

    @Setup(Level.Trial)
    public void setUp() {
        factor = 123456789L;
        negativeFactor = -123456789L;
        overflowFactor = 9223372037L;
        dividend = 1234567890123456789L;
        negativeDividend = -1234567890123456789L;
        unsignedDividend = -1L;
        validInteger = 9223372036L;
        invalidInteger = 9223372037L;
        longValue = 1234567890123456789L;
        unsignedIntFactor = -1;
        positiveInt = 123456789;
        roundingMode = RoundingMode.HALF_UP;
        uncheckedPolicy = UncheckedRounding.valueOf(RoundingMode.HALF_UP);
        checkedPolicy = CheckedRounding.valueOf(RoundingMode.HALF_EVEN);
        addLeft = 1_000_000_000L;
        addRight = 2_000_000_000L;
        mulLeft = 1_000_000_000L;
        mulRight = 2_000_000_000L;
        divLeft = 3_000_000_000L;
        divRight = 2_000_000_000L;
    }

    @Benchmark
    public int getScale() {
        return Scale9f.INSTANCE.getScale();
    }

    @Benchmark
    public long getScaleFactor() {
        return Scale9f.INSTANCE.getScaleFactor();
    }

    @Benchmark
    public int getScaleFactorNumberOfLeadingZeros() {
        return Scale9f.INSTANCE.getScaleFactorNumberOfLeadingZeros();
    }

    @Benchmark
    public long multiplyByScaleFactor() {
        return Scale9f.INSTANCE.multiplyByScaleFactor(factor);
    }

    @Benchmark
    public long multiplyByScaleFactorNegative() {
        return Scale9f.INSTANCE.multiplyByScaleFactor(negativeFactor);
    }

    @Benchmark
    public BigInteger getScaleFactorAsBigInteger() {
        return Scale9f.INSTANCE.getScaleFactorAsBigInteger();
    }

    @Benchmark
    public BigDecimal getScaleFactorAsBigDecimal() {
        return Scale9f.INSTANCE.getScaleFactorAsBigDecimal();
    }

    @Benchmark
    public long getMaxIntegerValue() {
        return Scale9f.INSTANCE.getMaxIntegerValue();
    }

    @Benchmark
    public long getMinIntegerValue() {
        return Scale9f.INSTANCE.getMinIntegerValue();
    }

    @Benchmark
    public boolean isValidIntegerValue() {
        return Scale9f.INSTANCE.isValidIntegerValue(validInteger);
    }

    @Benchmark
    public boolean isInvalidIntegerValue() {
        return Scale9f.INSTANCE.isValidIntegerValue(invalidInteger);
    }

    @Benchmark
    public long multiplyByScaleFactorExact() {
        return Scale9f.INSTANCE.multiplyByScaleFactorExact(factor);
    }

    @Benchmark
    public long multiplyByScaleFactorExactNegative() {
        return Scale9f.INSTANCE.multiplyByScaleFactorExact(negativeFactor);
    }

    @Benchmark
    public long multiplyByScaleFactorExactOverflow() {
        try {
            return Scale9f.INSTANCE.multiplyByScaleFactorExact(overflowFactor);
        } catch (ArithmeticException ex) {
            return 0L;
        }
    }

    @Benchmark
    public long mulloByScaleFactor() {
        return Scale9f.INSTANCE.mulloByScaleFactor(unsignedIntFactor);
    }

    @Benchmark
    public long mulloByScaleFactorPositive() {
        return Scale9f.INSTANCE.mulloByScaleFactor(positiveInt);
    }

    @Benchmark
    public long mulhiByScaleFactor() {
        return Scale9f.INSTANCE.mulhiByScaleFactor(positiveInt);
    }

    @Benchmark
    public long divideByScaleFactor() {
        return Scale9f.INSTANCE.divideByScaleFactor(dividend);
    }

    @Benchmark
    public long divideByScaleFactorNegative() {
        return Scale9f.INSTANCE.divideByScaleFactor(negativeDividend);
    }

    @Benchmark
    public long divideUnsignedByScaleFactor() {
        return Scale9f.INSTANCE.divideUnsignedByScaleFactor(unsignedDividend);
    }

    @Benchmark
    public long moduloByScaleFactor() {
        return Scale9f.INSTANCE.moduloByScaleFactor(dividend);
    }

    @Benchmark
    public long moduloByScaleFactorNegative() {
        return Scale9f.INSTANCE.moduloByScaleFactor(negativeDividend);
    }

    @Benchmark
    public String toStringValue() {
        return Scale9f.INSTANCE.toString(longValue);
    }

    @Benchmark
    public DecimalArithmetic getDefaultArithmetic() {
        return Scale9f.INSTANCE.getDefaultArithmetic();
    }

    @Benchmark
    public DecimalArithmetic getDefaultCheckedArithmetic() {
        return Scale9f.INSTANCE.getDefaultCheckedArithmetic();
    }

    @Benchmark
    public DecimalArithmetic getRoundingDownArithmetic() {
        return Scale9f.INSTANCE.getRoundingDownArithmetic();
    }

    @Benchmark
    public DecimalArithmetic getRoundingFloorArithmetic() {
        return Scale9f.INSTANCE.getRoundingFloorArithmetic();
    }

    @Benchmark
    public DecimalArithmetic getRoundingHalfEvenArithmetic() {
        return Scale9f.INSTANCE.getRoundingHalfEvenArithmetic();
    }

    @Benchmark
    public DecimalArithmetic getRoundingUnnecessaryArithmetic() {
        return Scale9f.INSTANCE.getRoundingUnnecessaryArithmetic();
    }

    @Benchmark
    public DecimalArithmetic getArithmeticByRoundingMode() {
        return Scale9f.INSTANCE.getArithmetic(roundingMode);
    }

    @Benchmark
    public DecimalArithmetic getCheckedArithmeticByRoundingMode() {
        return Scale9f.INSTANCE.getCheckedArithmetic(roundingMode);
    }

    @Benchmark
    public DecimalArithmetic getArithmeticByUncheckedPolicy() {
        return Scale9f.INSTANCE.getArithmetic(uncheckedPolicy);
    }

    @Benchmark
    public DecimalArithmetic getArithmeticByCheckedPolicy() {
        return Scale9f.INSTANCE.getArithmetic(checkedPolicy);
    }

    @Benchmark
    public String toStringInstance() {
        return Scale9f.INSTANCE.toString();
    }

    @Benchmark
    public long defaultArithmeticAdd() {
        DecimalArithmetic arithmetic = Scale9f.INSTANCE.getDefaultArithmetic();
        return arithmetic.add(addLeft, addRight);
    }

    @Benchmark
    public long defaultArithmeticMultiply() {
        DecimalArithmetic arithmetic = Scale9f.INSTANCE.getDefaultArithmetic();
        return arithmetic.multiply(mulLeft, mulRight);
    }

    @Benchmark
    public long defaultArithmeticDivide() {
        DecimalArithmetic arithmetic = Scale9f.INSTANCE.getDefaultArithmetic();
        return arithmetic.divide(divLeft, divRight);
    }
}
