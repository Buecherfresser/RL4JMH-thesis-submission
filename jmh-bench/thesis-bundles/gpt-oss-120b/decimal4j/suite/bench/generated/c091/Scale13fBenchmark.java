package bench.generated.c091;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.scale.Scale13f;
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
public class Scale13fBenchmark {

    private Scale13f scale = Scale13f.INSTANCE;

    private long longValue;
    private int intValue;
    private long dividend;
    private long unsignedDividend;
    private long moduloDividend;
    private long toStringValue;
    private RoundingMode roundingMode;
    private TruncationPolicy truncationPolicy;

    @Setup
    public void setup() {
        longValue = 12345L;
        intValue = 12345;
        dividend = scale.getScaleFactor() * 5;
        unsignedDividend = Long.MAX_VALUE >>> 1;
        moduloDividend = scale.getScaleFactor() * 3 + 7;
        toStringValue = 987654321L;
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
        return scale.multiplyByScaleFactor(longValue);
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
        return scale.isValidIntegerValue(longValue);
    }

    @Benchmark
    public long benchmarkMultiplyByScaleFactorExact() {
        return scale.multiplyByScaleFactorExact(longValue);
    }

    @Benchmark
    public long benchmarkMulloByScaleFactor() {
        return scale.mulloByScaleFactor(intValue);
    }

    @Benchmark
    public long benchmarkMulhiByScaleFactor() {
        return scale.mulhiByScaleFactor(intValue);
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
        return scale.moduloByScaleFactor(moduloDividend);
    }

    @Benchmark
    public String benchmarkToStringLong() {
        return scale.toString(toStringValue);
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

    @Benchmark
    public String benchmarkToString() {
        return scale.toString();
    }
}
