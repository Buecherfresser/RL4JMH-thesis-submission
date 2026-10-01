package bench.generated.c007;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.math.BigDecimal;
import org.decimal4j.arithmetic.UncheckedScaleNfTruncatingArithmetic;
import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.scale.Scale5f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class UncheckedScaleNfTruncatingArithmeticBenchmark {

    private UncheckedScaleNfTruncatingArithmetic arithmetic;
    private ScaleMetrics scaleMetrics;

    // Inputs for arithmetic operations
    private long uDecimal1;
    private long uDecimal2;
    private long uDecimal3;
    private long uDecimal4;
    private long uDecimalDividend;
    private long uDecimalDivisor;
    private long uDecimalUnscaled;
    private int scale;
    private int exponent;
    private int positions;
    private int precision;

    // Inputs for conversion/parsing
    private float floatInput;
    private double doubleInput;
    private BigDecimal bigDecimalInput;
    private String stringInput;
    private CharSequence charSequenceInput;

    @Setup(Level.Trial)
    public void setup() {
        // Use Scale5f as a representative scale
        scaleMetrics = Scale5f.INSTANCE;
        arithmetic = new UncheckedScaleNfTruncatingArithmetic(scaleMetrics);

        // Initialize representative inputs
        uDecimal1 = 1234567890123L;
        uDecimal2 = 9876543210987L;
        uDecimal3 = 50000000000L;
        uDecimal4 = 10L;

        uDecimalDividend = 100000000000L;
        uDecimalDivisor = 10L;
        uDecimalUnscaled = 500000L;
        scale = 5;
        exponent = 3;
        positions = 2;
        precision = 10;

        floatInput = 123.45f;
        doubleInput = 123.45678901234567;
        bigDecimalInput = new BigDecimal("123.456789");
        stringInput = "1234567890"; // Assuming scale 0 for simple parsing test
        charSequenceInput = "1234567890";
    }

    // --- Arithmetic Benchmarks ---

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
    public long testMultiply(Blackhole bh) {
        long result = arithmetic.multiply(uDecimal1, uDecimal2);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public long testMultiplyByUnscaled(Blackhole bh) {
        long result = arithmetic.multiplyByUnscaled(uDecimal1, uDecimalUnscaled, scale);
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
    public long testDivide(Blackhole bh) {
        long result = arithmetic.divide(uDecimalDividend, uDecimalDivisor);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public long testDivideByLong(Blackhole bh) {
        long result = arithmetic.divideByLong(uDecimalDividend, uDecimalDivisor);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public long testDivideByUnscaled(Blackhole bh) {
        long result = arithmetic.divideByUnscaled(uDecimal1, uDecimalUnscaled, scale);
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
    public long testAvg(Blackhole bh) {
        long result = arithmetic.avg(uDecimal1, uDecimal2);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public long testMultiplyByPowerOf10(Blackhole bh) {
        long result = arithmetic.multiplyByPowerOf10(uDecimal1, positions);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public long testDivideByPowerOf10(Blackhole bh) {
        long result = arithmetic.divideByPowerOf10(uDecimal1, positions);
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
        long result = arithmetic.shiftLeft(uDecimal1, positions);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public long testShiftRight(Blackhole bh) {
        long result = arithmetic.shiftRight(uDecimal1, positions);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public long testRound(Blackhole bh) {
        long result = arithmetic.round(uDecimal1, precision);
        bh.consume(result);
        return result;
    }

    // --- Conversion Benchmarks ---

    @Benchmark
    public long testFromLong(Blackhole bh) {
        long result = arithmetic.fromLong(uDecimal1);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public long testFromUnscaled(Blackhole bh) {
        long result = arithmetic.fromUnscaled(uDecimalUnscaled, scale);
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
    public long testFromBigDecimal(Blackhole bh) {
        long result = arithmetic.fromBigDecimal(bigDecimalInput);
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
    public long testToUnscaled(Blackhole bh) {
        long result = arithmetic.toUnscaled(uDecimal1, scale);
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
