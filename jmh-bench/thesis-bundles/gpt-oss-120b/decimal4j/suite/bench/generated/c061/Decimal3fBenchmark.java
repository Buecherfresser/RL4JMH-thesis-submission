package bench.generated.c061;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.immutable.Decimal3f;
import org.decimal4j.immutable.Decimal6f;
import org.decimal4j.exact.Multipliable3f;
import org.decimal4j.mutable.MutableDecimal3f;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Decimal3fBenchmark {

    private Decimal3f a;
    private Decimal3f b;
    private long longValue;
    private float floatValue;
    private double doubleValue;
    private BigInteger bigIntegerValue;
    private BigDecimal bigDecimalValue;
    private String stringValue;
    private long unscaledValue;
    private int unscaledScale;
    private RoundingMode roundingMode;

    @Setup(Level.Trial)
    public void setUp() {
        longValue = 12345L;
        floatValue = 12345.678f;
        doubleValue = 12345.6789d;
        bigIntegerValue = new BigInteger("12345678901234567890");
        bigDecimalValue = new BigDecimal("12345.6789012345");
        stringValue = "12345.6789";
        unscaledValue = 1234567L;
        unscaledScale = 5;
        roundingMode = RoundingMode.HALF_UP;

        a = Decimal3f.valueOf(longValue);
        b = Decimal3f.valueOf(6789L);
    }

    @Benchmark
    public Decimal3f valueOfLong() {
        return Decimal3f.valueOf(longValue);
    }

    @Benchmark
    public Decimal3f valueOfFloat() {
        return Decimal3f.valueOf(floatValue);
    }

    @Benchmark
    public Decimal3f valueOfFloatRounding() {
        return Decimal3f.valueOf(floatValue, roundingMode);
    }

    @Benchmark
    public Decimal3f valueOfDouble() {
        return Decimal3f.valueOf(doubleValue);
    }

    @Benchmark
    public Decimal3f valueOfDoubleRounding() {
        return Decimal3f.valueOf(doubleValue, roundingMode);
    }

    @Benchmark
    public Decimal3f valueOfBigInteger() {
        return Decimal3f.valueOf(bigIntegerValue);
    }

    @Benchmark
    public Decimal3f valueOfBigDecimal() {
        return Decimal3f.valueOf(bigDecimalValue);
    }

    @Benchmark
    public Decimal3f valueOfBigDecimalRounding() {
        return Decimal3f.valueOf(bigDecimalValue, roundingMode);
    }

    @Benchmark
    public Decimal3f valueOfString() {
        return Decimal3f.valueOf(stringValue);
    }

    @Benchmark
    public Decimal3f valueOfStringRounding() {
        return Decimal3f.valueOf(stringValue, roundingMode);
    }

    @Benchmark
    public Decimal3f valueOfUnscaled() {
        return Decimal3f.valueOfUnscaled(unscaledValue);
    }

    @Benchmark
    public Decimal3f valueOfUnscaledWithScale() {
        return Decimal3f.valueOfUnscaled(unscaledValue, unscaledScale);
    }

    @Benchmark
    public Decimal3f valueOfUnscaledWithScaleRounding() {
        return Decimal3f.valueOfUnscaled(unscaledValue, unscaledScale, roundingMode);
    }

    @Benchmark
    public Decimal3f add() {
        return a.add(b);
    }

    @Benchmark
    public Decimal3f subtract() {
        return a.subtract(b);
    }

    @Benchmark
    public Decimal3f multiply() {
        return a.multiply(b);
    }

    @Benchmark
    public Decimal3f divide() {
        return a.divide(b);
    }

    @Benchmark
    public Decimal3f remainder() {
        return a.remainder(b);
    }

    @Benchmark
    public Decimal3f negate() {
        return a.negate();
    }

    @Benchmark
    public Decimal3f abs() {
        return a.abs();
    }

    @Benchmark
    public Decimal3f invert() {
        return a.invert();
    }

    @Benchmark
    public Decimal3f square() {
        return a.square();
    }

    @Benchmark
    public Decimal3f sqrt() {
        return a.sqrt();
    }

    @Benchmark
    public Decimal3f pow() {
        return a.pow(2);
    }

    @Benchmark
    public Decimal3f avg() {
        return a.avg(b);
    }

    @Benchmark
    public Decimal3f shiftLeft() {
        return a.shiftLeft(2);
    }

    @Benchmark
    public Decimal3f shiftRight() {
        return a.shiftRight(2);
    }

    @Benchmark
    public Decimal3f round() {
        return a.round(2);
    }

    @Benchmark
    public void multiplyExact(Blackhole bh) {
        Multipliable3f mul = a.multiplyExact();
        bh.consume(mul.by(Decimal3f.TWO));
    }

    @Benchmark
    public MutableDecimal3f toMutable() {
        return a.toMutableDecimal();
    }

    @Benchmark
    public Decimal3f toImmutable() {
        return a.toImmutableDecimal();
    }
}
