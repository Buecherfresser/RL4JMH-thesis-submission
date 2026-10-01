package bench.generated.c005;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.math.BigDecimal;
import org.decimal4j.arithmetic.UncheckedScale0fTruncatingArithmetic;
import org.openjdk.jmh.annotations.Scope;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class UncheckedScale0fTruncatingArithmeticBenchmark {

    private UncheckedScale0fTruncatingArithmetic arithmetic;

    // Sample operands
    private long valA;
    private long valB;
    private long unscaled;
    private int scale; // always 0 for Scale0f
    private int positions; // for power‑of‑10 operations
    private int exponent;
    private int shiftPositions;
    private int precision;
    private double doubleVal;
    private float floatVal;
    private BigDecimal bigDecimalVal;
    private String parseString;
    private CharSequence parseCharSeq;
    private int parseStart;
    private int parseEnd;

    @Setup
    public void setup() {
        arithmetic = UncheckedScale0fTruncatingArithmetic.INSTANCE;

        valA = 123456789L;
        valB = 987654321L;
        unscaled = 42L;
        scale = 0;
        positions = 3;          // 10^3 = 1000
        exponent = 5;           // power
        shiftPositions = 4;     // bit shift
        precision = 2;          // rounding precision (unused by truncating arithmetic)
        doubleVal = 12345.6789;
        floatVal = 12345.67f;
        bigDecimalVal = new BigDecimal("123456789.12345");
        parseString = "987654321";
        parseCharSeq = "1122334455";
        parseStart = 2;
        parseEnd = 8; // parses "223344"
    }

    @Benchmark
    public long addUnscaled() {
        return arithmetic.addUnscaled(valA, unscaled, scale);
    }

    @Benchmark
    public long subtractUnscaled() {
        return arithmetic.subtractUnscaled(valA, unscaled, scale);
    }

    @Benchmark
    public long multiplyByUnscaled() {
        return arithmetic.multiplyByUnscaled(valA, unscaled, scale);
    }

    @Benchmark
    public long divide() {
        return arithmetic.divide(valA, valB);
    }

    @Benchmark
    public long divideByLong() {
        return arithmetic.divideByLong(valA, valB);
    }

    @Benchmark
    public long divideByUnscaled() {
        return arithmetic.divideByUnscaled(valA, unscaled, scale);
    }

    @Benchmark
    public long multiplyByPowerOf10() {
        return arithmetic.multiplyByPowerOf10(valA, positions);
    }

    @Benchmark
    public long divideByPowerOf10() {
        return arithmetic.divideByPowerOf10(valA, positions);
    }

    @Benchmark
    public long invert() {
        return arithmetic.invert(valA);
    }

    @Benchmark
    public long sqrt() {
        return arithmetic.sqrt(valA);
    }

    @Benchmark
    public long pow() {
        return arithmetic.pow(valA, exponent);
    }

    @Benchmark
    public long shiftLeft() {
        return arithmetic.shiftLeft(valA, shiftPositions);
    }

    @Benchmark
    public long shiftRight() {
        return arithmetic.shiftRight(valA, shiftPositions);
    }

    @Benchmark
    public long avg() {
        return arithmetic.avg(valA, valB);
    }

    @Benchmark
    public long round() {
        return arithmetic.round(valA, precision);
    }

    @Benchmark
    public long toUnscaled() {
        return arithmetic.toUnscaled(valA, scale);
    }

    @Benchmark
    public double toDouble() {
        return arithmetic.toDouble(valA);
    }

    @Benchmark
    public float toFloat() {
        return arithmetic.toFloat(valA);
    }

    @Benchmark
    public long fromUnscaled() {
        return arithmetic.fromUnscaled(unscaled, scale);
    }

    @Benchmark
    public long fromFloat() {
        return arithmetic.fromFloat(floatVal);
    }

    @Benchmark
    public long fromDouble() {
        return arithmetic.fromDouble(doubleVal);
    }

    @Benchmark
    public long fromBigDecimal() {
        return arithmetic.fromBigDecimal(bigDecimalVal);
    }

    @Benchmark
    public long parseString() {
        return arithmetic.parse(parseString);
    }

    @Benchmark
    public long parseCharSequence() {
        return arithmetic.parse(parseCharSeq, parseStart, parseEnd);
    }
}
