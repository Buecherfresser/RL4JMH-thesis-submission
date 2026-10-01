package bench.generated.c002;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.arithmetic.CheckedScaleNfRoundingArithmetic;
import org.decimal4j.scale.Scale5f;
import java.math.BigDecimal;
import java.math.RoundingMode;


@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class CheckedScaleNfRoundingArithmeticBenchmark {

    private CheckedScaleNfRoundingArithmetic arithmetic;
    private long uDecimal1;
    private long uDecimal2;
    private int scale;
    private double doubleValue;
    private float floatValue;
    private BigDecimal bigDecimalValue;
    private String decimalString;
    private CharSequence decimalCharSeq;
    private long unscaledValue;
    private int unscaledScale;
    private int powExponent;
    private int shiftAmount;
    private int powerOf10;
    private int roundPrecision;
    private long divisorLong;

    @Setup(Level.Trial)
    public void setup() {
        // Use scale 5 (Scale5f) with HALF_UP rounding
        arithmetic = new CheckedScaleNfRoundingArithmetic(Scale5f.INSTANCE, RoundingMode.HALF_UP);
        scale = arithmetic.getScale();

        // Sample unscaled values within safe range
        uDecimal1 = 123456789L; // 1.23456789 with scale 5
        uDecimal2 = 987654321L; // 9.87654321 with scale 5

        doubleValue = 12345.6789;
        floatValue = 12345.67f;
        bigDecimalValue = new BigDecimal("12345.67890");
        decimalString = "12345.67890";
        decimalCharSeq = decimalString;

        unscaledValue = 55555555L;
        unscaledScale = 3;

        powExponent = 3;
        shiftAmount = 2;
        powerOf10 = 2;
        roundPrecision = 2;
        divisorLong = 7L;
    }

    @Benchmark
    public long benchmarkAddUnscaled() {
        return arithmetic.addUnscaled(uDecimal1, uDecimal2, scale);
    }

    @Benchmark
    public long benchmarkSubtractUnscaled() {
        return arithmetic.subtractUnscaled(uDecimal1, uDecimal2, scale);
    }

    @Benchmark
    public long benchmarkMultiplyByUnscaled() {
        return arithmetic.multiplyByUnscaled(uDecimal1, uDecimal2, scale);
    }

    @Benchmark
    public long benchmarkDivideByUnscaled() {
        return arithmetic.divideByUnscaled(uDecimal1, uDecimal2, scale);
    }

    @Benchmark
    public long benchmarkAvg() {
        return arithmetic.avg(uDecimal1, uDecimal2);
    }

    @Benchmark
    public long benchmarkInvert() {
        return arithmetic.invert(uDecimal1);
    }

    @Benchmark
    public long benchmarkMultiply() {
        return arithmetic.multiply(uDecimal1, uDecimal2);
    }

    @Benchmark
    public long benchmarkMultiplyByPowerOf10() {
        return arithmetic.multiplyByPowerOf10(uDecimal1, powerOf10);
    }

    @Benchmark
    public long benchmarkDivide() {
        return arithmetic.divide(uDecimal1, uDecimal2);
    }

    @Benchmark
    public long benchmarkDivideByLong() {
        return arithmetic.divideByLong(uDecimal1, divisorLong);
    }

    @Benchmark
    public long benchmarkDivideByPowerOf10() {
        return arithmetic.divideByPowerOf10(uDecimal1, powerOf10);
    }

    @Benchmark
    public long benchmarkSquare() {
        return arithmetic.square(uDecimal1);
    }

    @Benchmark
    public long benchmarkSqrt() {
        return arithmetic.sqrt(uDecimal1);
    }

    @Benchmark
    public long benchmarkPow() {
        return arithmetic.pow(uDecimal1, powExponent);
    }

    @Benchmark
    public long benchmarkShiftLeft() {
        return arithmetic.shiftLeft(uDecimal1, shiftAmount);
    }

    @Benchmark
    public long benchmarkShiftRight() {
        return arithmetic.shiftRight(uDecimal1, shiftAmount);
    }

    @Benchmark
    public long benchmarkRound() {
        return arithmetic.round(uDecimal1, roundPrecision);
    }

    @Benchmark
    public long benchmarkFromLong() {
        return arithmetic.fromLong(12345L);
    }

    @Benchmark
    public long benchmarkFromFloat() {
        return arithmetic.fromFloat(floatValue);
    }

    @Benchmark
    public long benchmarkFromDouble() {
        return arithmetic.fromDouble(doubleValue);
    }

    @Benchmark
    public long benchmarkFromUnscaled() {
        return arithmetic.fromUnscaled(unscaledValue, unscaledScale);
    }

    @Benchmark
    public long benchmarkFromBigDecimal() {
        return arithmetic.fromBigDecimal(bigDecimalValue);
    }

    @Benchmark
    public long benchmarkToLong() {
        return arithmetic.toLong(uDecimal1);
    }

    @Benchmark
    public float benchmarkToFloat() {
        return arithmetic.toFloat(uDecimal1);
    }

    @Benchmark
    public double benchmarkToDouble() {
        return arithmetic.toDouble(uDecimal1);
    }

    @Benchmark
    public long benchmarkToUnscaled() {
        return arithmetic.toUnscaled(uDecimal1, unscaledScale);
    }

    @Benchmark
    public long benchmarkParseString() {
        return arithmetic.parse(decimalString);
    }

    @Benchmark
    public long benchmarkParseCharSequence() {
        return arithmetic.parse(decimalCharSeq, 0, decimalCharSeq.length());
    }
}
