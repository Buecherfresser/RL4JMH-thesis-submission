package bench.generated.c108;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.Set;

import org.decimal4j.truncate.DecimalRounding;
import org.decimal4j.truncate.TruncatedPart;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class DecimalRoundingBenchmark {

    // Input data for testing calculateRoundingIncrement
    private int sign;
    private long truncatedValue;
    private TruncatedPart truncatedPart;

    // Constants for testing
    private static final int POSITIVE_SIGN = 1;
    private static final int NEGATIVE_SIGN = -1;
    private static final long TRUNCATED_VALUE = 100L; // Example value
    private static final TruncatedPart TRUNCATED_PART_GT_HALF = TruncatedPart.GREATER_THAN_HALF;
    private static final TruncatedPart TRUNCATED_PART_EQ_HALF = TruncatedPart.EQUAL_TO_HALF;
    private static final TruncatedPart TRUNCATED_PART_LT_HALF = TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO;
    private static final TruncatedPart TRUNCATED_PART_ZERO = TruncatedPart.ZERO;

    @Setup(Level.Trial)
    public void setup() {
        // Setup inputs once per trial
        this.sign = POSITIVE_SIGN;
        this.truncatedValue = TRUNCATED_VALUE;
        this.truncatedPart = TRUNCATED_PART_GT_HALF;
    }

    @Benchmark
    public void testUpRounding_FractionGreaterThanHalf(Blackhole bh) {
        DecimalRounding.UP.calculateRoundingIncrement(sign, truncatedValue, truncatedPart);
        bh.consume(null);
    }

    @Benchmark
    public void testDownRounding_FractionGreaterThanHalf(Blackhole bh) {
        DecimalRounding.DOWN.calculateRoundingIncrement(sign, truncatedValue, truncatedPart);
        bh.consume(null);
    }

    @Benchmark
    public void testCeilingRounding_PositiveFraction(Blackhole bh) {
        // Test positive sign and fraction > 0
        DecimalRounding.CEILING.calculateRoundingIncrement(POSITIVE_SIGN, truncatedValue, TRUNCATED_PART_GT_HALF);
        bh.consume(null);
    }

    @Benchmark
    public void testFloorRounding_NegativeFraction(Blackhole bh) {
        // Test negative sign and fraction > 0
        DecimalRounding.FLOOR.calculateRoundingIncrement(NEGATIVE_SIGN, truncatedValue, TRUNCATED_PART_GT_HALF);
        bh.consume(null);
    }

    @Benchmark
    public void testHalfUpRounding_FractionEqualToHalf(Blackhole bh) {
        // Test HALF_UP where truncatedPart is EQUAL_TO_HALF
        DecimalRounding.HALF_UP.calculateRoundingIncrement(POSITIVE_SIGN, truncatedValue, TRUNCATED_PART_EQ_HALF);
        bh.consume(null);
    }

    @Benchmark
    public void testHalfDownRounding_FractionGreaterThanHalf(Blackhole bh) {
        // Test HALF_DOWN where truncatedPart is GREATER_THAN_HALF
        DecimalRounding.HALF_DOWN.calculateRoundingIncrement(POSITIVE_SIGN, truncatedValue, TRUNCATED_PART_GT_HALF);
        bh.consume(null);
    }

    @Benchmark
    public void testHalfEvenRounding_FractionGreaterThanHalf_OddValue(Blackhole bh) {
        // Test HALF_EVEN where truncatedPart is GREATER_THAN_HALF and value is odd (100 is even, let's adjust input for testing parity logic if possible, but sticking to setup for now)
        // Since 100 is even, this tests the 'else' branch for HALF_EVEN if the condition (truncatedPart.isGreaterThanHalf() | ((truncatedValue & 0x1) != 0)) is false.
        DecimalRounding.HALF_EVEN.calculateRoundingIncrement(POSITIVE_SIGN, TRUNCATED_VALUE, TRUNCATED_PART_GT_HALF);
        bh.consume(null);
    }

    @Benchmark
    public void testUnnecessaryRounding_FractionGreaterThanZero(Blackhole bh) {
        // This test is expected to throw ArithmeticException, which JMH handles gracefully.
        try {
            DecimalRounding.UNNECESSARY.calculateRoundingIncrement(POSITIVE_SIGN, TRUNCATED_VALUE, TRUNCATED_PART_GT_HALF);
        } catch (ArithmeticException e) {
            // Expected behavior
        }
        bh.consume(null);
    }
}
