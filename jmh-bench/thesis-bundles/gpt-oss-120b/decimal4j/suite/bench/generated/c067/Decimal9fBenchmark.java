package bench.generated.c067;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import org.decimal4j.immutable.Decimal9f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Decimal9fBenchmark {

    private Decimal9f a;
    private Decimal9f b;
    private Decimal9f two;
    private BigInteger bigInt;
    private BigDecimal bigDec;
    private String decimalString;
    private String longDecimalString;

    @Setup(Level.Trial)
    public void setUp() {
        a = Decimal9f.valueOf(12345L);
        b = Decimal9f.valueOf(6789L);
        two = Decimal9f.TWO;
        bigInt = new BigInteger("12345678901234567890");
        bigDec = new BigDecimal("12345.678901234");
        decimalString = "12345.678901234";
        longDecimalString = "12345.67890123456789";
    }

    @Benchmark
    public Decimal9f benchmarkValueOfLong() {
        return Decimal9f.valueOf(12345L);
    }

    @Benchmark
    public Decimal9f benchmarkValueOfLongNegative() {
        return Decimal9f.valueOf(-12345L);
    }

    @Benchmark
    public Decimal9f benchmarkValueOfFloat() {
        return Decimal9f.valueOf(12345.6789f);
    }

    @Benchmark
    public Decimal9f benchmarkValueOfFloatRounding() {
        return Decimal9f.valueOf(12345.6789f, RoundingMode.HALF_UP);
    }

    @Benchmark
    public Decimal9f benchmarkValueOfDouble() {
        return Decimal9f.valueOf(12345.678901234);
    }

    @Benchmark
    public Decimal9f benchmarkValueOfDoubleRounding() {
        return Decimal9f.valueOf(12345.678901234, RoundingMode.HALF_DOWN);
    }

    @Benchmark
    public Decimal9f benchmarkValueOfBigInteger() {
        return Decimal9f.valueOf(bigInt);
    }

    @Benchmark
    public Decimal9f benchmarkValueOfBigDecimal() {
        return Decimal9f.valueOf(bigDec);
    }

    @Benchmark
    public Decimal9f benchmarkValueOfBigDecimalRounding() {
        return Decimal9f.valueOf(new BigDecimal(longDecimalString), RoundingMode.HALF_UP);
    }

    @Benchmark
    public Decimal9f benchmarkValueOfString() {
        return Decimal9f.valueOf(decimalString);
    }

    @Benchmark
    public Decimal9f benchmarkValueOfStringRounding() {
        return Decimal9f.valueOf(longDecimalString, RoundingMode.HALF_UP);
    }

    @Benchmark
    public Decimal9f benchmarkValueOfUnscaled() {
        return Decimal9f.valueOfUnscaled(123456789L);
    }

    @Benchmark
    public Decimal9f benchmarkValueOfUnscaledScale() {
        return Decimal9f.valueOfUnscaled(123456789L, 5);
    }

    @Benchmark
    public Decimal9f benchmarkValueOfUnscaledScaleRounding() {
        return Decimal9f.valueOfUnscaled(123456789L, 5, RoundingMode.HALF_UP);
    }

    @Benchmark
    public Decimal9f benchmarkAdd() {
        return a.add(b);
    }

    @Benchmark
    public Decimal9f benchmarkSubtract() {
        return a.subtract(b);
    }

    @Benchmark
    public Decimal9f benchmarkMultiply() {
        return a.multiply(b);
    }

    @Benchmark
    public Decimal9f benchmarkDivide() {
        return a.divide(b);
    }

    @Benchmark
    public Decimal9f benchmarkNegate() {
        return a.negate();
    }

    @Benchmark
    public Decimal9f benchmarkAbs() {
        return a.abs();
    }

    @Benchmark
    public Decimal9f benchmarkSquare() {
        return a.square();
    }

    @Benchmark
    public Decimal9f benchmarkSqrt() {
        return a.sqrt();
    }

    @Benchmark
    public Decimal9f benchmarkPow() {
        return a.pow(3);
    }

    @Benchmark
    public Decimal9f benchmarkAvg() {
        return a.avg(b);
    }

    @Benchmark
    public Decimal9f benchmarkShiftLeft() {
        return a.shiftLeft(2);
    }

    @Benchmark
    public Decimal9f benchmarkShiftRight() {
        return a.shiftRight(2);
    }

    @Benchmark
    public Decimal9f benchmarkRound() {
        return a.round(4);
    }

    @Benchmark
    public BigDecimal benchmarkToBigDecimal() {
        return a.toBigDecimal();
    }

    @Benchmark
    public String benchmarkToString() {
        return a.toString();
    }

    @Benchmark
    public void benchmarkMultiplyExact(Blackhole bh) {
        bh.consume(a.multiplyExact().by(two));
    }
}
