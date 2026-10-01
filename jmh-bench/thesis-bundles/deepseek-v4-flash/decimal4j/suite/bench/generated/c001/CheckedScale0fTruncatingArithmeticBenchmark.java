package bench.generated.c001;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.decimal4j.arithmetic.CheckedScale0fTruncatingArithmetic;
import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class CheckedScale0fTruncatingArithmeticBenchmark {

    private CheckedScale0fTruncatingArithmetic arithmetic;
    private long a, b, divisor, base, exponent, shiftAmount;
    private float floatVal;
    private double doubleVal;
    private BigDecimal bigDecimalVal;
    private String stringVal;

    @Setup(Level.Trial)
    public void setup() {
        arithmetic = CheckedScale0fTruncatingArithmetic.INSTANCE;
        a = 123456789L;
        b = 98765L;
        divisor = 100L;
        base = 12321L; // perfect square (111^2)
        exponent = 3L;
        shiftAmount = 3L;
        floatVal = 123.456f;
        doubleVal = 987.654;
        bigDecimalVal = new BigDecimal("123456.789");
        stringVal = "123456.789";
    }

    // --- Basic arithmetic ---

    @Benchmark
    public long add() {
        return arithmetic.add(a, b);
    }

    @Benchmark
    public long subtract() {
        return arithmetic.subtract(a, b);
    }

    @Benchmark
    public long multiply() {
        return arithmetic.multiply(a, b);
    }

    @Benchmark
    public long divide() {
        return arithmetic.divide(a, divisor);
    }

    @Benchmark
    public long divideByLong() {
        return arithmetic.divideByLong(a, divisor);
    }

    @Benchmark
    public long avg() {
        return arithmetic.avg(a, b);
    }

    // --- Unscaled‑scale operations ---

    @Benchmark
    public long addUnscaled() {
        return arithmetic.addUnscaled(a, b, 0);
    }

    @Benchmark
    public long subtractUnscaled() {
        return arithmetic.subtractUnscaled(a, b, 0);
    }

    @Benchmark
    public long multiplyByUnscaled() {
        return arithmetic.multiplyByUnscaled(a, b, 0);
    }

    @Benchmark
    public long divideByUnscaled() {
        return arithmetic.divideByUnscaled(a, divisor, 0);
    }

    // --- Power and root ---

    @Benchmark
    public long pow() {
        return arithmetic.pow(base, (int) exponent);
    }

    @Benchmark
    public long sqrt() {
        return arithmetic.sqrt(base);
    }

    @Benchmark
    public long invert() {
        return arithmetic.invert(a);
    }

    // --- Shift and rounding ---

    @Benchmark
    public long shiftLeft() {
        return arithmetic.shiftLeft(a, (int) shiftAmount);
    }

    @Benchmark
    public long shiftRight() {
        return arithmetic.shiftRight(a, (int) shiftAmount);
    }

    @Benchmark
    public long round() {
        return arithmetic.round(a, 2);
    }

    @Benchmark
    public long divideByPowerOf10() {
        return arithmetic.divideByPowerOf10(a, 2);
    }

    @Benchmark
    public long multiplyByPowerOf10() {
        return arithmetic.multiplyByPowerOf10(a, 2);
    }

    // --- Conversions to primitive types ---

    @Benchmark
    public float toFloat() {
        return arithmetic.toFloat(a);
    }

    @Benchmark
    public double toDouble() {
        return arithmetic.toDouble(a);
    }

    @Benchmark
    public long toLong() {
        return arithmetic.toLong(a);
    }

    @Benchmark
    public BigDecimal toBigDecimal() {
        return arithmetic.toBigDecimal(a);
    }

    @Benchmark
    public String toString() {
        return arithmetic.toString(a);
    }

    @Benchmark
    public long toUnscaled() {
        // convert to scale 2 (irrelevant for scale 0, but exercises the path)
        return arithmetic.toUnscaled(a, 2);
    }

    // --- Conversions from other types ---

    @Benchmark
    public long fromFloat() {
        return arithmetic.fromFloat(floatVal);
    }

    @Benchmark
    public long fromDouble() {
        return arithmetic.fromDouble(doubleVal);
    }

    @Benchmark
    public long fromUnscaled() {
        return arithmetic.fromUnscaled(a, 0);
    }

    @Benchmark
    public long fromBigDecimal() {
        return arithmetic.fromBigDecimal(bigDecimalVal);
    }

    @Benchmark
    public long parseString() {
        return arithmetic.parse(stringVal);
    }

    @Benchmark
    public long parseCharSequence() {
        return arithmetic.parse(stringVal, 0, stringVal.length());
    }
}
