package bench.generated.c004;

import org.decimal4j.arithmetic.UncheckedScale0fRoundingArithmetic;
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
public class UncheckedScale0fRoundingArithmeticBenchmark {

    private UncheckedScale0fRoundingArithmetic arithmetic;

    // --- Setup State ---
    @Setup
    public void setup() {
        // Initialize the arithmetic object with a specific rounding mode (e.g., HALF_UP)
        this.arithmetic = new UncheckedScale0fRoundingArithmetic(RoundingMode.HALF_UP);
    }

    // --- Input Data (Built in Setup/State) ---
    // These values are chosen to be large enough to test long arithmetic but small enough
    // to ensure fast execution.
    private final long uDecimal1 = 123456789012345L;
    private final long uDecimal2 = 987654321098765L;
    private final long unscaledValue = 12345L;
    private final int scale = 5;
    private final long largeLong = 9223372036854775807L; // Long.MAX_VALUE
    private final float testFloat = 3.14159f;
    private final double testDouble = 123456789012345.6789;
    private final BigDecimal bigDecimalValue = new BigDecimal("123456789012345.6789");
    private final String parseString = "123456789012345";
    private final long parseLong = 123456789012345L;


    // --- Arithmetic Operations Benchmarks ---

    @Benchmark
    public void testAddUnscaled(Blackhole bh) {
        long result = arithmetic.addUnscaled(uDecimal1, unscaledValue, scale);
        bh.consume(result);
    }

    @Benchmark
    public void testSubtractUnscaled(Blackhole bh) {
        long result = arithmetic.subtractUnscaled(uDecimal1, unscaledValue, scale);
        bh.consume(result);
    }

    @Benchmark
    public void testMultiplyByUnscaled(Blackhole bh) {
        long result = arithmetic.multiplyByUnscaled(uDecimal1, unscaledValue, scale);
        bh.consume(result);
    }

    @Benchmark
    public void testDivide(Blackhole bh) {
        long result = arithmetic.divide(uDecimal1, uDecimal2);
        bh.consume(result);
    }

    @Benchmark
    public void testDivideByLong(Blackhole bh) {
        long result = arithmetic.divideByLong(uDecimal1, 1000L);
        bh.consume(result);
    }

    @Benchmark
    public void testDivideByUnscaled(Blackhole bh) {
        long result = arithmetic.divideByUnscaled(uDecimal1, unscaledValue, scale);
        bh.consume(result);
    }

    @Benchmark
    public void testAvg(Blackhole bh) {
        long result = arithmetic.avg(uDecimal1, uDecimal2);
        bh.consume(result);
    }

    @Benchmark
    public void testInvert(Blackhole bh) {
        long result = arithmetic.invert(uDecimal1);
        bh.consume(result);
    }

    @Benchmark
    public void testShiftLeft(Blackhole bh) {
        long result = arithmetic.shiftLeft(uDecimal1, 10);
        bh.consume(result);
    }

    @Benchmark
    public void testShiftRight(Blackhole bh) {
        long result = arithmetic.shiftRight(uDecimal1, 5);
        bh.consume(result);
    }

    @Benchmark
    public void testDivideByPowerOf10(Blackhole bh) {
        long result = arithmetic.divideByPowerOf10(uDecimal1, 3);
        bh.consume(result);
    }

    @Benchmark
    public void testMultiplyByPowerOf10(Blackhole bh) {
        long result = arithmetic.multiplyByPowerOf10(uDecimal1, 2);
        bh.consume(result);
    }

    @Benchmark
    public void testSqrt(Blackhole bh) {
        long result = arithmetic.sqrt(largeLong);
        bh.consume(result);
    }

    @Benchmark
    public void testPow(Blackhole bh) {
        long result = arithmetic.pow(uDecimal1, 3);
        bh.consume(result);
    }

    @Benchmark
    public void testRound(Blackhole bh) {
        long result = arithmetic.round(uDecimal1, 5);
        bh.consume(result);
    }

    // --- Conversion Benchmarks ---

    @Benchmark
    public void testToFloat(Blackhole bh) {
        float result = arithmetic.toFloat(uDecimal1);
        bh.consume(result);
    }

    @Benchmark
    public void testToDouble(Blackhole bh) {
        double result = arithmetic.toDouble(uDecimal1);
        bh.consume(result);
    }

    @Benchmark
    public void testFromUnscaled(Blackhole bh) {
        long result = arithmetic.fromUnscaled(unscaledValue, scale);
        bh.consume(result);
    }

    @Benchmark
    public void testFromFloat(Blackhole bh) {
        long result = arithmetic.fromFloat(testFloat);
        bh.consume(result);
    }

    @Benchmark
    public void testFromDouble(Blackhole bh) {
        long result = arithmetic.fromDouble(testDouble);
        bh.consume(result);
    }

    @Benchmark
    public void testFromBigDecimal(Blackhole bh) {
        long result = arithmetic.fromBigDecimal(bigDecimalValue);
        bh.consume(result);
    }

    @Benchmark
    public void testParseString(Blackhole bh) {
        long result = arithmetic.parse(parseString);
        bh.consume(result);
    }

    @Benchmark
    public void testParseCharSequence(Blackhole bh) {
        // Test parsing a substring
        long result = arithmetic.parse(parseString.substring(0, 10));
        bh.consume(result);
    }
}
