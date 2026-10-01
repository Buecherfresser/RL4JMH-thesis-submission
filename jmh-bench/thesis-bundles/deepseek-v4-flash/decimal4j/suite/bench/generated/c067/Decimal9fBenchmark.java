package bench.generated.c067;

import org.decimal4j.api.ImmutableDecimal;
import org.decimal4j.immutable.Decimal18f;
import org.decimal4j.immutable.Decimal9f;
import org.decimal4j.truncate.UncheckedRounding;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Decimal9fBenchmark {

    private Decimal9f a, b, e;
    private long l1;
    private double d1;
    private BigDecimal bd;
    private String str;
    private RoundingMode rm;
    private int exp2;
    private int scale3;
    private int scale5;

    @Setup(Level.Trial)
    public void setup() {
        a = Decimal9f.valueOf("123.456789012");
        b = Decimal9f.valueOf("987.654321098");
        e = Decimal9f.valueOf("-123.456789012");
        l1 = 123456789L;
        d1 = 123.456789012;
        bd = new BigDecimal("123.4567890123456789");
        str = "123.456789012";
        rm = RoundingMode.HALF_UP;
        exp2 = 2;
        scale3 = 3;
        scale5 = 5;
    }

    @Benchmark
    public Decimal9f addDecimal() {
        return a.add(b);
    }

    @Benchmark
    public Decimal9f addLong() {
        return a.add(l1);
    }

    @Benchmark
    public Decimal9f subtractDecimal() {
        return a.subtract(b);
    }

    @Benchmark
    public Decimal9f multiplyDecimal() {
        return a.multiply(b);
    }

    @Benchmark
    public Decimal9f divideDecimal() {
        return a.divide(b, rm);
    }

    @Benchmark
    public Decimal9f remainderDecimal() {
        return a.remainder(b);
    }

    @Benchmark
    public Decimal9f negate() {
        return a.negate();
    }

    @Benchmark
    public Decimal9f abs() {
        return e.abs();
    }

    @Benchmark
    public Decimal9f square() {
        return a.square();
    }

    @Benchmark
    public Decimal9f sqrt() {
        return a.sqrt();
    }

    @Benchmark
    public Decimal9f pow() {
        return a.pow(exp2);
    }

    @Benchmark
    public Decimal9f avg() {
        return a.avg(b);
    }

    @Benchmark
    public Decimal9f min() {
        return a.min(b);
    }

    @Benchmark
    public Decimal9f max() {
        return a.max(b);
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
    public int getScale() {
        return a.getScale();
    }

    @Benchmark
    public String toStringBench() {
        return a.toString();
    }

    @Benchmark
    public Decimal9f valueOfLong() {
        return Decimal9f.valueOf(l1);
    }

    @Benchmark
    public Decimal9f valueOfDouble() {
        return Decimal9f.valueOf(d1);
    }

    @Benchmark
    public Decimal9f valueOfBigDecimal() {
        return Decimal9f.valueOf(bd);
    }

    @Benchmark
    public Decimal9f valueOfString() {
        return Decimal9f.valueOf(str);
    }

    @Benchmark
    public Decimal9f valueOfUnscaled() {
        return Decimal9f.valueOfUnscaled(l1);
    }

    @Benchmark
    public Decimal9f valueOfUnscaledWithScale() {
        return Decimal9f.valueOfUnscaled(l1, scale3);
    }

    @Benchmark
    public Decimal18f multiplyExactBy() {
        return a.multiplyExact().by(b);
    }

    @Benchmark
    public ImmutableDecimal<?> scaleInt() {
        return a.scale(scale5);
    }

    @Benchmark
    public Decimal9f addWithRounding() {
        return a.add(b, rm);
    }

    @Benchmark
    public Decimal9f multiplyWithRounding() {
        return a.multiply(b, rm);
    }

    @Benchmark
    public Decimal9f divideWithRounding() {
        return a.divide(b, rm);
    }

    @Benchmark
    public Decimal9f multiplyUnscaled() {
        return a.multiplyUnscaled(l1, UncheckedRounding.HALF_UP);
    }
}
