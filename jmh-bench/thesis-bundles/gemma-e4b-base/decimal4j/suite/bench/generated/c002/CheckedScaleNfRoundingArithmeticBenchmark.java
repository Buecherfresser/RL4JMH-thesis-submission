package bench.generated.c002;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.arithmetic.CheckedScaleNfRoundingArithmetic;
import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.scale.Scale5f;
import java.math.RoundingMode;
import java.math.BigDecimal;
import org.decimal4j.truncate.DecimalRounding;
import org.decimal4j.truncate.CheckedRounding;
import org.decimal4j.truncate.OverflowMode;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class CheckedScaleNfRoundingArithmeticBenchmark {

    private CheckedScaleNfRoundingArithmetic arithmetic;

    // Representative inputs
    private long uDecimal1;
    private long uDecimal2;
    private long uDecimal3;
    private long uDecimal4;
    private long uDecimal5;
    private long uDecimal6;
    private int scale;
    private int precision;
    private int exponent;
    private int shiftAmount;
    private String testString;
    private CharSequence testCharSequence;
    private float testFloat;
    private double testDouble;
    private BigDecimal testBigDecimal;

    @Setup(Level.Trial)
    public void setup() {
        // Initialize the SUT using Scale5f and HALF_UP rounding
        ScaleMetrics scaleMetrics = Scale5f.INSTANCE;
        RoundingMode roundingMode = RoundingMode.HALF_UP;
        arithmetic = new CheckedScaleNfRoundingArithmetic(scaleMetrics, roundingMode);

        // Initialize representative inputs
        uDecimal1 = 1234567890123L;
        uDecimal2 = 9876543210987L;
        uDecimal3 = 500000000000L;
        uDecimal4 = 100000000000L;
        uDecimal5 = 1L;
        uDecimal6 = 2L;
        
        scale = 5;
        precision = 3;
        exponent = 2;
        shiftAmount = 3;

        testString = "1234567890";
        testCharSequence = "1234567890";
        
        testFloat = 123.45f;
        testDouble = 123.456789;
        
        testBigDecimal = new BigDecimal("123.456789");
    }

    // --- Getters ---

    @Benchmark
    public RoundingMode testGetRoundingMode(Blackhole bh) {
        return arithmetic.getRoundingMode();
    }

    @Benchmark
    public CheckedRounding testGetTruncationPolicy(Blackhole bh) {
        return arithmetic.getTruncationPolicy();
    }

    // --- Arithmetic Operations ---

    @Benchmark
    public long testAddUnscaled(Blackhole bh) {
        long result = arithmetic.addUnscaled(uDecimal1, uDecimal2, scale);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public long testSubtractUnscaled(Blackhole bh) {
        long result = arithmetic.subtractUnscaled(uDecimal1, uDecimal2, scale);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public long testMultiplyByUnscaled(Blackhole bh) {
        long result = arithmetic.multiplyByUnscaled(uDecimal1, uDecimal2, scale);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public long testDivideByUnscaled(Blackhole bh) {
        long result = arithmetic.divideByUnscaled(uDecimal1, uDecimal2, scale);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public long testAvg(Blackhole bh) {
        long result = arithmetic.avg(uDecimal1, uDecimal2);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public long testInvert(Blackhole bh) {
        long result = arithmetic.invert(uDecimal1);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public long testMultiply(Blackhole bh) {
        long result = arithmetic.multiply(uDecimal1, uDecimal2);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public long testMultiplyByPowerOf10(Blackhole bh) {
        long result = arithmetic.multiplyByPowerOf10(uDecimal1, exponent);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public long testDivide(Blackhole bh) {
        long result = arithmetic.divide(uDecimal1, uDecimal2);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public long testDivideByLong(Blackhole bh) {
        long result = arithmetic.divideByLong(uDecimal1, uDecimal2);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public long testDivideByPowerOf10(Blackhole bh) {
        long result = arithmetic.divideByPowerOf10(uDecimal1, exponent);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public long testSquare(Blackhole bh) {
        long result = arithmetic.square(uDecimal1);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public long testSqrt(Blackhole bh) {
        long result = arithmetic.sqrt(uDecimal1);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public long testPow(Blackhole bh) {
        long result = arithmetic.pow(uDecimal1, exponent);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public long testShiftLeft(Blackhole bh) {
        long result = arithmetic.shiftLeft(uDecimal1, shiftAmount);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public long testShiftRight(Blackhole bh) {
        long result = arithmetic.shiftRight(uDecimal1, shiftAmount);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public long testRound(Blackhole bh) {
        long result = arithmetic.round(uDecimal1, precision);
        bh.consume(result);
        return result;
    }

    // --- Conversion Operations ---

    @Benchmark
    public long testFromLong(Blackhole bh) {
        long result = arithmetic.fromLong(uDecimal1);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public long testFromFloat(Blackhole bh) {
        long result = arithmetic.fromFloat(testFloat);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public long testFromDouble(Blackhole bh) {
        long result = arithmetic.fromDouble(testDouble);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public long testFromUnscaled(Blackhole bh) {
        long result = arithmetic.fromUnscaled(uDecimal1, scale);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public long testFromBigDecimal(Blackhole bh) {
        long result = arithmetic.fromBigDecimal(testBigDecimal);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public long testToLong(Blackhole bh) {
        long result = arithmetic.toLong(uDecimal1);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public float testToFloat(Blackhole bh) {
        float result = arithmetic.toFloat(uDecimal1);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public double testToDouble(Blackhole bh) {
        double result = arithmetic.toDouble(uDecimal1);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public long testToUnscaled(Blackhole bh) {
        long result = arithmetic.toUnscaled(uDecimal1, scale);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public long testParseString(Blackhole bh) {
        long result = arithmetic.parse(testString);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public long testParseCharSequence(Blackhole bh) {
        long result = arithmetic.parse(testCharSequence, 0, testCharSequence.length());
        bh.consume(result);
        return result;
    }
}
