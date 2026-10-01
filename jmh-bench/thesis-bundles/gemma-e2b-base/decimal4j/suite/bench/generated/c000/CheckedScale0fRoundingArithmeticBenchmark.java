package bench.generated.c000;

import org.decimal4j.arithmetic.CheckedScale0fRoundingArithmetic;
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
public class CheckedScale0fRoundingArithmeticBenchmark {

    private CheckedScale0fRoundingArithmetic arithmetic;

    // --- Setup Data ---
    // Fixed inputs for arithmetic operations
    private long uDecimal1;
    private long uDecimal2;
    private long unscaled1;
    private long unscaled2;
    private int scale = 0;

    // Fixed inputs for conversions
    private double testDouble;
    private float testFloat;
    private BigDecimal testBigDecimal;
    private String testString;

    @Setup
    public void setup() {
        // Initialize the arithmetic object with a specific rounding mode
        this.arithmetic = new CheckedScale0fRoundingArithmetic(RoundingMode.HALF_UP);

        // Setup long inputs (non-trivial values)
        this.uDecimal1 = 123456789L;
        this.uDecimal2 = 987654321L;
        this.unscaled1 = 1000000000L;
        this.unscaled2 = 500000000L;

        // Setup conversion inputs
        this.testDouble = 12345.6789;
        this.testFloat = 12345.6789f;
        this.testBigDecimal = new BigDecimal("12345.6789");
        this.testString = "12345.6789";
    }

    // --- Arithmetic Benchmarks (Unscaled Operations) ---

    @Benchmark
    public void addUnscaled(Blackhole bh) {
        long result = arithmetic.addUnscaled(uDecimal1, unscaled1, scale);
        bh.consume(result);
    }

    @Benchmark
    public void subtractUnscaled(Blackhole bh) {
        long result = arithmetic.subtractUnscaled(uDecimal1, unscaled1, scale);
        bh.consume(result);
    }

    @Benchmark
    public void multiplyByUnscaled(Blackhole bh) {
        long result = arithmetic.multiplyByUnscaled(uDecimal1, unscaled1, scale);
        bh.consume(result);
    }

    @Benchmark
    public void divideByUnscaled(Blackhole bh) {
        long result = arithmetic.divideByUnscaled(uDecimal1, unscaled1, scale);
        bh.consume(result);
    }

    @Benchmark
    public void divide(Blackhole bh) {
        long result = arithmetic.divide(uDecimal1, uDecimal2);
        bh.consume(result);
    }

    @Benchmark
    public void multiplyByPowerOf10(Blackhole bh) {
        int n = 10;
        long result = arithmetic.multiplyByPowerOf10(uDecimal1, n);
        bh.consume(result);
    }

    @Benchmark
    public void divideByLong(Blackhole bh) {
        long lDivisor = 1000L;
        long result = arithmetic.divideByLong(uDecimal1, lDivisor);
        bh.consume(result);
    }

    @Benchmark
    public void divideByPowerOf10(Blackhole bh) {
        int n = 100;
        long result = arithmetic.divideByPowerOf10(uDecimal1, n);
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
    public void sqrt(Blackhole bh) {
        long result = arithmetic.sqrt(uDecimal1);
        bh.consume(result);
    }

    @Benchmark
    public void pow(Blackhole bh) {
        int exponent = 3;
        long result = arithmetic.pow(uDecimal1, exponent);
        bh.consume(result);
    }

    @Benchmark
    public void round(Blackhole bh) {
        int precision = 5;
        long result = arithmetic.round(uDecimal1, precision);
        bh.consume(result);
    }

    @Benchmark
    public void shiftLeft(Blackhole bh) {
        int n = 4;
        long result = arithmetic.shiftLeft(uDecimal1, n);
        bh.consume(result);
    }

    @Benchmark
    public void shiftRight(Blackhole bh) {
        int n = 2;
        long result = arithmetic.shiftRight(uDecimal1, n);
        bh.consume(result);
    }

    // --- Conversion Benchmarks ---

    @Benchmark
    public void toFloat(Blackhole bh) {
        float result = arithmetic.toFloat(uDecimal1);
        bh.consume(result);
    }

    @Benchmark
    public void toDouble(Blackhole bh) {
        double result = arithmetic.toDouble(uDecimal1);
        bh.consume(result);
    }

    @Benchmark
    public void toUnscaled(Blackhole bh) {
        long result = arithmetic.toUnscaled(uDecimal1, scale);
        bh.consume(result);
    }

    @Benchmark
    public void fromFloat(Blackhole bh) {
        long result = arithmetic.fromFloat(testFloat);
        bh.consume(result);
    }

    @Benchmark
    public void fromDouble(Blackhole bh) {
        long result = arithmetic.fromDouble(testDouble);
        bh.consume(result);
    }

    @Benchmark
    public void fromUnscaled(Blackhole bh) {
        long result = arithmetic.fromUnscaled(unscaled1, scale);
        bh.consume(result);
    }

    @Benchmark
    public void fromBigDecimal(Blackhole bh) {
        long result = arithmetic.fromBigDecimal(testBigDecimal);
        bh.consume(result);
    }

    @Benchmark
    public void parseString(Blackhole bh) {
        long result = arithmetic.parse(testString);
        bh.consume(result);
    }

    @Benchmark
    public void parseCharSequence(Blackhole bh) {
        // Test parsing a substring
        long result = arithmetic.parse(testString, 0, 5);
        bh.consume(result);
    }
}
