package bench.generated.c064;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.immutable.Decimal6f;
import java.math.RoundingMode;
import java.math.BigDecimal;
import java.math.BigInteger;
import org.decimal4j.exact.Multipliable6f;
import org.decimal4j.mutable.MutableDecimal6f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Decimal6fBenchmark {

    private Decimal6f a;
    private Decimal6f b;

    private long valLong;
    private float valFloat;
    private double valDouble;
    private BigInteger valBigInt;
    private BigDecimal valBigDec;
    private String valString;
    private long valUnscaled;
    private int valScale;
    private RoundingMode roundingMode;

    @Setup(Level.Trial)
    public void setup() {
        valLong = 12345L;
        valFloat = 12345.678f;
        valDouble = 12345.678901;
        valBigInt = new BigInteger("12345678901234567890");
        valBigDec = new BigDecimal("12345.67890123456789");
        valString = "12345.678901";
        valUnscaled = 12345678L;
        valScale = 4;
        roundingMode = RoundingMode.HALF_UP;

        a = Decimal6f.valueOf(1000L);
        b = Decimal6f.valueOf(200L);
    }

    // Static factory benchmarks
    @Benchmark
    public Decimal6f benchmarkValueOfLong() {
        return Decimal6f.valueOf(valLong);
    }

    @Benchmark
    public Decimal6f benchmarkValueOfFloat() {
        return Decimal6f.valueOf(valFloat);
    }

    @Benchmark
    public Decimal6f benchmarkValueOfFloatRounding() {
        return Decimal6f.valueOf(valFloat, roundingMode);
    }

    @Benchmark
    public Decimal6f benchmarkValueOfDouble() {
        return Decimal6f.valueOf(valDouble);
    }

    @Benchmark
    public Decimal6f benchmarkValueOfDoubleRounding() {
        return Decimal6f.valueOf(valDouble, roundingMode);
    }

    @Benchmark
    public Decimal6f benchmarkValueOfBigInteger() {
        return Decimal6f.valueOf(valBigInt);
    }

    @Benchmark
    public Decimal6f benchmarkValueOfBigDecimal() {
        return Decimal6f.valueOf(valBigDec);
    }

    @Benchmark
    public Decimal6f benchmarkValueOfBigDecimalRounding() {
        return Decimal6f.valueOf(valBigDec, roundingMode);
    }

    @Benchmark
    public Decimal6f benchmarkValueOfString() {
        return Decimal6f.valueOf(valString);
    }

    @Benchmark
    public Decimal6f benchmarkValueOfStringRounding() {
        return Decimal6f.valueOf(valString, roundingMode);
    }

    @Benchmark
    public Decimal6f benchmarkValueOfUnscaled() {
        return Decimal6f.valueOfUnscaled(valUnscaled);
    }

    @Benchmark
    public Decimal6f benchmarkValueOfUnscaledWithScale() {
        return Decimal6f.valueOfUnscaled(valUnscaled, valScale);
    }

    @Benchmark
    public Decimal6f benchmarkValueOfUnscaledWithScaleRounding() {
        return Decimal6f.valueOfUnscaled(valUnscaled, valScale, roundingMode);
    }

    // Instance arithmetic benchmarks
    @Benchmark
    public Decimal6f benchmarkAdd() {
        return a.add(b);
    }

    @Benchmark
    public Decimal6f benchmarkSubtract() {
        return a.subtract(b);
    }

    @Benchmark
    public Decimal6f benchmarkMultiply() {
        return a.multiply(b);
    }

    @Benchmark
    public Decimal6f benchmarkDivide() {
        return a.divide(b);
    }

    @Benchmark
    public Decimal6f benchmarkRemainder() {
        return a.remainder(b);
    }

    @Benchmark
    public Decimal6f benchmarkNegate() {
        return a.negate();
    }

    @Benchmark
    public Decimal6f benchmarkAbs() {
        return a.abs();
    }

    @Benchmark
    public Decimal6f benchmarkInvert() {
        return a.invert();
    }

    @Benchmark
    public Decimal6f benchmarkSquare() {
        return a.square();
    }

    @Benchmark
    public Decimal6f benchmarkSqrt() {
        return a.sqrt();
    }

    @Benchmark
    public Decimal6f benchmarkPow() {
        return a.pow(3);
    }

    // Exact multiplication benchmark
    @Benchmark
    public Object benchmarkMultiplyExact() {
        return a.multiplyExact().by(Decimal6f.TWO);
    }

    // Conversion benchmarks
    @Benchmark
    public MutableDecimal6f benchmarkToMutable() {
        return a.toMutableDecimal();
    }

    @Benchmark
    public Decimal6f benchmarkToImmutable() {
        return a.toImmutableDecimal();
    }
}
