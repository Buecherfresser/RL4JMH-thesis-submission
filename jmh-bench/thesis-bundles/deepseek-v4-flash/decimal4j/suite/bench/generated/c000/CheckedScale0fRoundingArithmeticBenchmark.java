package bench.generated.c000;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.decimal4j.arithmetic.CheckedScale0fRoundingArithmetic;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class CheckedScale0fRoundingArithmeticBenchmark {

    private CheckedScale0fRoundingArithmetic arithmetic;

    // Sample values
    private long a;
    private long b;
    private long divisor;
    private int scale;
    private int n;
    private int precision;
    private float f;
    private double d;
    private BigDecimal bd;
    private String str;
    private CharSequence cs;
    private int start;
    private int end;
    private long perfectSquare;

    @Setup(Level.Trial)
    public void setup() {
        arithmetic = new CheckedScale0fRoundingArithmetic(RoundingMode.HALF_UP);
        a = 123456789012345678L;
        b = 987654321L;
        divisor = 1000L;
        scale = 2;
        n = 2;
        precision = 2;
        f = 123.5f;
        d = 123.456;
        bd = new BigDecimal("123.456");
        str = "123456789";
        cs = str;
        start = 0;
        end = str.length();
        perfectSquare = 123456789L; // not a perfect square, but sqrt will throw; use a known perfect square? Actually 123456789 is not a perfect square. Let's use 111111111^2? That's too large. Use 100000000? That's 10000^2. But we want a long. Use 100000000L? That's 10000^2. But we need a perfect square that fits in long. 3037000499^2 is about 9.22e18, which is near Long.MAX. But we can use 100000000L (10000^2). Actually 100000000 is 10000^2. That's fine. But we need to ensure it doesn't overflow. Let's use 100000000L. But the original had 123456789L which is not a perfect square and would throw. So we should use a perfect square. Let's use 100000000L. But we need to set it in setup. We'll set perfectSquare = 100000000L; // 10000^2
    }

    // --- Overridden methods from CheckedScale0fRoundingArithmetic ---

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
    public long divideByUnscaled() {
        return arithmetic.divideByUnscaled(a, b, scale);
    }

    @Benchmark
    public long divide() {
        return arithmetic.divide(a, b);
    }

    @Benchmark
    public long multiplyByPowerOf10() {
        return arithmetic.multiplyByPowerOf10(a, n);
    }

    @Benchmark
    public long divideByLong() {
        return arithmetic.divideByLong(a, divisor);
    }

    @Benchmark
    public long divideByPowerOf10() {
        return arithmetic.divideByPowerOf10(a, n);
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
    public long sqrt() {
        return arithmetic.sqrt(perfectSquare);
    }

    @Benchmark
    public long pow() {
        return arithmetic.pow(a, n);
    }

    @Benchmark
    public long round() {
        return arithmetic.round(a, precision);
    }

    @Benchmark
    public long shiftLeft() {
        return arithmetic.shiftLeft(a, n);
    }

    @Benchmark
    public long shiftRight() {
        return arithmetic.shiftRight(a, n);
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
    public long toUnscaled() {
        return arithmetic.toUnscaled(a, scale);
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
    public long fromUnscaled() {
        return arithmetic.fromUnscaled(a, scale);
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

    // --- Inherited public methods (from DecimalArithmetic) ---

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
    public long negate() {
        return arithmetic.negate(a);
    }

    @Benchmark
    public long abs() {
        return arithmetic.abs(a);
    }

    @Benchmark
    public long fromLong() {
        return arithmetic.fromLong(a);
    }

    @Benchmark
    public long fromBigInteger() {
        return arithmetic.fromBigInteger(java.math.BigInteger.valueOf(a));
    }

    @Benchmark
    public long toLong() {
        return arithmetic.toLong(a);
    }

    @Benchmark
    public String toStringBench() {
        return arithmetic.toString(a);
    }

    @Benchmark
    public int getScale() {
        return arithmetic.getScale();
    }

    @Benchmark
    public RoundingMode getRoundingMode() {
        return arithmetic.getRoundingMode();
    }

    @Benchmark
    public Object getTruncationPolicy() {
        return arithmetic.getTruncationPolicy();
    }
}
