package bench.generated.c087;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import org.decimal4j.api.DecimalArithmetic;
import org.decimal4j.scale.Scale0f;
import org.decimal4j.truncate.TruncationPolicy;
import org.decimal4j.truncate.UncheckedRounding;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Scale0fBenchmark {

    private Scale0f scale;
    private long value;
    private int intValue;
    private RoundingMode roundingMode;
    private TruncationPolicy truncationPolicy;

    @Setup(Level.Trial)
    public void setup() {
        scale = Scale0f.INSTANCE;
        value = 123456789L;
        intValue = 42;
        roundingMode = RoundingMode.HALF_UP;
        truncationPolicy = UncheckedRounding.HALF_UP;
    }

    @Benchmark
    public int getScale() {
        return scale.getScale();
    }

    @Benchmark
    public long getScaleFactor() {
        return scale.getScaleFactor();
    }

    @Benchmark
    public int getScaleFactorNumberOfLeadingZeros() {
        return scale.getScaleFactorNumberOfLeadingZeros();
    }

    @Benchmark
    public BigInteger getScaleFactorAsBigInteger() {
        return scale.getScaleFactorAsBigInteger();
    }

    @Benchmark
    public BigDecimal getScaleFactorAsBigDecimal() {
        return scale.getScaleFactorAsBigDecimal();
    }

    @Benchmark
    public long getMaxIntegerValue() {
        return scale.getMaxIntegerValue();
    }

    @Benchmark
    public long getMinIntegerValue() {
        return scale.getMinIntegerValue();
    }

    @Benchmark
    public boolean isValidIntegerValue() {
        return scale.isValidIntegerValue(value);
    }

    @Benchmark
    public long multiplyByScaleFactor() {
        return scale.multiplyByScaleFactor(value);
    }

    @Benchmark
    public long multiplyByScaleFactorExact() {
        return scale.multiplyByScaleFactorExact(value);
    }

    @Benchmark
    public long mulloByScaleFactor() {
        return scale.mulloByScaleFactor(intValue);
    }

    @Benchmark
    public long mulhiByScaleFactor() {
        return scale.mulhiByScaleFactor(intValue);
    }

    @Benchmark
    public long divideByScaleFactor() {
        return scale.divideByScaleFactor(value);
    }

    @Benchmark
    public long divideUnsignedByScaleFactor() {
        return scale.divideUnsignedByScaleFactor(value);
    }

    @Benchmark
    public long moduloByScaleFactor() {
        return scale.moduloByScaleFactor(value);
    }

    @Benchmark
    public String toStringLong() {
        return scale.toString(value);
    }

    @Benchmark
    public DecimalArithmetic getDefaultArithmetic() {
        return scale.getDefaultArithmetic();
    }

    @Benchmark
    public DecimalArithmetic getDefaultCheckedArithmetic() {
        return scale.getDefaultCheckedArithmetic();
    }

    @Benchmark
    public DecimalArithmetic getRoundingDownArithmetic() {
        return scale.getRoundingDownArithmetic();
    }

    @Benchmark
    public DecimalArithmetic getRoundingFloorArithmetic() {
        return scale.getRoundingFloorArithmetic();
    }

    @Benchmark
    public DecimalArithmetic getRoundingHalfEvenArithmetic() {
        return scale.getRoundingHalfEvenArithmetic();
    }

    @Benchmark
    public DecimalArithmetic getRoundingUnnecessaryArithmetic() {
        return scale.getRoundingUnnecessaryArithmetic();
    }

    @Benchmark
    public DecimalArithmetic getArithmetic() {
        return scale.getArithmetic(roundingMode);
    }

    @Benchmark
    public DecimalArithmetic getCheckedArithmetic() {
        return scale.getCheckedArithmetic(roundingMode);
    }

    @Benchmark
    public DecimalArithmetic getArithmeticWithPolicy() {
        return scale.getArithmetic(truncationPolicy);
    }

    @Benchmark
    public String toStringScale() {
        return scale.toString();
    }
}
