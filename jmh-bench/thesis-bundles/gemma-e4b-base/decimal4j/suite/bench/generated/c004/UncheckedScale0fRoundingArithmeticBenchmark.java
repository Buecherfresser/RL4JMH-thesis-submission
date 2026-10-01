package bench.generated.c004;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.decimal4j.arithmetic.UncheckedScale0fRoundingArithmetic;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class UncheckedScale0fRoundingArithmeticBenchmark {

    private UncheckedScale0fRoundingArithmetic arithmetic;

    // Inputs for arithmetic operations
    private long uDecimal1;
    private long uDecimal2;
    private long unscaled1;
    private long unscaled2;
    private int scale;
    private int positions;
    private int precision;
    private int exponent;

    // Inputs for conversions/parsing
    private BigDecimal bdInput;
    private String stringInput;
    private CharSequence charSequenceInput;

    @Setup
    public void setup() {
        // Initialize SUT with a specific rounding mode
        arithmetic = new UncheckedScale0fRoundingArithmetic(RoundingMode.HALF_UP);

        // Initialize representative long inputs
        uDecimal1 = 123456789L;
        uDecimal2 = 987654321L;
        unscaled1 = 50000L;
        unscaled2 = 1000L;

        // Initialize representative integer inputs
        scale = 5;
        positions = 3;
        precision = 2;
        exponent = 4;

        // Initialize conversion inputs
        bdInput = new BigDecimal("123.456789");
        stringInput = "987654321";
        charSequenceInput = "1234567890";
    }

    // --- Arithmetic Operations Benchmarks ---

    @Benchmark
    public long testAddUnscaled(Blackhole bh) {
        long result = arithmetic.addUnscaled(uDecimal1, unscaled1, scale);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public long testSubtractUnscaled(Blackhole bh) {
        long result = arithmetic.subtractUnscaled(uDecimal1, unscaled1, scale);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public long testMultiplyByUnscaled(Blackhole bh) {
        long result = arithmetic.multiplyByUnscaled(uDecimal1, unscaled1, scale);
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
    public long testDivideByUnscaled(Blackhole bh) {
        long result = arithmetic.divideByUnscaled(uDecimal1, unscaled1, scale);
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
    public long testDivideByPowerOf10(Blackhole bh) {
        long result = arithmetic.divideByPowerOf10(uDecimal1, positions);
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
    public long testRound(Blackhole bh) {
        long result = arithmetic.round(uDecimal1, precision);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public long testToUnscaled(Blackhole bh) {
        long result = arithmetic.toUnscaled(uDecimal1, scale);
        bh.consume(result);
        return result;
    }

    // --- Conversion Benchmarks ---

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
    public long testFromUnscaled(Blackhole bh) {
        long result = arithmetic.fromUnscaled(unscaled1, scale);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public long testFromFloat(Blackhole bh) {
        long result = arithmetic.fromFloat((float) uDecimal1);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public long testFromDouble(Blackhole bh) {
        long result = arithmetic.fromDouble((double) uDecimal1);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public long testFromBigDecimal(Blackhole bh) {
        long result = arithmetic.fromBigDecimal(bdInput);
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
        long result = arithmetic.parse(charSequenceInput, 0, 5);
        bh.consume(result);
        return result;
    }

    // --- Getter Benchmarks (Trivial but included for completeness) ---

    @Benchmark
    public RoundingMode testGetRoundingMode(Blackhole bh) {
        RoundingMode result = arithmetic.getRoundingMode();
        bh.consume(result);
        return result;
    }

    @Benchmark
    public org.decimal4j.truncate.UncheckedRounding testGetTruncationPolicy(Blackhole bh) {
        org.decimal4j.truncate.UncheckedRounding result = arithmetic.getTruncationPolicy();
        bh.consume(result);
        return result;
    }
}
