package bench.generated.c059;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.immutable.Decimal1f;
import java.math.RoundingMode;
import java.math.BigInteger;
import java.math.BigDecimal;
import org.decimal4j.exact.Multipliable1f;
import org.decimal4j.api.Decimal;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Decimal1fBenchmark {

    private long longValue;
    private float floatValue;
    private double doubleValue;
    private BigInteger bigIntegerValue;
    private BigDecimal bigDecimalValue;
    private BigDecimal bigDecimalRounding;
    private String stringValue;
    private String stringValueRounding;
    private RoundingMode roundingMode;
    private Decimal1f a;
    private Decimal1f b;
    private long unscaledValue;

    @Setup(Level.Trial)
    public void setup() {
        longValue = 12345L;
        floatValue = 12345.6f;
        doubleValue = 12345.6789;
        bigIntegerValue = new BigInteger("12345678901234567890");
        bigDecimalValue = new BigDecimal("12345.6");
        bigDecimalRounding = new BigDecimal("12345.6789");
        stringValue = "12345.6";
        stringValueRounding = "12345.6789";
        roundingMode = RoundingMode.HALF_UP;
        a = Decimal1f.valueOf(12345L);
        b = Decimal1f.valueOf(6789L);
        unscaledValue = 123456L;
    }

    @Benchmark
    public Decimal1f benchmarkValueOfLong() {
        return Decimal1f.valueOf(longValue);
    }

    @Benchmark
    public Decimal1f benchmarkValueOfFloat() {
        return Decimal1f.valueOf(floatValue);
    }

    @Benchmark
    public Decimal1f benchmarkValueOfFloatRounding() {
        return Decimal1f.valueOf(floatValue, roundingMode);
    }

    @Benchmark
    public Decimal1f benchmarkValueOfDouble() {
        return Decimal1f.valueOf(doubleValue);
    }

    @Benchmark
    public Decimal1f benchmarkValueOfDoubleRounding() {
        return Decimal1f.valueOf(doubleValue, roundingMode);
    }

    @Benchmark
    public Decimal1f benchmarkValueOfBigInteger() {
        return Decimal1f.valueOf(bigIntegerValue);
    }

    @Benchmark
    public Decimal1f benchmarkValueOfBigDecimal() {
        return Decimal1f.valueOf(bigDecimalValue);
    }

    @Benchmark
    public Decimal1f benchmarkValueOfBigDecimalRounding() {
        return Decimal1f.valueOf(bigDecimalRounding, roundingMode);
    }

    @Benchmark
    public Decimal1f benchmarkValueOfDecimal() {
        return Decimal1f.valueOf((Decimal<?>) a);
    }

    @Benchmark
    public Decimal1f benchmarkValueOfDecimalRounding() {
        return Decimal1f.valueOf((Decimal<?>) b, roundingMode);
    }

    @Benchmark
    public Decimal1f benchmarkValueOfString() {
        return Decimal1f.valueOf(stringValue);
    }

    @Benchmark
    public Decimal1f benchmarkValueOfStringRounding() {
        return Decimal1f.valueOf(stringValueRounding, roundingMode);
    }

    @Benchmark
    public Decimal1f benchmarkValueOfUnscaled() {
        return Decimal1f.valueOfUnscaled(unscaledValue);
    }

    @Benchmark
    public Decimal1f benchmarkValueOfUnscaledWithScale() {
        return Decimal1f.valueOfUnscaled(unscaledValue, 2);
    }

    @Benchmark
    public Decimal1f benchmarkValueOfUnscaledWithScaleRounding() {
        return Decimal1f.valueOfUnscaled(unscaledValue, 2, roundingMode);
    }

    @Benchmark
    public Decimal1f benchmarkAdd() {
        return a.add(b);
    }

    @Benchmark
    public Decimal1f benchmarkSubtract() {
        return a.subtract(b);
    }

    @Benchmark
    public Decimal1f benchmarkMultiply() {
        return a.multiply(b);
    }

    @Benchmark
    public Decimal1f benchmarkDivide() {
        return a.divide(b);
    }

    @Benchmark
    public Decimal1f benchmarkRemainder() {
        return a.remainder(b);
    }

    @Benchmark
    public Decimal1f benchmarkNegate() {
        return a.negate();
    }

    @Benchmark
    public Decimal1f benchmarkAbs() {
        return a.abs();
    }

    @Benchmark
    public Decimal1f benchmarkSquare() {
        return a.square();
    }

    @Benchmark
    public Decimal1f benchmarkSqrt() {
        return a.sqrt();
    }

    @Benchmark
    public Decimal1f benchmarkPow() {
        return a.pow(3);
    }

    @Benchmark
    public void benchmarkMultiplyExact(Blackhole bh) {
        bh.consume(a.multiplyExact().by(b));
    }

    @Benchmark
    public Decimal1f benchmarkShiftLeft() {
        return a.shiftLeft(2);
    }

    @Benchmark
    public Decimal1f benchmarkShiftRight() {
        return a.shiftRight(2);
    }
}
