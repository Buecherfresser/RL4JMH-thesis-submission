package bench.generated.c090;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.scale.Scale12f;
import java.math.BigInteger;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.decimal4j.truncate.TruncationPolicy;
import org.decimal4j.truncate.UncheckedRounding;
import org.decimal4j.api.DecimalArithmetic;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Scale12fBenchmark {

    private Scale12f scale = Scale12f.INSTANCE;

    private long sampleLong;
    private int sampleInt;
    private long sampleUnsignedLong;
    private long sampleDivisible;
    private RoundingMode sampleRoundingMode;
    private TruncationPolicy sampleTruncationPolicy;

    @Setup(Level.Trial)
    public void setUp() {
        sampleLong = 123456789L;
        sampleInt = 12345;
        sampleUnsignedLong = Long.MAX_VALUE >>> 1;
        sampleDivisible = Scale12f.SCALE_FACTOR * 5L; // guaranteed divisible
        sampleRoundingMode = RoundingMode.HALF_UP;
        sampleTruncationPolicy = UncheckedRounding.valueOf(RoundingMode.HALF_UP);
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
        return scale.multiplyByScaleFactor(sampleLong);
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
        return scale.isValidIntegerValue(sampleLong);
    }

    @Benchmark
    public long benchMultiplyByScaleFactorExact() {
        return scale.multiplyByScaleFactorExact(sampleLong);
    }

    @Benchmark
    public long benchMulloByScaleFactor() {
        return scale.mulloByScaleFactor(sampleInt);
    }

    @Benchmark
    public long benchMulhiByScaleFactor() {
        return scale.mulhiByScaleFactor(sampleInt);
    }

    @Benchmark
    public long benchDivideByScaleFactor() {
        return scale.divideByScaleFactor(sampleDivisible);
    }

    @Benchmark
    public long benchDivideUnsignedByScaleFactor() {
        return scale.divideUnsignedByScaleFactor(sampleUnsignedLong);
    }

    @Benchmark
    public long benchModuloByScaleFactor() {
        return scale.moduloByScaleFactor(sampleLong);
    }

    @Benchmark
    public String benchToStringLong() {
        return scale.toString(sampleLong);
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
        return scale.getArithmetic(sampleRoundingMode);
    }

    @Benchmark
    public DecimalArithmetic benchGetCheckedArithmeticRoundingMode() {
        return scale.getCheckedArithmetic(sampleRoundingMode);
    }

    @Benchmark
    public DecimalArithmetic benchGetArithmeticTruncationPolicy() {
        return scale.getArithmetic(sampleTruncationPolicy);
    }

    @Benchmark
    public String benchToString() {
        return scale.toString();
    }
}
