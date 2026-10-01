package bench.generated.c096;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.scale.Scale18f;
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
public class Scale18fBenchmark {

    private Scale18f scale = Scale18f.INSTANCE;

    // Sample inputs built once per trial
    private long sampleLong;
    private int sampleInt;
    private long sampleDividend;
    private long sampleUnsignedDividend;
    private RoundingMode sampleRoundingMode;
    private TruncationPolicy sampleTruncationPolicy;

    @Setup(Level.Trial)
    public void setup() {
        // Choose values that stay within valid integer range for the scale
        sampleLong = 123456789L;               // arbitrary safe long
        sampleInt = 42;                        // arbitrary int factor
        sampleDividend = 9876543210L;          // arbitrary dividend
        // Ensure unsigned dividend is positive
        sampleUnsignedDividend = Long.MAX_VALUE - 1;
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
        return scale.divideByScaleFactor(sampleDividend);
    }

    @Benchmark
    public long benchmarkDivideUnsignedByScaleFactor() {
        return scale.divideUnsignedByScaleFactor(sampleUnsignedDividend);
    }

    @Benchmark
    public long benchmarkModuloByScaleFactor() {
        return scale.moduloByScaleFactor(sampleDividend);
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
        return scale.getArithmetic(sampleRoundingMode);
    }

    @Benchmark
    public DecimalArithmetic benchmarkGetCheckedArithmeticRoundingMode() {
        return scale.getCheckedArithmetic(sampleRoundingMode);
    }

    @Benchmark
    public DecimalArithmetic benchmarkGetArithmeticTruncationPolicy() {
        return scale.getArithmetic(sampleTruncationPolicy);
    }

    @Benchmark
    public String benchmarkToString() {
        return scale.toString();
    }
}
