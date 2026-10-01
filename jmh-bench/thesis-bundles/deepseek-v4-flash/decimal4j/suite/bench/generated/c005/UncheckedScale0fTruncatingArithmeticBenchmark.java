package bench.generated.c005;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.math.BigDecimal;
import org.decimal4j.arithmetic.UncheckedScale0fTruncatingArithmetic;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class UncheckedScale0fTruncatingArithmeticBenchmark {

    private final UncheckedScale0fTruncatingArithmetic arith = UncheckedScale0fTruncatingArithmetic.INSTANCE;

    private long a;
    private long b;
    private int scale;
    private int positions;
    private int exponent;
    private int precision;
    private String decimalString;
    private CharSequence charSeq;
    private int start;
    private int end;
    private float floatValue;
    private double doubleValue;
    private BigDecimal bigDecimalValue;
    private long unscaledValue;
    private int unscaledScale;

    @Setup(Level.Trial)
    public void setup() {
        a = 123456789L;
        b = 987654321L;
        scale = 2;
        positions = 3;
        exponent = 2;
        precision = 1;
        decimalString = "123.456";
        charSeq = "123456789.123456789";
        start = 0;
        end = charSeq.length();
        floatValue = 123.456f;
        doubleValue = 123.456;
        bigDecimalValue = new BigDecimal("123.456");
        unscaledValue = 123456789L;
        unscaledScale = 2;
    }

    @Benchmark
    public long addUnscaled() {
        return arith.addUnscaled(a, b, scale);
    }

    @Benchmark
    public long subtractUnscaled() {
        return arith.subtractUnscaled(a, b, scale);
    }

    @Benchmark
    public long multiplyByUnscaled() {
        return arith.multiplyByUnscaled(a, b, scale);
    }

    @Benchmark
    public long divide() {
        return arith.divide(a, b);
    }

    @Benchmark
    public long divideByLong() {
        return arith.divideByLong(a, b);
    }

    @Benchmark
    public long divideByUnscaled() {
        return arith.divideByUnscaled(a, b, scale);
    }

    @Benchmark
    public long multiplyByPowerOf10() {
        return arith.multiplyByPowerOf10(a, positions);
    }

    @Benchmark
    public long divideByPowerOf10() {
        return arith.divideByPowerOf10(a, positions);
    }

    @Benchmark
    public long invert() {
        return arith.invert(a);
    }

    @Benchmark
    public long sqrt() {
        return arith.sqrt(a);
    }

    @Benchmark
    public long pow() {
        return arith.pow(a, exponent);
    }

    @Benchmark
    public long shiftLeft() {
        return arith.shiftLeft(a, positions);
    }

    @Benchmark
    public long shiftRight() {
        return arith.shiftRight(a, positions);
    }

    @Benchmark
    public long avg() {
        return arith.avg(a, b);
    }

    @Benchmark
    public long round() {
        return arith.round(a, precision);
    }

    @Benchmark
    public long toUnscaled() {
        return arith.toUnscaled(a, scale);
    }

    @Benchmark
    public double toDouble() {
        return arith.toDouble(a);
    }

    @Benchmark
    public float toFloat() {
        return arith.toFloat(a);
    }

    @Benchmark
    public long fromUnscaled() {
        return arith.fromUnscaled(unscaledValue, unscaledScale);
    }

    @Benchmark
    public long fromFloat() {
        return arith.fromFloat(floatValue);
    }

    @Benchmark
    public long fromDouble() {
        return arith.fromDouble(doubleValue);
    }

    @Benchmark
    public long fromBigDecimal() {
        return arith.fromBigDecimal(bigDecimalValue);
    }

    @Benchmark
    public long parseString() {
        return arith.parse(decimalString);
    }

    @Benchmark
    public long parseCharSequence() {
        return arith.parse(charSeq, start, end);
    }
}
