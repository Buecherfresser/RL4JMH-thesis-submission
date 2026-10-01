package bench.generated.c090;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.scale.Scale12f;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Random;
import org.decimal4j.api.DecimalArithmetic;
import org.decimal4j.truncate.TruncationPolicy;
import org.decimal4j.truncate.UncheckedRounding;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Scale12fBenchmark {

    private Scale12f scale;
    private Random rnd;

    private long factor;
    private long factorExact;
    private int intFactor;
    private long dividend;
    private long unsignedDividend;
    private long moduloDividend;
    private long validValue;
    private long invalidValue;
    private long valueForToString;
    private RoundingMode roundingMode;
    private TruncationPolicy truncationPolicy;

    @Setup(Level.Trial)
    public void setUp() {
        rnd = new Random(123456789L);
        scale = Scale12f.INSTANCE;

        factor = rnd.nextLong();
        factorExact = rnd.nextInt(2_000_001) - 1_000_000;
        intFactor = rnd.nextInt();
        dividend = rnd.nextLong();
        unsignedDividend = rnd.nextLong();
        moduloDividend = rnd.nextLong();
        validValue = rnd.nextInt(2_000_001) - 1_000_000;
        invalidValue = rnd.nextLong() | (1L << 40);
        valueForToString = rnd.nextLong();

        RoundingMode[] modes = RoundingMode.values();
        roundingMode = modes[rnd.nextInt(modes.length)];
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
        return scale.isValidIntegerValue(validValue);
    }

    @Benchmark
    public boolean isInvalidIntegerValue() {
        return scale.isValidIntegerValue(invalidValue);
    }

    @Benchmark
    public long multiplyByScaleFactor() {
        return scale.multiplyByScaleFactor(factor);
    }

    @Benchmark
    public long multiplyByScaleFactorExact() {
        return scale.multiplyByScaleFactorExact(factorExact);
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
    public String toStringValue() {
        return scale.toString(valueForToString);
    }

    @Benchmark
    public String toStringInstance() {
        return scale.toString();
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
}
