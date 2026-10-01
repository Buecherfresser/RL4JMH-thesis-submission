package bench.generated.c007;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.arithmetic.UncheckedScaleNfTruncatingArithmetic;
import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.scale.Scale5f;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.decimal4j.truncate.UncheckedRounding;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class UncheckedScaleNfTruncatingArithmeticBenchmark {

    private UncheckedScaleNfTruncatingArithmetic arithmetic;

    // Sample operands
    private long uDecimal;
    private long uDecimal2;
    private long unscaledOther;
    private int otherScale;
    private long divisorLong;
    private int positions;
    private int exponent;
    private int precision;
    private float fVal;
    private double dVal;
    private BigDecimal bdVal;
    private String strVal;
    private String csVal;
    private int csStart;
    private int csEnd;

    @Setup(Level.Trial)
    public void setup() {
        ScaleMetrics scaleMetrics = Scale5f.INSTANCE;
        arithmetic = new UncheckedScaleNfTruncatingArithmetic(scaleMetrics);

        // Values chosen to stay within range for scale 5
        uDecimal = 12_345_678L;          // represents 123.45678
        uDecimal2 = 23_456_789L;         // represents 234.56789
        unscaledOther = 7_654_321L;      // represents 76.54321
        otherScale = 3;                  // different scale for cross‑scale ops
        divisorLong = 123L;
        positions = 2;                   // multiply/divide by 10^2
        exponent = 3;                    // power of 3
        precision = 4;                   // rounding precision
        fVal = 12.345f;
        dVal = 12.3456789;
        bdVal = new BigDecimal("12.34567");
        strVal = "123.4567";
        csVal = "987.6543";
        csStart = 0;
        csEnd = csVal.length();
    }

    @Benchmark
    public RoundingMode benchmarkGetRoundingMode() {
        return arithmetic.getRoundingMode();
    }

    @Benchmark
    public UncheckedRounding benchmarkGetTruncationPolicy() {
        return arithmetic.getTruncationPolicy();
    }

    @Benchmark
    public long benchmarkAddUnscaled() {
        return arithmetic.addUnscaled(uDecimal, unscaledOther, otherScale);
    }

    @Benchmark
    public long benchmarkSubtractUnscaled() {
        return arithmetic.subtractUnscaled(uDecimal, unscaledOther, otherScale);
    }

    @Benchmark
    public long benchmarkMultiply() {
        return arithmetic.multiply(uDecimal, uDecimal2);
    }

    @Benchmark
    public long benchmarkMultiplyByUnscaled() {
        return arithmetic.multiplyByUnscaled(uDecimal, unscaledOther, otherScale);
    }

    @Benchmark
    public long benchmarkSquare() {
        return arithmetic.square(uDecimal);
    }

    @Benchmark
    public long benchmarkSqrt() {
        return arithmetic.sqrt(uDecimal);
    }

    @Benchmark
    public long benchmarkDivide() {
        return arithmetic.divide(uDecimal, uDecimal2);
    }

    @Benchmark
    public long benchmarkDivideByLong() {
        return arithmetic.divideByLong(uDecimal, divisorLong);
    }

    @Benchmark
    public long benchmarkDivideByUnscaled() {
        return arithmetic.divideByUnscaled(uDecimal, unscaledOther, otherScale);
    }

    @Benchmark
    public long benchmarkInvert() {
        return arithmetic.invert(uDecimal);
    }

    @Benchmark
    public long benchmarkAvg() {
        return arithmetic.avg(uDecimal, uDecimal2);
    }

    @Benchmark
    public long benchmarkMultiplyByPowerOf10() {
        return arithmetic.multiplyByPowerOf10(uDecimal, positions);
    }

    @Benchmark
    public long benchmarkDivideByPowerOf10() {
        return arithmetic.divideByPowerOf10(uDecimal, positions);
    }

    @Benchmark
    public long benchmarkPow() {
        return arithmetic.pow(uDecimal, exponent);
    }

    @Benchmark
    public long benchmarkShiftLeft() {
        return arithmetic.shiftLeft(uDecimal, positions);
    }

    @Benchmark
    public long benchmarkShiftRight() {
        return arithmetic.shiftRight(uDecimal, positions);
    }

    @Benchmark
    public long benchmarkRound() {
        return arithmetic.round(uDecimal, precision);
    }

    @Benchmark
    public long benchmarkFromLong() {
        return arithmetic.fromLong(12345L);
    }

    @Benchmark
    public long benchmarkFromUnscaled() {
        return arithmetic.fromUnscaled(unscaledOther, otherScale);
    }

    @Benchmark
    public long benchmarkFromFloat() {
        return arithmetic.fromFloat(fVal);
    }

    @Benchmark
    public long benchmarkFromDouble() {
        return arithmetic.fromDouble(dVal);
    }

    @Benchmark
    public long benchmarkFromBigDecimal() {
        return arithmetic.fromBigDecimal(bdVal);
    }

    @Benchmark
    public long benchmarkToLong() {
        return arithmetic.toLong(uDecimal);
    }

    @Benchmark
    public long benchmarkToUnscaled() {
        return arithmetic.toUnscaled(uDecimal, otherScale);
    }

    @Benchmark
    public float benchmarkToFloat() {
        return arithmetic.toFloat(uDecimal);
    }

    @Benchmark
    public double benchmarkToDouble() {
        return arithmetic.toDouble(uDecimal);
    }

    @Benchmark
    public long benchmarkParseString() {
        return arithmetic.parse(strVal);
    }

    @Benchmark
    public long benchmarkParseCharSequence() {
        return arithmetic.parse(csVal, csStart, csEnd);
    }
}
