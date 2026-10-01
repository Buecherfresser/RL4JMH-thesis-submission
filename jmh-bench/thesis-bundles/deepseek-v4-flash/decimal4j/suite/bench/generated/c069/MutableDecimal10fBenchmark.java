package bench.generated.c069;

import org.openjdk.jmh.annotations.*;
import org.decimal4j.mutable.MutableDecimal10f;
import org.decimal4j.immutable.Decimal10f;
import org.decimal4j.exact.Multipliable10f;
import org.decimal4j.api.Decimal;
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
public class MutableDecimal10fBenchmark {

    private MutableDecimal10f a;
    private MutableDecimal10f b;
    private Decimal10f dec10f;
    private BigDecimal bigDecimal;
    private BigInteger bigInteger;
    private String str;
    private double doubleVal;
    private long longVal;
    private Decimal<?> genericDecimal;

    @Setup(Level.Trial)
    public void setup() {
        a = new MutableDecimal10f("12345.6789012345");
        b = new MutableDecimal10f("0.0000000001");
        dec10f = Decimal10f.valueOf("12345.6789012345");
        bigDecimal = new BigDecimal("12345.6789012345");
        bigInteger = new BigInteger("12345678901234567890");
        str = "12345.6789012345";
        doubleVal = 12345.6789012345;
        longVal = 1234567890L;
        genericDecimal = dec10f;
    }

    // Constructors and static factories
    @Benchmark
    public MutableDecimal10f constructFromLong() {
        return new MutableDecimal10f(longVal);
    }

    @Benchmark
    public MutableDecimal10f constructFromDouble() {
        return new MutableDecimal10f(doubleVal);
    }

    @Benchmark
    public MutableDecimal10f constructFromString() {
        return new MutableDecimal10f(str);
    }

    @Benchmark
    public MutableDecimal10f constructFromBigDecimal() {
        return new MutableDecimal10f(bigDecimal);
    }

    @Benchmark
    public MutableDecimal10f constructFromBigInteger() {
        return new MutableDecimal10f(bigInteger);
    }

    @Benchmark
    public MutableDecimal10f constructFromDecimal10f() {
        return new MutableDecimal10f(dec10f);
    }

    @Benchmark
    public MutableDecimal10f constructFromGenericDecimal() {
        return new MutableDecimal10f(genericDecimal);
    }

    @Benchmark
    public MutableDecimal10f staticUnscaled() {
        return MutableDecimal10f.unscaled(longVal);
    }

    @Benchmark
    public MutableDecimal10f staticZero() {
        return MutableDecimal10f.zero();
    }

    @Benchmark
    public MutableDecimal10f staticOne() {
        return MutableDecimal10f.one();
    }

    // clone and conversions
    @Benchmark
    public MutableDecimal10f clone() {
        return a.clone();
    }

    @Benchmark
    public Decimal10f toImmutableDecimal() {
        return a.toImmutableDecimal();
    }

    @Benchmark
    public Multipliable10f multiplyExact() {
        return a.multiplyExact();
    }

    // Arithmetic operations (using a fresh mutable copy to avoid state pollution)
    @Benchmark
    public MutableDecimal10f add() {
        return new MutableDecimal10f(a).add(b);
    }

    @Benchmark
    public MutableDecimal10f subtract() {
        return new MutableDecimal10f(a).subtract(b);
    }

    @Benchmark
    public MutableDecimal10f multiply() {
        return new MutableDecimal10f(a).multiply(b);
    }

    @Benchmark
    public MutableDecimal10f divide() {
        return new MutableDecimal10f(a).divide(b, RoundingMode.HALF_UP);
    }

    @Benchmark
    public MutableDecimal10f remainder() {
        return new MutableDecimal10f(a).remainder(b);
    }

    @Benchmark
    public MutableDecimal10f negate() {
        return new MutableDecimal10f(a).negate();
    }

    @Benchmark
    public MutableDecimal10f abs() {
        return new MutableDecimal10f(a).abs();
    }

    @Benchmark
    public MutableDecimal10f square() {
        return new MutableDecimal10f(a).square();
    }

    @Benchmark
    public MutableDecimal10f sqrt() {
        return new MutableDecimal10f(a).sqrt();
    }

    @Benchmark
    public MutableDecimal10f pow() {
        return new MutableDecimal10f(a).pow(3);
    }

    @Benchmark
    public MutableDecimal10f avg() {
        return new MutableDecimal10f(a).avg(b);
    }

    @Benchmark
    public MutableDecimal10f shiftLeft() {
        return new MutableDecimal10f(a).shiftLeft(2);
    }

    @Benchmark
    public MutableDecimal10f round() {
        return new MutableDecimal10f(a).round(2);
    }

    // Conversions
    @Benchmark
    public long longValue() {
        return a.longValue();
    }

    @Benchmark
    public int intValue() {
        return a.intValue();
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
    public String toString() {
        return a.toString();
    }

    @Benchmark
    public long unscaledValue() {
        return a.unscaledValue();
    }

    // Set methods (also constructing a fresh object)
    @Benchmark
    public MutableDecimal10f setLong() {
        return new MutableDecimal10f().set(longVal);
    }

    @Benchmark
    public MutableDecimal10f setDouble() {
        return new MutableDecimal10f().set(doubleVal);
    }

    @Benchmark
    public MutableDecimal10f setString() {
        return new MutableDecimal10f().set(str);
    }

    @Benchmark
    public MutableDecimal10f setBigDecimal() {
        return new MutableDecimal10f().set(bigDecimal);
    }

    @Benchmark
    public MutableDecimal10f setBigInteger() {
        return new MutableDecimal10f().set(bigInteger);
    }
}
