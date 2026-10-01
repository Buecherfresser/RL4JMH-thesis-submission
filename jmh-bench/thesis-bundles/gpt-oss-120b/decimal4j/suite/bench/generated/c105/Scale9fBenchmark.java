package bench.generated.c105;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.scale.Scale9f;
import java.math.BigInteger;
import java.math.BigDecimal;
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
public class Scale9fBenchmark {

    private Scale9f scale;

    private long factorLong;
    private int intFactor;
    private long dividendLong;
    private long unsignedDividend;
    private RoundingMode roundingMode;
    private TruncationPolicy truncationPolicy;

    @Setup(Level.Trial)
    public void setup() {
        scale = Scale9f.INSTANCE;
        Random rnd = new Random(12345L);

        long min = scale.getMinIntegerValue();
        long max = scale.getMaxIntegerValue();
        factorLong = min + Math.abs(rnd.nextLong()) % (max - min + 1);

        intFactor = rnd.nextInt();

        dividendLong = Math.abs(rnd.nextLong());
        unsignedDividend = dividendLong; // positive value for unsigned division

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
    public long multiplyByScaleFactor() {
        return scale.multiplyByScaleFactor(factorLong);
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
        return scale.isValidIntegerValue(factorLong);
    }

    @Benchmark
    public long multiplyByScaleFactorExact() {
        return scale.multiplyByScaleFactorExact(factorLong);
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
        return scale.divideByScaleFactor(dividendLong);
    }

    @Benchmark
    public long divideUnsignedByScaleFactor() {
        return scale.divideUnsignedByScaleFactor(unsignedDividend);
    }

    @Benchmark
    public long moduloByScaleFactor() {
        return scale.moduloByScaleFactor(dividendLong);
    }

    @Benchmark
    public String toStringLong() {
        return scale.toString(factorLong);
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
    public DecimalArithmetic getArithmeticRoundingMode() {
        return scale.getArithmetic(roundingMode);
    }

    @Benchmark
    public DecimalArithmetic getCheckedArithmetic() {
        return scale.getCheckedArithmetic(roundingMode);
    }

    @Benchmark
    public DecimalArithmetic getArithmeticTruncationPolicy() {
        return scale.getArithmetic(truncationPolicy);
    }

    @Benchmark
    public String toStringSelf() {
        return scale.toString();
    }
}
