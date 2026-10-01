package bench.generated.c065;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import org.decimal4j.immutable.Decimal7f;
import org.decimal4j.api.Decimal;
import org.decimal4j.exact.Multipliable7f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Decimal7fBenchmark {

    private Decimal7f a;
    private Decimal7f b;
    private Decimal7f c;
    private Decimal7f sqrtVal;
    private long longVal;
    private double doubleVal;
    private float floatVal;
    private String stringVal;
    private BigDecimal bigDecimalVal;
    private BigInteger bigIntegerVal;

    @Setup(Level.Trial)
    public void setup() {
        a = Decimal7f.valueOf("123.4567890");
        b = Decimal7f.valueOf("-987.6543210");
        c = Decimal7f.valueOf("0.5");
        sqrtVal = Decimal7f.valueOf("4.0");
        longVal = 123456789L;
        doubleVal = 123.456789;
        floatVal = 123.456789f;
        stringVal = "123.4567890";
        bigDecimalVal = new BigDecimal("123.4567890");
        bigIntegerVal = new BigInteger("123456789");
    }

    @Benchmark
    public Decimal7f valueOfLong() {
        return Decimal7f.valueOf(longVal);
    }

    @Benchmark
    public Decimal7f valueOfDouble() {
        return Decimal7f.valueOf(doubleVal);
    }

    @Benchmark
    public Decimal7f valueOfFloat() {
        return Decimal7f.valueOf(floatVal);
    }

    @Benchmark
    public Decimal7f valueOfString() {
        return Decimal7f.valueOf(stringVal);
    }

    @Benchmark
    public Decimal7f valueOfBigDecimal() {
        return Decimal7f.valueOf(bigDecimalVal);
    }

    @Benchmark
    public Decimal7f valueOfBigInteger() {
        return Decimal7f.valueOf(bigIntegerVal);
    }

    @Benchmark
    public Decimal7f valueOfDecimal() {
        return Decimal7f.valueOf(a);
    }

    @Benchmark
    public Decimal7f valueOfUnscaled() {
        return Decimal7f.valueOfUnscaled(longVal);
    }

    @Benchmark
    public Decimal7f valueOfUnscaledScale() {
        return Decimal7f.valueOfUnscaled(longVal, 3);
    }

    @Benchmark
    public Decimal7f add() {
        return a.add(b);
    }

    @Benchmark
    public Decimal7f subtract() {
        return a.subtract(b);
    }

    @Benchmark
    public Decimal7f multiply() {
        return a.multiply(b);
    }

    @Benchmark
    public Decimal7f divide() {
        return a.divide(b, RoundingMode.HALF_UP);
    }

    @Benchmark
    public Decimal7f negate() {
        return a.negate();
    }

    @Benchmark
    public Decimal7f abs() {
        return a.abs();
    }

    @Benchmark
    public Decimal7f square() {
        return a.square();
    }

    @Benchmark
    public Decimal7f sqrt() {
        return sqrtVal.sqrt();
    }

    @Benchmark
    public Decimal7f pow() {
        return a.pow(2);
    }

    @Benchmark
    public Decimal7f avg() {
        return a.avg(b);
    }

    @Benchmark
    public Decimal7f shiftLeft() {
        return a.shiftLeft(2);
    }

    @Benchmark
    public Decimal7f shiftRight() {
        return a.shiftRight(2);
    }

    @Benchmark
    public Decimal7f round() {
        return a.round(2);
    }

    @Benchmark
    public long longValue() {
        return a.longValue();
    }

    @Benchmark
    public double doubleValue() {
        return a.doubleValue();
    }

    @Benchmark
    public float floatValue() {
        return a.floatValue();
    }

    @Benchmark
    public BigDecimal toBigDecimal() {
        return a.toBigDecimal();
    }

    @Benchmark
    public BigInteger toBigInteger() {
        return a.toBigInteger();
    }

    @Benchmark
    public long unscaledValue() {
        return a.unscaledValue();
    }

    @Benchmark
    public String toStringBench() {
        return a.toString();
    }

    @Benchmark
    public Multipliable7f multiplyExact() {
        return a.multiplyExact();
    }
}
