package bench.generated.c099;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.scale.Scale3f;
import java.math.RoundingMode;
import org.decimal4j.truncate.TruncationPolicy;
import org.decimal4j.truncate.UncheckedRounding;
import org.decimal4j.api.DecimalArithmetic;
import java.math.BigInteger;
import java.math.BigDecimal;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Scale3fBenchmark {

    private Scale3f scale = Scale3f.INSTANCE;

    private long longValue;
    private int intValue;
    private long dividend;
    private long unsignedDividend;
    private long moduloValue;
    private long toStringValue;
    private RoundingMode roundingMode;
    private TruncationPolicy truncationPolicy;

    @Setup
    public void setup() {
        // Values are chosen to be within valid ranges for Scale3f
        longValue = 123L;                     // arbitrary small number
        intValue = 45;                        // arbitrary small int
        dividend = 987654321L;                // arbitrary dividend
        unsignedDividend = 0x7FFFFFFFFFFFFFFFL; // positive long for unsigned division
        moduloValue = 5555L;                  // arbitrary value
        toStringValue = 777777L;              // arbitrary value for toString(long)
        roundingMode = RoundingMode.HALF_UP; // typical rounding mode
        truncationPolicy = UncheckedRounding.HALF_UP; // unchecked rounding policy
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
    public long multiplyByScaleFactor() {
        return scale.multiplyByScaleFactor(longValue);
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
        return scale.isValidIntegerValue(longValue);
    }

    @Benchmark
    public long multiplyByScaleFactorExact() {
        return scale.multiplyByScaleFactorExact(longValue);
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
        return scale.divideByScaleFactor(dividend);
    }

    @Benchmark
    public long divideUnsignedByScaleFactor() {
        return scale.divideUnsignedByScaleFactor(unsignedDividend);
    }

    @Benchmark
    public long moduloByScaleFactor() {
        return scale.moduloByScaleFactor(moduloValue);
    }

    @Benchmark
    public String toStringLong() {
        return scale.toString(toStringValue);
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
    public DecimalArithmetic getArithmeticByRoundingMode() {
        return scale.getArithmetic(roundingMode);
    }

    @Benchmark
    public DecimalArithmetic getCheckedArithmeticByRoundingMode() {
        return scale.getCheckedArithmetic(roundingMode);
    }

    @Benchmark
    public DecimalArithmetic getArithmeticByTruncationPolicy() {
        return scale.getArithmetic(truncationPolicy);
    }

    @Benchmark
    public String toStringSelf() {
        return scale.toString();
    }
}
