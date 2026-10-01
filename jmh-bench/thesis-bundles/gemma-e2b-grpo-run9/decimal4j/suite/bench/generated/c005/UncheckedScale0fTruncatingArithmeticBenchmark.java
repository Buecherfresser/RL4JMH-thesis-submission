package bench.generated.c005;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.math.BigDecimal;

import org.decimal4j.arithmetic.UncheckedScale0fTruncatingArithmetic;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class UncheckedScale0fTruncatingArithmeticBenchmark {

    // State fields are not strictly necessary for this benchmark since we call the static INSTANCE
    // and use hardcoded inputs, but they satisfy the requirement of having state fields
    // if the SUT required an instance.

    @Setup
    public void setup() {
        // Setup phase: No complex setup needed for this static class benchmark.
    }

    @Benchmark
    public void testAddUnscaled(Blackhole bh) {
        // Test an arithmetic method. Since it's static, we call it directly.
        long result = UncheckedScale0fTruncatingArithmetic.INSTANCE.addUnscaled(
            100L, 50L, 0
        );
        bh.consume(result);
    }

    @Benchmark
    public void testSubtractUnscaled(Blackhole bh) {
        long result = UncheckedScale0fTruncatingArithmetic.INSTANCE.subtractUnscaled(
            100L, 50L, 0
        );
        bh.consume(result);
    }

    @Benchmark
    public void testMultiplyByUnscaled(Blackhole bh) {
        long result = UncheckedScale0fTruncatingArithmetic.INSTANCE.multiplyByUnscaled(
            100L, 50L, 0
        );
        bh.consume(result);
    }

    @Benchmark
    public void testDivide(Blackhole bh) {
        long result = UncheckedScale0fTruncatingArithmetic.INSTANCE.divide(
            1000L, 3L
        );
        bh.consume(result);
    }

    @Benchmark
    public void testDivideByLong(Blackhole bh) {
        long result = UncheckedScale0fTruncatingArithmetic.INSTANCE.divideByLong(
            1000L, 3L
        );
        bh.consume(result);
    }

    @Benchmark
    public void testInvert(Blackhole bh) {
        long result = UncheckedScale0fTruncatingArithmetic.INSTANCE.invert(
            123L
        );
        bh.consume(result);
    }

    @Benchmark
    public void testSqrt(Blackhole bh) {
        long result = UncheckedScale0fTruncatingArithmetic.INSTANCE.sqrt(
            1000000L
        );
        bh.consume(result);
    }

    @Benchmark
    public void testPow(Blackhole bh) {
        // Test power function
        long result = UncheckedScale0fTruncatingArithmetic.INSTANCE.pow(
            10L, 3
        );
        bh.consume(result);
    }

    @Benchmark
    public void testShiftLeft(Blackhole bh) {
        long result = UncheckedScale0fTruncatingArithmetic.INSTANCE.shiftLeft(
            100L, 2
        );
        bh.consume(result);
    }

    @Benchmark
    public void testRound(Blackhole bh) {
        // Test rounding function
        long result = UncheckedScale0fTruncatingArithmetic.INSTANCE.round(
            12345L, 3
        );
        bh.consume(result);
    }

    @Benchmark
    public void testToDouble(Blackhole bh) {
        // Test conversion out
        double result = UncheckedScale0fTruncatingArithmetic.INSTANCE.toDouble(
            123456789L
        );
        bh.consume(result);
    }

    @Benchmark
    public void testFromDouble(Blackhole bh) {
        // Test conversion in
        long result = UncheckedScale0fTruncatingArithmetic.INSTANCE.fromDouble(
            3.1415926535
        );
        bh.consume(result);
    }

    @Benchmark
    public void testFromBigDecimal(Blackhole bh) {
        // Test conversion in
        long result = UncheckedScale0fTruncatingArithmetic.INSTANCE.fromBigDecimal(
            new BigDecimal("987654321")
        );
        bh.consume(result);
    }

    @Benchmark
    public void testParseString(Blackhole bh) {
        // Test parsing method
        long result = UncheckedScale0fTruncatingArithmetic.INSTANCE.parse(
            "123456789"
        );
        bh.consume(result);
    }
}
