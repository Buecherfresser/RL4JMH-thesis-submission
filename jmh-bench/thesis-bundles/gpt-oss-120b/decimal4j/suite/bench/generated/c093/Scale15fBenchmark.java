package bench.generated.c093;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.scale.Scale15f;
import org.decimal4j.api.DecimalArithmetic;
import java.math.RoundingMode;
import org.decimal4j.truncate.TruncationPolicy;
import org.decimal4j.truncate.UncheckedRounding;
import java.math.BigInteger;
import java.math.BigDecimal;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Scale15fBenchmark {

    private Scale15f scale = Scale15f.INSTANCE;

    // Values used across benchmarks
    private long sampleLong;
    private int sampleInt;
    private long dividend;
    private long unsignedDividend;
    private RoundingMode sampleRoundingMode;
    private TruncationPolicy sampleTruncationPolicy;

    @Setup(Level.Trial)
    public void setUp() {
        sampleLong = 123456789L;               // within integer range for this scale
        sampleInt = 12345;
        dividend = 987654321012345L;
        unsignedDividend = 0x7FFFFFFFFFFFFFFFL; // positive large value
        sampleRoundingMode = RoundingMode.HALF_UP;
        sampleTruncationPolicy = UncheckedRounding.HALF_UP;
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
        return scale.multiplyByScaleFactorExact(sampleLong);
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
        return scale.divideByScaleFactor(dividend);
    }

    @Benchmark
    public long benchmarkDivideUnsignedByScaleFactor() {
        return scale.divideUnsignedByScaleFactor(unsignedDividend);
    }

    @Benchmark
    public long benchmarkModuloByScaleFactor() {
        return scale.moduloByScaleFactor(dividend);
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
    public DecimalArithmetic benchmarkGetArithmeticByRoundingMode() {
        return scale.getArithmetic(sampleRoundingMode);
    }

    @Benchmark
    public DecimalArithmetic benchmarkGetCheckedArithmeticByRoundingMode() {
        return scale.getCheckedArithmetic(sampleRoundingMode);
    }

    @Benchmark
    public DecimalArithmetic benchmarkGetArithmeticByTruncationPolicy() {
        return scale.getArithmetic(sampleTruncationPolicy);
    }

    @Benchmark
    public String benchmarkToString() {
        return scale.toString();
    }
}
