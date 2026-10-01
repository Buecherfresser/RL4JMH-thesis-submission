package bench.generated.c101;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.scale.Scale5f;
import java.math.RoundingMode;
import java.math.BigInteger;
import java.math.BigDecimal;
import org.decimal4j.api.DecimalArithmetic;
import org.decimal4j.truncate.UncheckedRounding;
import org.decimal4j.truncate.TruncationPolicy;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Scale5fBenchmark {

    private Scale5f scale;

    // Sample inputs
    private long sampleLong;
    private int sampleInt;
    private long sampleValidInteger;
    private long sampleInvalidInteger;
    private RoundingMode roundingMode;
    private TruncationPolicy truncationPolicy;

    @Setup(Level.Trial)
    public void setUp() {
        scale = Scale5f.INSTANCE;
        sampleLong = 12345L;
        sampleInt = 123;
        // Values within the valid integer range for this scale
        sampleValidInteger = 1000L; // well within range
        // Value outside the valid range to test isValidIntegerValue (still safe for method)
        sampleInvalidInteger = Long.MAX_VALUE;
        roundingMode = RoundingMode.HALF_UP;
        truncationPolicy = UncheckedRounding.HALF_UP;
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
        return scale.isValidIntegerValue(sampleValidInteger);
    }

    @Benchmark
    public long benchmarkMultiplyByScaleFactorExact() {
        return scale.multiplyByScaleFactorExact(sampleValidInteger);
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
        return scale.getArithmetic(roundingMode);
    }

    @Benchmark
    public DecimalArithmetic benchmarkGetCheckedArithmeticRoundingMode() {
        return scale.getCheckedArithmetic(roundingMode);
    }

    @Benchmark
    public DecimalArithmetic benchmarkGetArithmeticTruncationPolicy() {
        return scale.getArithmetic(truncationPolicy);
    }
}
