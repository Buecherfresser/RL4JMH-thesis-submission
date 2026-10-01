package bench.generated.c063;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.immutable.Decimal5f;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import org.decimal4j.api.Decimal;
import org.decimal4j.mutable.MutableDecimal5f;
import org.decimal4j.scale.Scale5f;
import org.decimal4j.factory.Factory5f;
import org.decimal4j.exact.Multipliable5f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Decimal5fBenchmark {

    private Decimal5f constA;
    private Decimal5f constB;
    private String stringValue;
    private BigDecimal bigDecimalValue;
    private BigInteger bigIntegerValue;
    private float floatValue;
    private double doubleValue;
    private long longValue;
    private RoundingMode roundingMode;

    @Setup(Level.Trial)
    public void setup() {
        constA = Decimal5f.valueOf(12345L);
        constB = Decimal5f.valueOf(6789L);
        stringValue = "12345.67890";
        bigDecimalValue = new BigDecimal("12345.67890");
        bigIntegerValue = new BigInteger("12345678901234567890");
        floatValue = 12345.6789f;
        doubleValue = 12345.678901234;
        longValue = 123456789L;
        roundingMode = RoundingMode.HALF_EVEN;
    }

    // Static factory methods
    @Benchmark
    public Decimal5f benchValueOfLong() {
        return Decimal5f.valueOf(longValue);
    }

    @Benchmark
    public Decimal5f benchValueOfFloat() {
        return Decimal5f.valueOf(floatValue);
    }

    @Benchmark
    public Decimal5f benchValueOfFloatRounding() {
        return Decimal5f.valueOf(floatValue, roundingMode);
    }

    @Benchmark
    public Decimal5f benchValueOfDouble() {
        return Decimal5f.valueOf(doubleValue);
    }

    @Benchmark
    public Decimal5f benchValueOfDoubleRounding() {
        return Decimal5f.valueOf(doubleValue, roundingMode);
    }

    @Benchmark
    public Decimal5f benchValueOfBigInteger() {
        return Decimal5f.valueOf(bigIntegerValue);
    }

    @Benchmark
    public Decimal5f benchValueOfBigDecimal() {
        return Decimal5f.valueOf(bigDecimalValue);
    }

    @Benchmark
    public Decimal5f benchValueOfBigDecimalRounding() {
        return Decimal5f.valueOf(bigDecimalValue, roundingMode);
    }

    @Benchmark
    public Decimal5f benchValueOfString() {
        return Decimal5f.valueOf(stringValue);
    }

    @Benchmark
    public Decimal5f benchValueOfStringRounding() {
        return Decimal5f.valueOf(stringValue, roundingMode);
    }

    @Benchmark
    public Decimal5f benchValueOfDecimal() {
        Decimal<?> dec = constA;
        return Decimal5f.valueOf(dec);
    }

    @Benchmark
    public Decimal5f benchValueOfDecimalRounding() {
        Decimal<?> dec = constA;
        return Decimal5f.valueOf(dec, roundingMode);
    }

    @Benchmark
    public Decimal5f benchValueOfUnscaledLong() {
        return Decimal5f.valueOfUnscaled(longValue);
    }

    @Benchmark
    public Decimal5f benchValueOfUnscaledLongScale() {
        return Decimal5f.valueOfUnscaled(longValue, 3);
    }

    @Benchmark
    public Decimal5f benchValueOfUnscaledLongScaleRounding() {
        return Decimal5f.valueOfUnscaled(longValue, 3, roundingMode);
    }

    // Instance methods
    @Benchmark
    public int benchGetScale() {
        return constA.getScale();
    }

    @Benchmark
    public Scale5f benchGetScaleMetrics() {
        return constA.getScaleMetrics();
    }

    @Benchmark
    public Factory5f benchGetFactory() {
        return constA.getFactory();
    }

    @Benchmark
    public MutableDecimal5f benchToMutableDecimal() {
        return constA.toMutableDecimal();
    }

    @Benchmark
    public Decimal5f benchToImmutableDecimal() {
        return constA.toImmutableDecimal();
    }

    @Benchmark
    public Multipliable5f benchMultiplyExact() {
        return constA.multiplyExact();
    }

    // Arithmetic operations
    @Benchmark
    public Decimal5f benchAdd() {
        return constA.add(constB);
    }

    @Benchmark
    public Decimal5f benchSubtract() {
        return constA.subtract(constB);
    }

    @Benchmark
    public Decimal5f benchMultiply() {
        return constA.multiply(constB);
    }

    @Benchmark
    public Decimal5f benchDivide() {
        return constA.divide(constB);
    }

    @Benchmark
    public Decimal5f benchRemainder() {
        return constA.remainder(constB);
    }

    @Benchmark
    public Decimal5f benchNegate() {
        return constA.negate();
    }

    @Benchmark
    public Decimal5f benchAbs() {
        return constA.abs();
    }

    @Benchmark
    public Decimal5f benchSquare() {
        return constA.square();
    }

    @Benchmark
    public Decimal5f benchSqrt() {
        return constA.sqrt();
    }

    @Benchmark
    public Decimal5f benchPow() {
        return constA.pow(3);
    }

    // Conversion methods
    @Benchmark
    public long benchLongValue() {
        return constA.longValue();
    }

    @Benchmark
    public double benchDoubleValue() {
        return constA.doubleValue();
    }

    @Benchmark
    public BigDecimal benchToBigDecimal() {
        return constA.toBigDecimal();
    }

    @Benchmark
    public long benchUnscaledValue() {
        return constA.unscaledValue();
    }

    @Benchmark
    public String benchToString() {
        return constA.toString();
    }

    // Example of a void method using Blackhole (none in Decimal5f, but demonstrate pattern)
    @Benchmark
    public void benchConsumeToString(Blackhole bh) {
        bh.consume(constA.toString());
    }
}
