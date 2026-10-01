package bench.generated.c000;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.arithmetic.CheckedScale0fRoundingArithmetic;
import java.math.RoundingMode;
import java.math.BigDecimal;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class CheckedScale0fRoundingArithmeticBenchmark {

    private CheckedScale0fRoundingArithmetic arithmetic;

    private long uDecimal1;
    private long uDecimal2;
    private long unscaled;
    private int scale;
    private long divisor;
    private int power;
    private int exponent;
    private int precision;
    private int shift;
    private float floatVal;
    private double doubleVal;
    private BigDecimal bigDecimalVal;
    private String parseString;
    private CharSequence parseCharSeq;
    private long longDivisor;

    @Setup(Level.Trial)
    public void setup() {
        arithmetic = new CheckedScale0fRoundingArithmetic(RoundingMode.HALF_UP);
        uDecimal1 = 123456789L;
        uDecimal2 = 987654321L;
        unscaled = 1000L;
        scale = 2;
        divisor = 12345L;
        power = 3;
        exponent = 5;
        precision = 2;
        shift = 4;
        floatVal = 12345.67f;
        doubleVal = 1234567.89;
        bigDecimalVal = new BigDecimal("123456789.123");
        parseString = "987654321";
        parseCharSeq = new StringBuilder("123456789");
        longDivisor = 7L;
    }

    @Benchmark
    public long addUnscaled() {
        return arithmetic.addUnscaled(uDecimal1, unscaled, scale);
    }

    @Benchmark
    public long subtractUnscaled() {
        return arithmetic.subtractUnscaled(uDecimal1, unscaled, scale);
    }

    @Benchmark
    public long multiplyByUnscaled() {
        return arithmetic.multiplyByUnscaled(uDecimal1, unscaled, scale);
    }

    @Benchmark
    public long divideByUnscaled() {
        return arithmetic.divideByUnscaled(uDecimal1, unscaled, scale);
    }

    @Benchmark
    public long divide() {
        return arithmetic.divide(uDecimal1, uDecimal2);
    }

    @Benchmark
    public long multiplyByPowerOf10() {
        return arithmetic.multiplyByPowerOf10(uDecimal1, power);
    }

    @Benchmark
    public long divideByLong() {
        return arithmetic.divideByLong(uDecimal1, longDivisor);
    }

    @Benchmark
    public long divideByPowerOf10() {
        return arithmetic.divideByPowerOf10(uDecimal1, power);
    }

    @Benchmark
    public long avg() {
        return arithmetic.avg(uDecimal1, uDecimal2);
    }

    @Benchmark
    public long invert() {
        return arithmetic.invert(uDecimal1);
    }

    @Benchmark
    public long sqrt() {
        return arithmetic.sqrt(uDecimal1);
    }

    @Benchmark
    public long pow() {
        return arithmetic.pow(uDecimal1, exponent);
    }

    @Benchmark
    public long round() {
        return arithmetic.round(uDecimal1, precision);
    }

    @Benchmark
    public long shiftLeft() {
        return arithmetic.shiftLeft(uDecimal1, shift);
    }

    @Benchmark
    public long shiftRight() {
        return arithmetic.shiftRight(uDecimal1, shift);
    }

    @Benchmark
    public float toFloat() {
        return arithmetic.toFloat(uDecimal1);
    }

    @Benchmark
    public double toDouble() {
        return arithmetic.toDouble(uDecimal1);
    }

    @Benchmark
    public long toUnscaled() {
        return arithmetic.toUnscaled(uDecimal1, scale);
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
    public long fromUnscaled() {
        return arithmetic.fromUnscaled(unscaled, scale);
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
        return arithmetic.parse(parseCharSeq, 0, parseCharSeq.length());
    }
}
