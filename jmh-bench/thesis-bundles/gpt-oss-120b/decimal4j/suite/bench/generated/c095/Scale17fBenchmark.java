package bench.generated.c095;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.scale.Scale17f;
import org.decimal4j.api.DecimalArithmetic;
import java.math.BigInteger;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.decimal4j.truncate.TruncationPolicy;
import org.decimal4j.truncate.UncheckedRounding;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Scale17fBenchmark {

    private Scale17f scale;

    private long sampleLong;
    private int sampleInt;
    private long validFactor;
    private TruncationPolicy truncPolicy;

    @Setup(Level.Trial)
    public void setUp() {
        scale = Scale17f.INSTANCE;
        sampleLong = 123456789012345L; // arbitrary value within range
        sampleInt = 12345;
        // Choose a factor that stays within integer bounds for exact multiplication
        validFactor = 10L;
        truncPolicy = UncheckedRounding.HALF_UP;
    }

    @Benchmark
    public int benchmarkGetScale() {
        return scale.getScale();
    }

    @Benchmark
    public long benchmarkGetScaleFactor() {
        return scale.getScaleFactor();
    }

    @Benchmark
    public int benchmarkGetScaleFactorNumberOfLeadingZeros() {
        return scale.getScaleFactorNumberOfLeadingZeros();
    }

    @Benchmark
    public long benchmarkMultiplyByScaleFactor() {
        return scale.multiplyByScaleFactor(sampleLong);
    }

    @Benchmark
    public BigInteger benchmarkGetScaleFactorAsBigInteger() {
        return scale.getScaleFactorAsBigInteger();
    }

    @Benchmark
    public BigDecimal benchmarkGetScaleFactorAsBigDecimal() {
        return scale.getScaleFactorAsBigDecimal();
    }

    @Benchmark
    public long benchmarkGetMaxIntegerValue() {
        return scale.getMaxIntegerValue();
    }

    @Benchmark
    public long benchmarkGetMinIntegerValue() {
        return scale.getMinIntegerValue();
    }

    @Benchmark
    public boolean benchmarkIsValidIntegerValue() {
        return scale.isValidIntegerValue(sampleLong);
    }

    @Benchmark
    public long benchmarkMultiplyByScaleFactorExact() {
        return scale.multiplyByScaleFactorExact(validFactor);
    }

    @Benchmark
    public long benchmarkMulloByScaleFactor() {
        return scale.mulloByScaleFactor(sampleInt);
    }

    @Benchmark
    public long benchmarkMulhiByScaleFactor() {
        return scale.mulhiByScaleFactor(sampleInt);
    }

    @Benchmark
    public long benchmarkDivideByScaleFactor() {
        return scale.divideByScaleFactor(sampleLong);
    }

    @Benchmark
    public long benchmarkDivideUnsignedByScaleFactor() {
        return scale.divideUnsignedByScaleFactor(sampleLong);
    }

    @Benchmark
    public long benchmarkModuloByScaleFactor() {
        return scale.moduloByScaleFactor(sampleLong);
    }

    @Benchmark
    public String benchmarkToStringLong() {
        return scale.toString(sampleLong);
    }

    @Benchmark
    public DecimalArithmetic benchmarkGetDefaultArithmetic() {
        return scale.getDefaultArithmetic();
    }

    @Benchmark
    public DecimalArithmetic benchmarkGetDefaultCheckedArithmetic() {
        return scale.getDefaultCheckedArithmetic();
    }

    @Benchmark
    public DecimalArithmetic benchmarkGetRoundingDownArithmetic() {
        return scale.getRoundingDownArithmetic();
    }

    @Benchmark
    public DecimalArithmetic benchmarkGetRoundingFloorArithmetic() {
        return scale.getRoundingFloorArithmetic();
    }

    @Benchmark
    public DecimalArithmetic benchmarkGetRoundingHalfEvenArithmetic() {
        return scale.getRoundingHalfEvenArithmetic();
    }

    @Benchmark
    public DecimalArithmetic benchmarkGetRoundingUnnecessaryArithmetic() {
        return scale.getRoundingUnnecessaryArithmetic();
    }

    @Benchmark
    public DecimalArithmetic benchmarkGetArithmeticRoundingMode() {
        return scale.getArithmetic(RoundingMode.HALF_UP);
    }

    @Benchmark
    public DecimalArithmetic benchmarkGetCheckedArithmeticRoundingMode() {
        return scale.getCheckedArithmetic(RoundingMode.HALF_UP);
    }

    @Benchmark
    public DecimalArithmetic benchmarkGetArithmeticTruncationPolicy() {
        return scale.getArithmetic(truncPolicy);
    }

    @Benchmark
    public String benchmarkToString() {
        return scale.toString();
    }
}
