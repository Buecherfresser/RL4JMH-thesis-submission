package bench.generated.c006;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.decimal4j.arithmetic.UncheckedScaleNfRoundingArithmetic;
import org.decimal4j.scale.Scale5f;
import org.decimal4j.truncate.UncheckedRounding;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class UncheckedScaleNfRoundingArithmeticBenchmark {

    private UncheckedScaleNfRoundingArithmetic arithmetic;

    private long uDecimal1;
    private long uDecimal2;
    private long unscaled;
    private int otherScale;
    private long divisorLong;
    private int powExponent;
    private int shiftPositions;
    private int powerOf10;
    private int roundPrecision;
    private long fromLongValue;
    private float fromFloatValue;
    private double fromDoubleValue;
    private BigDecimal fromBigDecimalValue;
    private String parseString;
    private CharSequence parseCharSeq;
    private int parseStart;
    private int parseEnd;

    @Setup(Level.Trial)
    public void setup() {
        arithmetic = new UncheckedScaleNfRoundingArithmetic(Scale5f.INSTANCE, RoundingMode.HALF_UP);
        fromLongValue = 12345L;
        uDecimal1 = arithmetic.fromLong(fromLongValue);
        uDecimal2 = arithmetic.fromLong(6789L);
        unscaled = 55555L;
        otherScale = 3;
        divisorLong = 7L;
        powExponent = 3;
        shiftPositions = 2;
        powerOf10 = 4;
        roundPrecision = 2;
        fromFloatValue = 123.45f;
        fromDoubleValue = 12345.6789;
        fromBigDecimalValue = new BigDecimal("12345.6789");
        parseString = "12345.6789";
        parseCharSeq = parseString;
        parseStart = 0;
        parseEnd = parseString.length();
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
        return arithmetic.addUnscaled(uDecimal1, unscaled, otherScale);
    }

    @Benchmark
    public long benchmarkSubtractUnscaled() {
        return arithmetic.subtractUnscaled(uDecimal1, unscaled, otherScale);
    }

    @Benchmark
    public long benchmarkAvg() {
        return arithmetic.avg(uDecimal1, uDecimal2);
    }

    @Benchmark
    public long benchmarkMultiply() {
        return arithmetic.multiply(uDecimal1, uDecimal2);
    }

    @Benchmark
    public long benchmarkMultiplyByUnscaled() {
        return arithmetic.multiplyByUnscaled(uDecimal1, unscaled, otherScale);
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
    public long benchmarkDivideByLong() {
        return arithmetic.divideByLong(uDecimal1, divisorLong);
    }

    @Benchmark
    public long benchmarkDivideByUnscaled() {
        return arithmetic.divideByUnscaled(uDecimal1, unscaled, otherScale);
    }

    @Benchmark
    public long benchmarkDivide() {
        return arithmetic.divide(uDecimal1, uDecimal2);
    }

    @Benchmark
    public long benchmarkInvert() {
        return arithmetic.invert(uDecimal1);
    }

    @Benchmark
    public long benchmarkPow() {
        return arithmetic.pow(uDecimal1, powExponent);
    }

    @Benchmark
    public long benchmarkShiftLeft() {
        return arithmetic.shiftLeft(uDecimal1, shiftPositions);
    }

    @Benchmark
    public long benchmarkShiftRight() {
        return arithmetic.shiftRight(uDecimal1, shiftPositions);
    }

    @Benchmark
    public long benchmarkMultiplyByPowerOf10() {
        return arithmetic.multiplyByPowerOf10(uDecimal1, powerOf10);
    }

    @Benchmark
    public long benchmarkDivideByPowerOf10() {
        return arithmetic.divideByPowerOf10(uDecimal1, powerOf10);
    }

    @Benchmark
    public long benchmarkRound() {
        return arithmetic.round(uDecimal1, roundPrecision);
    }

    @Benchmark
    public long benchmarkFromLong() {
        return arithmetic.fromLong(fromLongValue);
    }

    @Benchmark
    public long benchmarkFromUnscaled() {
        return arithmetic.fromUnscaled(unscaled, otherScale);
    }

    @Benchmark
    public long benchmarkFromFloat() {
        return arithmetic.fromFloat(fromFloatValue);
    }

    @Benchmark
    public long benchmarkFromDouble() {
        return arithmetic.fromDouble(fromDoubleValue);
    }

    @Benchmark
    public long benchmarkFromBigDecimal() {
        return arithmetic.fromBigDecimal(fromBigDecimalValue);
    }

    @Benchmark
    public long benchmarkToLong() {
        return arithmetic.toLong(uDecimal1);
    }

    @Benchmark
    public long benchmarkToUnscaled() {
        return arithmetic.toUnscaled(uDecimal1, otherScale);
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
    public long benchmarkParseString() {
        return arithmetic.parse(parseString);
    }

    @Benchmark
    public long benchmarkParseCharSequence() {
        return arithmetic.parse(parseCharSeq, parseStart, parseEnd);
    }
}
