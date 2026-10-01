package bench.generated.c002;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.decimal4j.arithmetic.CheckedScaleNfRoundingArithmetic;
import org.decimal4j.scale.Scale5f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class CheckedScaleNfRoundingArithmeticBenchmark {

    private CheckedScaleNfRoundingArithmetic arithmetic;
    private long a;
    private long b;
    private long unscaled;
    private int scale;
    private long uDecimal;
    private String parseStr;
    private CharSequence charSeq;
    private BigDecimal bigDecimal;
    private long baseForPow;
    private int exponent;
    private int shiftN;
    private int precisionForRound;

    @Setup(Level.Trial)
    public void setup() {
        arithmetic = new CheckedScaleNfRoundingArithmetic(Scale5f.INSTANCE, RoundingMode.HALF_UP);
        a = 123456789L;          // 1234.56789 at scale 5
        b = 987654321L;          // 9876.54321 at scale 5
        unscaled = 42L;
        scale = 2;
        uDecimal = 123456789L;
        parseStr = "123.45678";
        charSeq = "123.45678";
        bigDecimal = new BigDecimal("123.45678");
        baseForPow = 200000L;    // 2.0 at scale 5
        exponent = 10;
        shiftN = 2;
        precisionForRound = 2;
    }

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
        return arithmetic.divide(a, b);
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
    public long square() {
        return arithmetic.square(a);
    }

    @Benchmark
    public long sqrt() {
        return arithmetic.sqrt(a);
    }

    @Benchmark
    public long pow() {
        return arithmetic.pow(baseForPow, exponent);
    }

    @Benchmark
    public long shiftLeft() {
        return arithmetic.shiftLeft(uDecimal, shiftN);
    }

    @Benchmark
    public long shiftRight() {
        return arithmetic.shiftRight(uDecimal, shiftN);
    }

    @Benchmark
    public long round() {
        return arithmetic.round(uDecimal, precisionForRound);
    }

    @Benchmark
    public long multiplyByPowerOf10() {
        return arithmetic.multiplyByPowerOf10(uDecimal, 2);
    }

    @Benchmark
    public long divideByPowerOf10() {
        return arithmetic.divideByPowerOf10(uDecimal, 2);
    }

    @Benchmark
    public long divideByLong() {
        return arithmetic.divideByLong(a, 7L);
    }

    @Benchmark
    public long multiplyByUnscaled() {
        return arithmetic.multiplyByUnscaled(a, 100L, scale);
    }

    @Benchmark
    public long divideByUnscaled() {
        return arithmetic.divideByUnscaled(a, 100L, scale);
    }

    @Benchmark
    public long addUnscaled() {
        return arithmetic.addUnscaled(a, 100L, scale);
    }

    @Benchmark
    public long subtractUnscaled() {
        return arithmetic.subtractUnscaled(a, 100L, scale);
    }

    @Benchmark
    public long fromLong() {
        return arithmetic.fromLong(1234567L);
    }

    @Benchmark
    public long fromFloat() {
        return arithmetic.fromFloat(123.456f);
    }

    @Benchmark
    public long fromDouble() {
        return arithmetic.fromDouble(123.456);
    }

    @Benchmark
    public long fromUnscaled() {
        return arithmetic.fromUnscaled(123456L, 2);
    }

    @Benchmark
    public long fromBigDecimal() {
        return arithmetic.fromBigDecimal(bigDecimal);
    }

    @Benchmark
    public long toLong() {
        return arithmetic.toLong(uDecimal);
    }

    @Benchmark
    public float toFloat() {
        return arithmetic.toFloat(uDecimal);
    }

    @Benchmark
    public double toDouble() {
        return arithmetic.toDouble(uDecimal);
    }

    @Benchmark
    public long toUnscaled() {
        return arithmetic.toUnscaled(uDecimal, scale);
    }

    @Benchmark
    public long parseString() {
        return arithmetic.parse(parseStr);
    }

    @Benchmark
    public long parseCharSequence() {
        return arithmetic.parse(charSeq, 0, charSeq.length());
    }

    @Benchmark
    public RoundingMode getRoundingMode() {
        return arithmetic.getRoundingMode();
    }

    @Benchmark
    public org.decimal4j.truncate.CheckedRounding getTruncationPolicy() {
        return arithmetic.getTruncationPolicy();
    }
}
