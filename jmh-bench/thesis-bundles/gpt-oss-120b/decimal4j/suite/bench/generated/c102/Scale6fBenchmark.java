package bench.generated.c102;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.scale.Scale6f;
import java.math.RoundingMode;
import org.decimal4j.api.DecimalArithmetic;
import org.decimal4j.truncate.TruncationPolicy;
import org.decimal4j.truncate.OverflowMode;
import java.math.BigInteger;
import java.math.BigDecimal;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Scale6fBenchmark {

    private Scale6f scale;

    private long factor;
    private long value;
    private int intFactor;
    private long dividend;
    private long unsignedDividend;
    private long moduloDividend;
    private RoundingMode roundingMode;
    private TruncationPolicy truncPolicy;

    private static class SimpleTruncationPolicy implements TruncationPolicy {
        private final OverflowMode overflow;
        private final RoundingMode rounding;

        SimpleTruncationPolicy(OverflowMode overflow, RoundingMode rounding) {
            this.overflow = overflow;
            this.rounding = rounding;
        }

        @Override
        public OverflowMode getOverflowMode() {
            return overflow;
        }

        @Override
        public RoundingMode getRoundingMode() {
            return rounding;
        }
    }

    @Setup(Level.Trial)
    public void setup() {
        scale = Scale6f.INSTANCE;
        factor = 123456L;
        value = 987654321L;
        intFactor = 12345;
        dividend = 9876543210L;
        unsignedDividend = 0x7FFFFFFFFFFFFFFFL;
        moduloDividend = 123456789L;
        roundingMode = RoundingMode.HALF_UP;
        truncPolicy = new SimpleTruncationPolicy(OverflowMode.UNCHECKED, RoundingMode.HALF_UP);
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
        return scale.multiplyByScaleFactor(factor);
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
    public long multiplyByScaleFactorExact() {
        return scale.multiplyByScaleFactorExact(factor);
    }

    @Benchmark
    public long mulloByScaleFactor() {
        return scale.mulloByScaleFactor(intFactor);
    }

    @Benchmark
    public long mulhiByScaleFactor() {
        return scale.mulhiByScaleFactor(intFactor);
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
        return scale.moduloByScaleFactor(moduloDividend);
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
    public DecimalArithmetic getArithmeticByRoundingMode() {
        return scale.getArithmetic(roundingMode);
    }

    @Benchmark
    public DecimalArithmetic getCheckedArithmeticByRoundingMode() {
        return scale.getCheckedArithmetic(roundingMode);
    }

    @Benchmark
    public DecimalArithmetic getArithmeticByTruncationPolicy() {
        return scale.getArithmetic(truncPolicy);
    }

    @Benchmark
    public String toStringSelf() {
        return scale.toString();
    }
}
