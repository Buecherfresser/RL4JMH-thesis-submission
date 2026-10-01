package bench.generated.c004;

import org.openjdk.jmh.annotations.*;
import org.decimal4j.arithmetic.UncheckedScale0fRoundingArithmetic;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class UncheckedScale0fRoundingArithmeticBenchmark {

    private UncheckedScale0fRoundingArithmetic arithmetic;
    private long a, b, c;
    private int scale, positions, exponent, precision;
    private float f;
    private double d;
    private BigDecimal bd;
    private String str;
    private CharSequence cs;
    private int start, end;

    @Setup(Level.Trial)
    public void setup() {
        arithmetic = new UncheckedScale0fRoundingArithmetic(RoundingMode.HALF_UP);
        a = 123456789L;
        b = 987654321L;
        c = 1000000L;
        scale = 2;
        positions = 3;
        exponent = 5;
        precision = 2;
        f = 123.456f;
        d = 123456.789;
        bd = new BigDecimal("123456.789");
        str = "123456.789";
        cs = str;
        start = 0;
        end = str.length();
    }

    // Basic arithmetic (inherited, but part of public API)
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

    // Overridden methods
    @Benchmark
    public long addUnscaled() {
        return arithmetic.addUnscaled(a, b, scale);
    }

    @Benchmark
    public long subtractUnscaled() {
        return arithmetic.subtractUnscaled(a, b, scale);
    }

    @Benchmark
    public long multiplyByUnscaled() {
        return arithmetic.multiplyByUnscaled(a, b, scale);
    }

    @Benchmark
    public long divide() {
        return arithmetic.divide(a, b);
    }

    @Benchmark
    public long divideByLong() {
        return arithmetic.divideByLong(a, b);
    }

    @Benchmark
    public long divideByUnscaled() {
        return arithmetic.divideByUnscaled(a, b, scale);
    }

    @Benchmark
    public long avg() {
        return arithmetic.avg(a, b);
    }

    @Benchmark
    public long invert() {
        return arithmetic.invert(a);
    }

    @Benchmark
    public long shiftLeft() {
        return arithmetic.shiftLeft(a, positions);
    }

    @Benchmark
    public long shiftRight() {
        return arithmetic.shiftRight(a, positions);
    }

    @Benchmark
    public long divideByPowerOf10() {
        return arithmetic.divideByPowerOf10(a, positions);
    }

    @Benchmark
    public long multiplyByPowerOf10() {
        return arithmetic.multiplyByPowerOf10(a, positions);
    }

    @Benchmark
    public long sqrt() {
        return arithmetic.sqrt(a);
    }

    @Benchmark
    public long pow() {
        return arithmetic.pow(a, exponent);
    }

    @Benchmark
    public long round() {
        return arithmetic.round(a, precision);
    }

    @Benchmark
    public long toUnscaled() {
        return arithmetic.toUnscaled(a, scale);
    }

    @Benchmark
    public float toFloat() {
        return arithmetic.toFloat(a);
    }

    @Benchmark
    public double toDouble() {
        return arithmetic.toDouble(a);
    }

    @Benchmark
    public long fromUnscaled() {
        return arithmetic.fromUnscaled(a, scale);
    }

    @Benchmark
    public long fromFloat() {
        return arithmetic.fromFloat(f);
    }

    @Benchmark
    public long fromDouble() {
        return arithmetic.fromDouble(d);
    }

    @Benchmark
    public long fromBigDecimal() {
        return arithmetic.fromBigDecimal(bd);
    }

    @Benchmark
    public long parseString() {
        return arithmetic.parse(str);
    }

    @Benchmark
    public long parseCharSequence() {
        return arithmetic.parse(cs, start, end);
    }
}
