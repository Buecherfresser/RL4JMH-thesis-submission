package bench.generated.c001;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.decimal4j.arithmetic.CheckedScale0fTruncatingArithmetic;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class CheckedScale0fTruncatingArithmeticBenchmark {

    private CheckedScale0fTruncatingArithmetic arithmetic;

    // Inputs for arithmetic operations
    private long uDecimal1;
    private long uDecimal2;
    private int scale;
    private int exponent;
    private int precision;

    // Inputs for conversions
    private float floatInput;
    private double doubleInput;
    private BigDecimal bigDecimalInput;
    private long unscaledInput;
    private int unscaledScale;

    // Inputs for parsing
    private String stringInput;
    private CharSequence charSequenceInput;

    @Setup(Level.Trial)
    public void setup() {
        arithmetic = CheckedScale0fTruncatingArithmetic.INSTANCE;

        // Initialize common inputs
        uDecimal1 = 1234567890123L;
        uDecimal2 = 9876543210987L;
        scale = 0;
        exponent = 3;
        precision = 5;

        // Conversion inputs
        floatInput = 123.45f;
        doubleInput = 123.456789;
        bigDecimalInput = new BigDecimal("987654321.12345");
        unscaledInput = 5000L;
        unscaledScale = 10;

        // Parsing inputs
        stringInput = "123456789";
        charSequenceInput = "987654321";
    }

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
    public long testDivide(Blackhole bh) {
        long result = arithmetic.divide(uDecimal1, uDecimal2);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public long testDivideByLong(Blackhole bh) {
        long result = arithmetic.divideByLong(uDecimal1, 10L);
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
    public long testPow(Blackhole bh) {
        long result = arithmetic.pow(uDecimal1, exponent);
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
    public long testDivideByPowerOf10(Blackhole bh) {
        long result = arithmetic.divideByPowerOf10(uDecimal1, exponent);
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
    public long testShiftLeft(Blackhole bh) {
        long result = arithmetic.shiftLeft(uDecimal1, exponent);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public long testShiftRight(Blackhole bh) {
        long result = arithmetic.shiftRight(uDecimal1, exponent);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public long testRound(Blackhole bh) {
        long result = arithmetic.round(uDecimal1, precision);
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
        long result = arithmetic.toUnscaled(uDecimal1, unscaledScale);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public long testFromFloat(Blackhole bh) {
        long result = arithmetic.fromFloat(floatInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public long testFromDouble(Blackhole bh) {
        long result = arithmetic.fromDouble(doubleInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public long testFromUnscaled(Blackhole bh) {
        long result = arithmetic.fromUnscaled(unscaledInput, unscaledScale);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public long testFromBigDecimal(Blackhole bh) {
        long result = arithmetic.fromBigDecimal(bigDecimalInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public long testParseString(Blackhole bh) {
        long result = arithmetic.parse(stringInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public long testParseCharSequence(Blackhole bh) {
        long result = arithmetic.parse(charSequenceInput, 0, charSequenceInput.length());
        bh.consume(result);
        return result;
    }
}
