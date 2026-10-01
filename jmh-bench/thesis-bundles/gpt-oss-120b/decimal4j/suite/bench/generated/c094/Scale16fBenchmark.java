package bench.generated.c094;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.scale.Scale16f;
import java.math.BigInteger;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.decimal4j.api.DecimalArithmetic;
import org.decimal4j.truncate.TruncationPolicy;
import org.decimal4j.truncate.UncheckedRounding;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Scale16fBenchmark {

    private Scale16f scale;
    private long longValue;
    private int intFactor;
    private long dividend;
    private long unsignedDividend;
    private long factorLong;
    private RoundingMode roundingMode;
    private TruncationPolicy truncationPolicy;

    @Setup(Level.Trial)
    public void setup() {
        scale = Scale16f.INSTANCE;
        longValue = 123456789L;
        intFactor = 12345;
        dividend = 9876543210123456L;
        unsignedDividend = 0x7FFFFFFFFFFFFFFFL;
        factorLong = 123456L;
        roundingMode = RoundingMode.HALF_UP;
        truncationPolicy = UncheckedRounding.UNNECESSARY;
    }

    @Benchmark
    public int benchGetScale() {
        return scale.getScale();
    }

    @Benchmark
    public long benchGetScaleFactor() {
        return scale.getScaleFactor();
    }

    @Benchmark
    public int benchGetScaleFactorNumberOfLeadingZeros() {
        return scale.getScaleFactorNumberOfLeadingZeros();
    }

    @Benchmark
    public long benchMultiplyByScaleFactor() {
        return scale.multiplyByScaleFactor(factorLong);
    }

    @Benchmark
    public BigInteger benchGetScaleFactorAsBigInteger() {
        return scale.getScaleFactorAsBigInteger();
    }

    @Benchmark
    public BigDecimal benchGetScaleFactorAsBigDecimal() {
        return scale.getScaleFactorAsBigDecimal();
    }

    @Benchmark
    public long benchGetMaxIntegerValue() {
        return scale.getMaxIntegerValue();
    }

    @Benchmark
    public long benchGetMinIntegerValue() {
        return scale.getMinIntegerValue();
    }

    @Benchmark
    public boolean benchIsValidIntegerValue() {
        return scale.isValidIntegerValue(longValue);
    }

    @Benchmark
    public long benchMultiplyByScaleFactorExact() {
        return scale.multiplyByScaleFactorExact(factorLong);
    }

    @Benchmark
    public long benchMulloByScaleFactor() {
        return scale.mulloByScaleFactor(intFactor);
    }

    @Benchmark
    public long benchMulhiByScaleFactor() {
        return scale.mulhiByScaleFactor(intFactor);
    }

    @Benchmark
    public long benchDivideByScaleFactor() {
        return scale.divideByScaleFactor(dividend);
    }

    @Benchmark
    public long benchDivideUnsignedByScaleFactor() {
        return scale.divideUnsignedByScaleFactor(unsignedDividend);
    }

    @Benchmark
    public long benchModuloByScaleFactor() {
        return scale.moduloByScaleFactor(dividend);
    }

    @Benchmark
    public String benchToStringLong() {
        return scale.toString(longValue);
    }

    @Benchmark
    public DecimalArithmetic benchGetDefaultArithmetic() {
        return scale.getDefaultArithmetic();
    }

    @Benchmark
    public DecimalArithmetic benchGetDefaultCheckedArithmetic() {
        return scale.getDefaultCheckedArithmetic();
    }

    @Benchmark
    public DecimalArithmetic benchGetRoundingDownArithmetic() {
        return scale.getRoundingDownArithmetic();
    }

    @Benchmark
    public DecimalArithmetic benchGetRoundingFloorArithmetic() {
        return scale.getRoundingFloorArithmetic();
    }

    @Benchmark
    public DecimalArithmetic benchGetRoundingHalfEvenArithmetic() {
        return scale.getRoundingHalfEvenArithmetic();
    }

    @Benchmark
    public DecimalArithmetic benchGetRoundingUnnecessaryArithmetic() {
        return scale.getRoundingUnnecessaryArithmetic();
    }

    @Benchmark
    public DecimalArithmetic benchGetArithmeticRoundingMode() {
        return scale.getArithmetic(roundingMode);
    }

    @Benchmark
    public DecimalArithmetic benchGetCheckedArithmeticRoundingMode() {
        return scale.getCheckedArithmetic(roundingMode);
    }

    @Benchmark
    public DecimalArithmetic benchGetArithmeticTruncationPolicy() {
        return scale.getArithmetic(truncationPolicy);
    }

    @Benchmark
    public String benchToString() {
        return scale.toString();
    }
}
