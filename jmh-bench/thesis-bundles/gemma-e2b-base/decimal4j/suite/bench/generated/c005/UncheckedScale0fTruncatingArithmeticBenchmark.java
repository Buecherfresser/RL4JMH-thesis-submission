package bench.generated.c005;

import org.decimal4j.arithmetic.UncheckedScale0fTruncatingArithmetic;
import java.math.BigDecimal;
import org.decimal4j.scale.Scale0f;
import org.decimal4j.truncate.DecimalRounding;
import org.decimal4j.truncate.UncheckedRounding;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.concurrent.TimeUnit;
import java.util.Random;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class UncheckedScale0fTruncatingArithmeticBenchmark {

    private UncheckedScale0fTruncatingArithmetic arithmetic;
    private long uDecimalA;
    private long uDecimalB;
    private long unscaledA;
    private long unscaledB;
    private int scale;
    private BigDecimal bigDecimalValue;
    private String stringValue;
    private double doubleValue;
    private float floatValue;

    private static final Random RANDOM = new Random();
    private static final int SCALE = 0;

    @Setup
    public void setup() {
        arithmetic = UncheckedScale0fTruncatingArithmetic.INSTANCE;

        // Setup long inputs
        uDecimalA = RANDOM.nextLong() & 0x7FFFFFFFFFFFFFFFL; // Positive long
        uDecimalB = RANDOM.nextLong() & 0x7FFFFFFFFFFFFFFFL; // Positive long
        unscaledA = RANDOM.nextLong() & 0x7FFFFFFFFFFFFFFFL;
        unscaledB = RANDOM.nextLong() & 0x7FFFFFFFFFFFFFFFL;
        scale = SCALE;

        // Setup BigDecimal/String inputs
        bigDecimalValue = new BigDecimal("123456789012345678901234567890");
        stringValue = "123456789012345678901234567890";
        doubleValue = RANDOM.nextDouble() * 1e18;
        floatValue = (float) RANDOM.nextGaussian();
    }

    // --- Arithmetic Benchmarks ---

    @Benchmark
    public void benchmarkAddUnscaled(Blackhole bh) {
        long result = arithmetic.addUnscaled(uDecimalA, unscaledA, scale);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkSubtractUnscaled(Blackhole bh) {
        long result = arithmetic.subtractUnscaled(uDecimalA, unscaledA, scale);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkMultiplyByUnscaled(Blackhole bh) {
        long result = arithmetic.multiplyByUnscaled(uDecimalA, unscaledA, scale);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkDivide(Blackhole bh) {
        long result = arithmetic.divide(uDecimalA, uDecimalB);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkDivideByLong(Blackhole bh) {
        long result = arithmetic.divideByLong(uDecimalA, uDecimalB);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkDivideByUnscaled(Blackhole bh) {
        long result = arithmetic.divideByUnscaled(uDecimalA, unscaledA, scale);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkMultiplyByPowerOf10(Blackhole bh) {
        int positions = 10;
        long result = arithmetic.multiplyByPowerOf10(uDecimalA, positions);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkDivideByPowerOf10(Blackhole bh) {
        int positions = 5;
        long result = arithmetic.divideByPowerOf10(uDecimalA, positions);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkInvert(Blackhole bh) {
        long result = arithmetic.invert(uDecimalA);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkSqrt(Blackhole bh) {
        long result = arithmetic.sqrt(uDecimalA);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkPow(Blackhole bh) {
        int exponent = 3;
        long result = arithmetic.pow(uDecimalA, exponent);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkShiftLeft(Blackhole bh) {
        int positions = 8;
        long result = arithmetic.shiftLeft(uDecimalA, positions);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkShiftRight(Blackhole bh) {
        int positions = 4;
        long result = arithmetic.shiftRight(uDecimalA, positions);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkAvg(Blackhole bh) {
        long result = arithmetic.avg(uDecimalA, uDecimalB);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkRound(Blackhole bh) {
        int precision = 5;
        long result = arithmetic.round(uDecimalA, precision);
        bh.consume(result);
    }

    // --- Conversion Benchmarks ---

    @Benchmark
    public void benchmarkToDouble(Blackhole bh) {
        double result = arithmetic.toDouble(uDecimalA);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkToFloat(Blackhole bh) {
        float result = arithmetic.toFloat(uDecimalA);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkFromDouble(Blackhole bh) {
        long result = arithmetic.fromDouble(doubleValue);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkFromFloat(Blackhole bh) {
        long result = arithmetic.fromFloat(floatValue);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkFromBigDecimal(Blackhole bh) {
        long result = arithmetic.fromBigDecimal(bigDecimalValue);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkParseString(Blackhole bh) {
        long result = arithmetic.parse(stringValue);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkParseCharSequence(Blackhole bh) {
        int start = 10;
        int end = 20;
        long result = arithmetic.parse(stringValue, start, end);
        bh.consume(result);
    }
}
