package bench.generated.c001;

import org.decimal4j.arithmetic.CheckedScale0fTruncatingArithmetic;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

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
    private long unscaled1;
    private int scale1;
    private long uDecimal2;
    private long unscaled2;
    private int scale2;

    // Inputs for conversions
    private long testLong;
    private double testDouble;
    private BigDecimal testBigDecimal;
    private String testString;

    @Setup
    public void setup() {
        arithmetic = CheckedScale0fTruncatingArithmetic.INSTANCE;

        // Setup inputs: Use large, non-trivial long values
        uDecimal1 = 123456789012345L;
        unscaled1 = 987654321098765L;
        scale1 = 10;

        uDecimal2 = 500000000000000L;
        unscaled2 = 123456789012345L;
        scale2 = 5;

        testLong = 9223372036854775807L; // Long.MAX_VALUE
        testDouble = 3.141592653589793;
        testBigDecimal = new BigDecimal("123456789012345.6789");
        testString = "123456789012345";
    }

    // --- Arithmetic Operations ---

    @Benchmark
    public void addUnscaled(Blackhole bh) {
        long result = arithmetic.addUnscaled(uDecimal1, unscaled1, scale1);
        bh.consume(result);
    }

    @Benchmark
    public void subtractUnscaled(Blackhole bh) {
        long result = arithmetic.subtractUnscaled(uDecimal1, unscaled1, scale1);
        bh.consume(result);
    }

    @Benchmark
    public void multiplyByUnscaled(Blackhole bh) {
        long result = arithmetic.multiplyByUnscaled(uDecimal1, unscaled1, scale1);
        bh.consume(result);
    }

    @Benchmark
    public void divideByUnscaled(Blackhole bh) {
        long result = arithmetic.divideByUnscaled(uDecimal1, unscaled1, scale1);
        bh.consume(result);
    }

    @Benchmark
    public void divide(Blackhole bh) {
        long result = arithmetic.divide(uDecimal1, uDecimal2);
        bh.consume(result);
    }

    @Benchmark
    public void divideByLong(Blackhole bh) {
        long result = arithmetic.divideByLong(uDecimal1, unscaled2);
        bh.consume(result);
    }

    @Benchmark
    public void avg(Blackhole bh) {
        long result = arithmetic.avg(uDecimal1, uDecimal2);
        bh.consume(result);
    }

    @Benchmark
    public void invert(Blackhole bh) {
        long result = arithmetic.invert(uDecimal1);
        bh.consume(result);
    }

    @Benchmark
    public void pow(Blackhole bh) {
        long result = arithmetic.pow(uDecimal1, 3);
        bh.consume(result);
    }

    @Benchmark
    public void sqrt(Blackhole bh) {
        long result = arithmetic.sqrt(uDecimal1);
        bh.consume(result);
    }

    @Benchmark
    public void divideByPowerOf10(Blackhole bh) {
        int n = 15;
        long result = arithmetic.divideByPowerOf10(uDecimal1, n);
        bh.consume(result);
    }

    @Benchmark
    public void multiplyByPowerOf10(Blackhole bh) {
        int n = 10;
        long result = arithmetic.multiplyByPowerOf10(uDecimal1, n);
        bh.consume(result);
    }

    @Benchmark
    public void shiftLeft(Blackhole bh) {
        int positions = 5;
        long result = arithmetic.shiftLeft(uDecimal1, positions);
        bh.consume(result);
    }

    @Benchmark
    public void shiftRight(Blackhole bh) {
        int positions = 3;
        long result = arithmetic.shiftRight(uDecimal1, positions);
        bh.consume(result);
    }

    @Benchmark
    public void round(Blackhole bh) {
        int precision = 5;
        long result = arithmetic.round(uDecimal1, precision);
        bh.consume(result);
    }

    // --- Conversions ---

    @Benchmark
    public void toFloat(Blackhole bh) {
        float result = arithmetic.toFloat(testLong);
        bh.consume(result);
    }

    @Benchmark
    public void toDouble(Blackhole bh) {
        double result = arithmetic.toDouble(testLong);
        bh.consume(result);
    }

    @Benchmark
    public void fromFloat(Blackhole bh) {
        long result = arithmetic.fromFloat((float) 123.45f);
        bh.consume(result);
    }

    @Benchmark
    public void fromDouble(Blackhole bh) {
        long result = arithmetic.fromDouble(testDouble);
        bh.consume(result);
    }

    @Benchmark
    public void fromBigDecimal(Blackhole bh) {
        long result = arithmetic.fromBigDecimal(testBigDecimal);
        bh.consume(result);
    }

    @Benchmark
    public void toUnscaled(Blackhole bh) {
        long result = arithmetic.toUnscaled(uDecimal1, scale1);
        bh.consume(result);
    }

    @Benchmark
    public void fromUnscaled(Blackhole bh) {
        long result = arithmetic.fromUnscaled(unscaled1, scale1);
        bh.consume(result);
    }

    // --- Parsing ---

    @Benchmark
    public void parseString(Blackhole bh) {
        long result = arithmetic.parse(testString);
        bh.consume(result);
    }

    @Benchmark
    public void parseCharSequence(Blackhole bh) {
        int start = 0;
        int end = testString.length();
        // Fix: Use substring to ensure we pass a CharSequence instead of a primitive char
        String charSequence = testString.substring(start, end);
        long result = arithmetic.parse(charSequence, start, end);
        bh.consume(result);
    }
}
