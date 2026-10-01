package bench.generated.c051;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import org.decimal4j.immutable.Decimal11f;
import org.decimal4j.mutable.MutableDecimal11f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Decimal11fBenchmark {

    private long longValue;
    private float floatValue;
    private double doubleValue;
    private BigInteger bigIntegerValue;
    private BigDecimal bigDecimalValue;
    private String stringValue;
    private RoundingMode roundingMode;

    private Decimal11f decA;
    private Decimal11f decB;
    private Decimal11f decC;

    @Setup
    public void setup() {
        longValue = 123456789L;
        floatValue = 12345.6789f;
        doubleValue = 12345.678901234;
        bigIntegerValue = new BigInteger("123456789012345678901234567890");
        bigDecimalValue = new BigDecimal("1234567890.12345678901");
        stringValue = "12345.67890123456";
        roundingMode = RoundingMode.HALF_UP;

        decA = Decimal11f.valueOf(12345L);
        decB = Decimal11f.valueOf(6789L);
        decC = Decimal11f.valueOf(3L);
    }

    @Benchmark
    public Decimal11f benchmarkValueOfLong() {
        return Decimal11f.valueOf(longValue);
    }

    @Benchmark
    public Decimal11f benchmarkValueOfFloat() {
        return Decimal11f.valueOf(floatValue);
    }

    @Benchmark
    public Decimal11f benchmarkValueOfFloatRounding() {
        return Decimal11f.valueOf(floatValue, roundingMode);
    }

    @Benchmark
    public Decimal11f benchmarkValueOfDouble() {
        return Decimal11f.valueOf(doubleValue);
    }

    @Benchmark
    public Decimal11f benchmarkValueOfDoubleRounding() {
        return Decimal11f.valueOf(doubleValue, roundingMode);
    }

    @Benchmark
    public Decimal11f benchmarkValueOfBigInteger() {
        return Decimal11f.valueOf(bigIntegerValue);
    }

    @Benchmark
    public Decimal11f benchmarkValueOfBigDecimal() {
        return Decimal11f.valueOf(bigDecimalValue);
    }

    @Benchmark
    public Decimal11f benchmarkValueOfBigDecimalRounding() {
        return Decimal11f.valueOf(bigDecimalValue, roundingMode);
    }

    @Benchmark
    public Decimal11f benchmarkValueOfString() {
        return Decimal11f.valueOf(stringValue);
    }

    @Benchmark
    public Decimal11f benchmarkValueOfStringRounding() {
        return Decimal11f.valueOf(stringValue, roundingMode);
    }

    @Benchmark
    public Decimal11f benchmarkValueOfUnscaled() {
        return Decimal11f.valueOfUnscaled(longValue);
    }

    @Benchmark
    public Decimal11f benchmarkValueOfUnscaledWithScale() {
        return Decimal11f.valueOfUnscaled(longValue, 5);
    }

    @Benchmark
    public Decimal11f benchmarkValueOfUnscaledWithScaleRounding() {
        return Decimal11f.valueOfUnscaled(longValue, 5, roundingMode);
    }

    @Benchmark
    public Decimal11f benchmarkAdd() {
        return decA.add(decB);
    }

    @Benchmark
    public Decimal11f benchmarkSubtract() {
        return decA.subtract(decB);
    }

    @Benchmark
    public Decimal11f benchmarkMultiply() {
        return decA.multiply(decB);
    }

    @Benchmark
    public Decimal11f benchmarkDivide() {
        return decA.divide(decB);
    }

    @Benchmark
    public Decimal11f benchmarkNegate() {
        return decA.negate();
    }

    @Benchmark
    public Decimal11f benchmarkAbs() {
        return decA.abs();
    }

    @Benchmark
    public Decimal11f benchmarkSquare() {
        return decA.square();
    }

    @Benchmark
    public Decimal11f benchmarkSqrt() {
        return decA.sqrt();
    }

    @Benchmark
    public Decimal11f benchmarkRound() {
        return decA.round(2);
    }

    @Benchmark
    public String benchmarkToString() {
        return decA.toString();
    }

    @Benchmark
    public BigDecimal benchmarkToBigDecimal() {
        return decA.toBigDecimal();
    }

    @Benchmark
    public long benchmarkUnscaledValue() {
        return decA.unscaledValue();
    }

    @Benchmark
    public int benchmarkGetScale() {
        return decA.getScale();
    }

    @Benchmark
    public MutableDecimal11f benchmarkToMutable() {
        return decA.toMutableDecimal();
    }

    @Benchmark
    public Decimal11f benchmarkToImmutable() {
        return decA.toImmutableDecimal();
    }

    @Benchmark
    public Decimal11f benchmarkGetFactory() {
        return decA.getFactory().valueOf(1);
    }

    @Benchmark
    public void benchmarkConsumeViaBlackhole(Blackhole bh) {
        bh.consume(decA.add(decB));
    }
}
