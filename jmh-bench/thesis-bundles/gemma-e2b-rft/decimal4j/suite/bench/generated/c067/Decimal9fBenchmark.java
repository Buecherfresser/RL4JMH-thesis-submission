package bench.generated.c067;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.concurrent.TimeUnit;

import org.decimal4j.api.Decimal;
import org.decimal4j.api.DecimalArithmetic;
import org.decimal4j.immutable.Decimal9f;
import org.decimal4j.exact.Multipliable9f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 5, time = 2)
@Measurement(iterations = 5, time = 2)
public class Decimal9fBenchmark {

    // --- Setup State ---
    private Decimal9f testDecimal;
    private long testLongValue;
    private double testDoubleValue;
    private BigDecimal testBigDecimalValue;
    private BigInteger testBigIntegerValue;
    private String testStringValue;
    private Decimal<?> testDecimalValue;

    // Constants for setup
    private static final long LONG_INPUT = 1234567890123L;
    private static final double DOUBLE_INPUT = 1234567890123.456789;
    private static final BigDecimal BIG_DECIMAL_INPUT = new BigDecimal("1234567890123.456789");
    private static final BigInteger BIG_INTEGER_INPUT = new BigInteger("1234567890123456789");
    private static final String STRING_INPUT = "1234567890123.456789";

    @Setup
    public void setup() {
        // Initialize inputs once per trial
        testLongValue = LONG_INPUT;
        testDoubleValue = DOUBLE_INPUT;
        testBigDecimalValue = BIG_DECIMAL_INPUT;
        testBigIntegerValue = BIG_INTEGER_INPUT;
        testStringValue = STRING_INPUT;

        // Create a base Decimal9f instance from a long
        testDecimal = Decimal9f.valueOf(testLongValue);
        
        // Create a Decimal9f instance from a double
        testDecimal = Decimal9f.valueOf(testDoubleValue);
        
        // Create a Decimal9f instance from a BigDecimal
        testDecimal = Decimal9f.valueOf(testBigDecimalValue);
        
        // Create a Decimal9f instance from a BigInteger
        testDecimal = Decimal9f.valueOf(testBigIntegerValue);

        // Create a Decimal9f instance from a String
        testDecimal = Decimal9f.valueOf(testStringValue);
        
        // Create a generic Decimal for testing generic methods
        testDecimalValue = testDecimal;
    }

    // --- Construction/Factory Benchmarks ---

    @Benchmark
    public void constructFromLong(Blackhole bh) {
        Decimal9f result = Decimal9f.valueOf(testLongValue);
        bh.consume(result);
    }

    @Benchmark
    public void constructFromDouble(Blackhole bh) {
        Decimal9f result = Decimal9f.valueOf(testDoubleValue);
        bh.consume(result);
    }

    @Benchmark
    public void constructFromBigDecimal(Blackhole bh) {
        Decimal9f result = Decimal9f.valueOf(testBigDecimalValue);
        bh.consume(result);
    }

    @Benchmark
    public void constructFromString(Blackhole bh) {
        Decimal9f result = Decimal9f.valueOf(testStringValue);
        bh.consume(result);
    }

    @Benchmark
    public void constructFromUnscaledLong(Blackhole bh) {
        Decimal9f result = Decimal9f.valueOfUnscaled(testLongValue);
        bh.consume(result);
    }

    // --- Arithmetic Benchmarks (Immutable) ---

    @Benchmark
    public void add(Blackhole bh) {
        Decimal9f result = testDecimal.add(Decimal9f.ONE);
        bh.consume(result);
    }

    @Benchmark
    public void subtract(Blackhole bh) {
        Decimal9f result = testDecimal.subtract(Decimal9f.TWO);
        bh.consume(result);
    }

    @Benchmark
    public void multiplyExact(Blackhole bh) {
        // Test the specialized exact multiplication. Returns Multipliable9f, consume it.
        bh.consume(testDecimal.multiplyExact());
    }

    @Benchmark
    public void negate(Blackhole bh) {
        Decimal9f result = testDecimal.negate();
        bh.consume(result);
    }

    @Benchmark
    public void abs(Blackhole bh) {
        Decimal9f result = testDecimal.abs();
        bh.consume(result);
    }

    @Benchmark
    public void square(Blackhole bh) {
        Decimal9f result = testDecimal.square();
        bh.consume(result);
    }

    @Benchmark
    public void sqrt(Blackhole bh) {
        Decimal9f result = testDecimal.sqrt();
        bh.consume(result);
    }

    @Benchmark
    public void pow(Blackhole bh) {
        // Test pow(int)
        Decimal9f result = testDecimal.pow(3);
        bh.consume(result);
    }

    // --- Conversion Benchmarks ---

    @Benchmark
    public void toBigDecimal(Blackhole bh) {
        BigDecimal result = testDecimal.toBigDecimal();
        bh.consume(result);
    }

    @Benchmark
    public void toString(Blackhole bh) {
        String result = testDecimal.toString();
        bh.consume(result);
    }

    @Benchmark
    public void unscaledValue(Blackhole bh) {
        long unscaled = testDecimal.unscaledValue();
        bh.consume(unscaled);
    }

    // --- Rounding/Conversion Benchmarks ---

    @Benchmark
    public void valueOfDoubleWithRounding(Blackhole bh) {
        // Test rounding using HALF_UP (default for double conversion)
        Decimal9f result = Decimal9f.valueOf(testDoubleValue, RoundingMode.HALF_UP);
        bh.consume(result);
    }

    @Benchmark
    public void valueOfBigDecimalWithRounding(Blackhole bh) {
        // Test rounding using HALF_EVEN
        Decimal9f result = Decimal9f.valueOf(testBigDecimalValue, RoundingMode.HALF_EVEN);
        bh.consume(result);
    }
}
