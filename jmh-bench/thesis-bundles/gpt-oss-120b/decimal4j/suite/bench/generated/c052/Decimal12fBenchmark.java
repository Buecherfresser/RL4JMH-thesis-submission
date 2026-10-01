package bench.generated.c052;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.immutable.Decimal12f;
import org.decimal4j.exact.Multipliable12f;
import org.decimal4j.mutable.MutableDecimal12f;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Decimal12fBenchmark {

    private Decimal12f base;
    private Decimal12f other;
    private long largeLong;
    private double doubleVal;
    private float floatVal;
    private BigInteger bigInt;
    private BigDecimal bigDec;
    private String decimalString;
    private String longString;
    private RoundingMode roundingMode;

    @Setup(Level.Trial)
    public void setup() {
        base = Decimal12f.valueOf(12345L);
        other = Decimal12f.valueOf(6789L);
        largeLong = Long.MAX_VALUE / 2;
        doubleVal = 12345.6789012345;
        floatVal = 12345.678f;
        bigInt = new BigInteger("1234567890123456789012345");
        bigDec = new BigDecimal("1234567890.123456789012");
        decimalString = "1234567890.123456789012";
        longString = "9876543210.987654321098";
        roundingMode = RoundingMode.HALF_UP;
    }

    @Benchmark
    public Decimal12f valueOfLong() {
        return Decimal12f.valueOf(largeLong);
    }

    @Benchmark
    public Decimal12f valueOfLongSmall() {
        return Decimal12f.valueOf(5L);
    }

    @Benchmark
    public Decimal12f valueOfDouble() {
        return Decimal12f.valueOf(doubleVal);
    }

    @Benchmark
    public Decimal12f valueOfDoubleRounding() {
        return Decimal12f.valueOf(doubleVal, roundingMode);
    }

    @Benchmark
    public Decimal12f valueOfFloat() {
        return Decimal12f.valueOf(floatVal);
    }

    @Benchmark
    public Decimal12f valueOfFloatRounding() {
        return Decimal12f.valueOf(floatVal, roundingMode);
    }

    @Benchmark
    public Decimal12f valueOfBigInteger() {
        return Decimal12f.valueOf(bigInt);
    }

    @Benchmark
    public Decimal12f valueOfBigDecimal() {
        return Decimal12f.valueOf(bigDec);
    }

    @Benchmark
    public Decimal12f valueOfBigDecimalRounding() {
        return Decimal12f.valueOf(bigDec, roundingMode);
    }

    @Benchmark
    public Decimal12f valueOfString() {
        return Decimal12f.valueOf(decimalString);
    }

    @Benchmark
    public Decimal12f valueOfStringRounding() {
        return Decimal12f.valueOf(longString, roundingMode);
    }

    @Benchmark
    public Decimal12f valueOfUnscaled() {
        return Decimal12f.valueOfUnscaled(base.unscaledValue());
    }

    @Benchmark
    public Decimal12f valueOfUnscaledWithScale() {
        return Decimal12f.valueOfUnscaled(base.unscaledValue(), 6);
    }

    @Benchmark
    public Decimal12f valueOfUnscaledWithScaleRounding() {
        return Decimal12f.valueOfUnscaled(base.unscaledValue(), 6, roundingMode);
    }

    @Benchmark
    public Multipliable12f multiplyExact() {
        return base.multiplyExact();
    }

    @Benchmark
    public MutableDecimal12f toMutable() {
        return base.toMutableDecimal();
    }

    @Benchmark
    public Decimal12f toImmutable() {
        return base.toImmutableDecimal();
    }

    @Benchmark
    public Decimal12f add() {
        return base.add(other);
    }

    @Benchmark
    public Decimal12f subtract() {
        return base.subtract(other);
    }

    @Benchmark
    public Decimal12f multiply() {
        return base.multiply(other);
    }

    @Benchmark
    public Decimal12f divide() {
        return base.divide(other);
    }

    @Benchmark
    public Decimal12f negate() {
        return base.negate();
    }

    @Benchmark
    public Decimal12f abs() {
        return base.abs();
    }

    @Benchmark
    public long toLong() {
        return base.longValue();
    }

    @Benchmark
    public void toDouble(Blackhole bh) {
        bh.consume(base.doubleValue());
    }

    @Benchmark
    public void toBigDecimal(Blackhole bh) {
        bh.consume(base.toBigDecimal());
    }
}
