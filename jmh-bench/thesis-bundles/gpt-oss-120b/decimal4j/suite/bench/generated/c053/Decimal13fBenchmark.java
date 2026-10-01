package bench.generated.c053;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import org.decimal4j.immutable.Decimal13f;
import org.decimal4j.mutable.MutableDecimal13f;
import org.decimal4j.scale.Scale13f;
import org.decimal4j.factory.Factory13f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Decimal13fBenchmark {

    private Decimal13f sample;
    private Decimal13f other;

    private long sampleLong;
    private float sampleFloat;
    private double sampleDouble;
    private BigInteger sampleBigInteger;
    private BigDecimal sampleBigDecimal;
    private String sampleString;
    private long unscaledLong;
    private int unscaledScale;
    private RoundingMode roundingMode;

    @Setup(Level.Trial)
    public void setUp() {
        sample = Decimal13f.valueOf(12345L);
        other = Decimal13f.valueOf(6789L);

        sampleLong = 123456789L;
        sampleFloat = 12345.6789f;
        sampleDouble = 12345678.9012345;
        sampleBigInteger = new BigInteger("12345678901234567890");
        sampleBigDecimal = new BigDecimal("1234567890.123456789");
        sampleString = "1234567890.123456789";
        unscaledLong = 9876543210L;
        unscaledScale = 5;
        roundingMode = RoundingMode.HALF_UP;
    }

    @Benchmark
    public Decimal13f benchmarkValueOfLong() {
        return Decimal13f.valueOf(sampleLong);
    }

    @Benchmark
    public Decimal13f benchmarkValueOfFloat() {
        return Decimal13f.valueOf(sampleFloat);
    }

    @Benchmark
    public Decimal13f benchmarkValueOfFloatRounding() {
        return Decimal13f.valueOf(sampleFloat, roundingMode);
    }

    @Benchmark
    public Decimal13f benchmarkValueOfDouble() {
        return Decimal13f.valueOf(sampleDouble);
    }

    @Benchmark
    public Decimal13f benchmarkValueOfDoubleRounding() {
        return Decimal13f.valueOf(sampleDouble, roundingMode);
    }

    @Benchmark
    public Decimal13f benchmarkValueOfBigInteger() {
        return Decimal13f.valueOf(sampleBigInteger);
    }

    @Benchmark
    public Decimal13f benchmarkValueOfBigDecimal() {
        return Decimal13f.valueOf(sampleBigDecimal);
    }

    @Benchmark
    public Decimal13f benchmarkValueOfBigDecimalRounding() {
        return Decimal13f.valueOf(sampleBigDecimal, roundingMode);
    }

    @Benchmark
    public Decimal13f benchmarkValueOfString() {
        return Decimal13f.valueOf(sampleString);
    }

    @Benchmark
    public Decimal13f benchmarkValueOfStringRounding() {
        return Decimal13f.valueOf(sampleString, roundingMode);
    }

    @Benchmark
    public Decimal13f benchmarkValueOfUnscaledLong() {
        return Decimal13f.valueOfUnscaled(unscaledLong);
    }

    @Benchmark
    public Decimal13f benchmarkValueOfUnscaledLongScale() {
        return Decimal13f.valueOfUnscaled(unscaledLong, unscaledScale);
    }

    @Benchmark
    public Decimal13f benchmarkValueOfUnscaledLongScaleRounding() {
        return Decimal13f.valueOfUnscaled(unscaledLong, unscaledScale, roundingMode);
    }

    @Benchmark
    public Decimal13f benchmarkAdd() {
        return sample.add(other);
    }

    @Benchmark
    public Decimal13f benchmarkSubtract() {
        return sample.subtract(other);
    }

    @Benchmark
    public Decimal13f benchmarkMultiply() {
        return sample.multiply(other);
    }

    @Benchmark
    public Decimal13f benchmarkDivide() {
        return sample.divide(other);
    }

    @Benchmark
    public Decimal13f benchmarkRemainder() {
        return sample.remainder(other);
    }

    @Benchmark
    public Decimal13f benchmarkNegate() {
        return sample.negate();
    }

    @Benchmark
    public Decimal13f benchmarkAbs() {
        return sample.abs();
    }

    @Benchmark
    public Decimal13f benchmarkInvert() {
        return sample.invert();
    }

    @Benchmark
    public Decimal13f benchmarkSquare() {
        return sample.square();
    }

    @Benchmark
    public Decimal13f benchmarkSqrt() {
        return sample.sqrt();
    }

    @Benchmark
    public Decimal13f benchmarkPow() {
        return sample.pow(3);
    }

    @Benchmark
    public Decimal13f benchmarkAvg() {
        return sample.avg(other);
    }

    @Benchmark
    public Decimal13f benchmarkShiftLeft() {
        return sample.shiftLeft(2);
    }

    @Benchmark
    public Decimal13f benchmarkShiftRight() {
        return sample.shiftRight(2);
    }

    @Benchmark
    public Decimal13f benchmarkRound() {
        return sample.round(5);
    }

    @Benchmark
    public MutableDecimal13f benchmarkToMutableDecimal() {
        return sample.toMutableDecimal();
    }

    @Benchmark
    public Decimal13f benchmarkToImmutableDecimal() {
        return sample.toImmutableDecimal();
    }

    @Benchmark
    public int benchmarkGetScale() {
        return sample.getScale();
    }

    @Benchmark
    public Scale13f benchmarkGetScaleMetrics() {
        return sample.getScaleMetrics();
    }

    @Benchmark
    public Factory13f benchmarkGetFactory() {
        return sample.getFactory();
    }
}
