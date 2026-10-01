package bench.generated.c066;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.concurrent.TimeUnit;
import org.decimal4j.immutable.Decimal8f;
import org.decimal4j.api.Decimal;
import org.decimal4j.mutable.MutableDecimal8f;
import org.decimal4j.exact.Multipliable8f;

@State(org.openjdk.jmh.annotations.Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Decimal8fBenchmark {

    // Immutable operands
    private Decimal8f d1;
    private Decimal8f d2;

    // Primitive and object inputs
    private long longVal;
    private float floatVal;
    private double doubleVal;
    private BigInteger bigIntVal;
    private BigDecimal bigDecVal;
    private String stringVal;

    @Setup
    public void setup() {
        d1 = Decimal8f.valueOf(12345L);
        d2 = Decimal8f.valueOf(6789L);
        longVal = 987654321L;
        floatVal = 12345.6789f;
        doubleVal = 12345678.9012345;
        bigIntVal = new BigInteger("12345678901234567890");
        bigDecVal = new BigDecimal("12345678.90123456789");
        stringVal = "12345678.90123456";
    }

    // ----- valueOf overloads -----
    @Benchmark
    public Decimal8f benchmarkValueOfLong() {
        return Decimal8f.valueOf(longVal);
    }

    @Benchmark
    public Decimal8f benchmarkValueOfFloat() {
        return Decimal8f.valueOf(floatVal);
    }

    @Benchmark
    public Decimal8f benchmarkValueOfFloatRounding() {
        return Decimal8f.valueOf(floatVal, RoundingMode.HALF_UP);
    }

    @Benchmark
    public Decimal8f benchmarkValueOfDouble() {
        return Decimal8f.valueOf(doubleVal);
    }

    @Benchmark
    public Decimal8f benchmarkValueOfDoubleRounding() {
        return Decimal8f.valueOf(doubleVal, RoundingMode.HALF_UP);
    }

    @Benchmark
    public Decimal8f benchmarkValueOfBigInteger() {
        return Decimal8f.valueOf(bigIntVal);
    }

    @Benchmark
    public Decimal8f benchmarkValueOfBigDecimal() {
        return Decimal8f.valueOf(bigDecVal);
    }

    @Benchmark
    public Decimal8f benchmarkValueOfBigDecimalRounding() {
        return Decimal8f.valueOf(bigDecVal, RoundingMode.HALF_UP);
    }

    @Benchmark
    public Decimal8f benchmarkValueOfString() {
        return Decimal8f.valueOf(stringVal);
    }

    @Benchmark
    public Decimal8f benchmarkValueOfStringRounding() {
        return Decimal8f.valueOf(stringVal, RoundingMode.HALF_UP);
    }

    // ----- conversion and mutable -----
    @Benchmark
    public MutableDecimal8f benchmarkToMutable() {
        return d1.toMutableDecimal();
    }

    // ----- exact multiplication helper -----
    @Benchmark
    public Multipliable8f benchmarkMultiplyExact() {
        return d1.multiplyExact();
    }

    // ----- arithmetic operations -----
    @Benchmark
    public Decimal8f benchmarkAdd() {
        return d1.add(d2);
    }

    @Benchmark
    public Decimal8f benchmarkSubtract() {
        return d1.subtract(d2);
    }

    @Benchmark
    public Decimal8f benchmarkMultiply() {
        return d1.multiply(d2);
    }

    @Benchmark
    public Decimal8f benchmarkDivide() {
        return d1.divide(d2);
    }

    @Benchmark
    public Decimal8f benchmarkRemainder() {
        return d1.remainder(d2);
    }

    @Benchmark
    public Decimal8f benchmarkNegate() {
        return d1.negate();
    }

    @Benchmark
    public Decimal8f benchmarkAbs() {
        return d1.abs();
    }

    @Benchmark
    public Decimal8f benchmarkInvert() {
        return d1.invert();
    }

    @Benchmark
    public Decimal8f benchmarkSquare() {
        return d1.square();
    }

    @Benchmark
    public Decimal8f benchmarkSqrt() {
        return d1.sqrt();
    }

    @Benchmark
    public Decimal8f benchmarkPow() {
        return d1.pow(3);
    }

    @Benchmark
    public Decimal8f benchmarkAvg() {
        return d1.avg(d2);
    }

    @Benchmark
    public Decimal8f benchmarkShiftLeft() {
        return d1.shiftLeft(2);
    }

    @Benchmark
    public Decimal8f benchmarkShiftRight() {
        return d1.shiftRight(2);
    }

    @Benchmark
    public Decimal8f benchmarkRound() {
        return d1.round(2);
    }

    // ----- consume via Blackhole for void-like operations -----
    @Benchmark
    public void benchmarkConsumeToString(Blackhole bh) {
        bh.consume(d1.toString());
    }

    @Benchmark
    public void benchmarkConsumeToBigDecimal(Blackhole bh) {
        bh.consume(d1.toBigDecimal());
    }

    @Benchmark
    public void benchmarkConsumeToLong(Blackhole bh) {
        bh.consume(d1.longValue());
    }
}
