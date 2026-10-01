package bench.generated.c108;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

import org.decimal4j.truncate.DecimalRounding;
import org.decimal4j.truncate.TruncatedPart;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class DecimalRoundingBenchmark {

    // --- Setup State ---
    private int sign;
    private long truncatedValue;
    private TruncatedPart truncatedPart;

    @Setup(Level.Trial)
    public void setup() {
        // Setup inputs that represent different scenarios for testing calculateRoundingIncrement
        this.sign = 1;
        this.truncatedValue = 100L; // Even number
        this.truncatedPart = TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO;

        // Setup inputs for HALF_EVEN test (odd number)
        this.truncatedValue = 101L; // Odd number
        this.truncatedPart = TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO;
    }

    // --- Benchmarks for specific rounding modes ---

    @Benchmark
    public void testUpRounding(Blackhole bh) {
        // UP: Rounds away from zero. Should return sign if truncatedPart is > 0.
        int increment = DecimalRounding.UP.calculateRoundingIncrement(sign, truncatedValue, truncatedPart);
        bh.consume(increment);
    }

    @Benchmark
    public void testDownRounding(Blackhole bh) {
        // DOWN: Rounds towards zero. Should always return 0 for non-zero truncatedPart.
        int increment = DecimalRounding.DOWN.calculateRoundingIncrement(sign, truncatedValue, truncatedPart);
        bh.consume(increment);
    }

    @Benchmark
    public void testCeilingRoundingPositive(Blackhole bh) {
        // CEILING: Rounds towards positive infinity. Should return 1 if sign > 0 and truncatedPart > 0.
        this.sign = 1;
        this.truncatedValue = 101L;
        this.truncatedPart = TruncatedPart.GREATER_THAN_HALF;
        int increment = DecimalRounding.CEILING.calculateRoundingIncrement(sign, truncatedValue, truncatedPart);
        bh.consume(increment);
    }

    @Benchmark
    public void testFloorRoundingNegative(Blackhole bh) {
        // FLOOR: Rounds towards negative infinity. Should return -1 if sign < 0 and truncatedPart > 0.
        this.sign = -1;
        this.truncatedValue = -101L;
        this.truncatedPart = TruncatedPart.GREATER_THAN_HALF;
        int increment = DecimalRounding.FLOOR.calculateRoundingIncrement(sign, truncatedValue, truncatedPart);
        bh.consume(increment);
    }

    @Benchmark
    public void testHalfUpRounding(Blackhole bh) {
        // HALF_UP: Rounds to nearest, ties go up. Should return sign if truncatedPart >= 0.5.
        this.truncatedValue = 105L; // Represents a value where the fraction is >= 0.5
        this.truncatedPart = TruncatedPart.EQUAL_TO_HALF;
        int increment = DecimalRounding.HALF_UP.calculateRoundingIncrement(sign, truncatedValue, truncatedPart);
        bh.consume(increment);
    }

    @Benchmark
    public void testHalfDownRounding(Blackhole bh) {
        // HALF_DOWN: Rounds to nearest, ties go down. Should return sign if truncatedPart > 0.5.
        this.truncatedValue = 104L; // Represents a value where the fraction is < 0.5
        this.truncatedPart = TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO;
        int increment = DecimalRounding.HALF_DOWN.calculateRoundingIncrement(sign, truncatedValue, truncatedPart);
        bh.consume(increment);
    }

    @Benchmark
    public void testHalfEvenRoundingOdd(Blackhole bh) {
        // HALF_EVEN: Banker's rounding. Checks if truncatedPart >= 0.5 AND (truncatedPart > 0.5 OR value is odd).
        // Setup state is already set to truncatedValue = 101L (odd).
        this.truncatedValue = 101L;
        this.truncatedPart = TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO; // This part is < 0.5
        
        // Since truncatedPart is < 0.5, the condition (truncatedPart.isGreaterEqualHalf()) is false, so it should return 0.
        int increment = DecimalRounding.HALF_EVEN.calculateRoundingIncrement(sign, truncatedValue, truncatedPart);
        bh.consume(increment);
    }

    @Benchmark
    public void testUnnecessaryRounding(Blackhole bh) {
        // UNNECESSARY: Should throw ArithmeticException if truncatedPart > 0.
        // We must wrap this in a try-catch block to prevent JMH from failing the benchmark run,
        // but we still measure the cost of the check itself.
        try {
            DecimalRounding.UNNECESSARY.calculateRoundingIncrement(sign, truncatedValue, truncatedPart);
        } catch (ArithmeticException e) {
            // Expected behavior
        }
        // Since the method is void and throws, we consume nothing specific.
    }
}
