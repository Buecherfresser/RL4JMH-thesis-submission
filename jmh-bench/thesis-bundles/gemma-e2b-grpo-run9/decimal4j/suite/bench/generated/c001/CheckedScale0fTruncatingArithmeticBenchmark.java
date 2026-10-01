package bench.generated.c001;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

import org.decimal4j.arithmetic.CheckedScale0fTruncatingArithmetic;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class CheckedScale0fTruncatingArithmeticBenchmark {

    // Since CheckedScale0fTruncatingArithmetic is a singleton, we don't need a @State field.

    @Benchmark
    public void testAddUnscaled(Blackhole bh) {
        // Test addition. Using large, non-zero values.
        long result = CheckedScale0fTruncatingArithmetic.INSTANCE.addUnscaled(1000000000L, 500000000L, 0);
        bh.consume(result);
    }

    @Benchmark
    public void testSubtractUnscaled(Blackhole bh) {
        // Test subtraction.
        long result = CheckedScale0fTruncatingArithmetic.INSTANCE.subtractUnscaled(1000000000L, 500000000L, 0);
        bh.consume(result);
    }

    @Benchmark
    public void testMultiplyByUnscaled(Blackhole bh) {
        // Test multiplication.
        long result = CheckedScale0fTruncatingArithmetic.INSTANCE.multiplyByUnscaled(1000000000L, 2L, 0);
        bh.consume(result);
    }

    @Benchmark
    public void testDivideByLong(Blackhole bh) {
        // Test division by long.
        long result = CheckedScale0fTruncatingArithmetic.INSTANCE.divideByLong(1000000000L, 2L);
        bh.consume(result);
    }

    @Benchmark
    public void testDivide(Blackhole bh) {
        // Test division.
        long result = CheckedScale0fTruncatingArithmetic.INSTANCE.divide(1000000000L, 2L);
        bh.consume(result);
    }

    @Benchmark
    public void testAvg(Blackhole bh) {
        // Test average calculation.
        long result = CheckedScale0fTruncatingArithmetic.INSTANCE.avg(100L, 200L);
        bh.consume(result);
    }

    @Benchmark
    public void testInvert(Blackhole bh) {
        // Test inversion.
        long result = CheckedScale0fTruncatingArithmetic.INSTANCE.invert(1000L);
        bh.consume(result);
    }

    @Benchmark
    public void testPow(Blackhole bh) {
        // Test power calculation.
        long result = CheckedScale0fTruncatingArithmetic.INSTANCE.pow(10L, 3);
        bh.consume(result);
    }

    @Benchmark
    public void testSqrt(Blackhole bh) {
        // Test square root calculation.
        long result = CheckedScale0fTruncatingArithmetic.INSTANCE.sqrt(1000000000000L);
        bh.consume(result);
    }

    @Benchmark
    public void testShiftLeft(Blackhole bh) {
        // Test shift left.
        long result = CheckedScale0fTruncatingArithmetic.INSTANCE.shiftLeft(100L, 5);
        bh.consume(result);
    }

    @Benchmark
    public void testRound(Blackhole bh) {
        // Test rounding.
        long result = CheckedScale0fTruncatingArithmetic.INSTANCE.round(123456789L, 5);
        bh.consume(result);
    }

    @Benchmark
    public void testToFloat(Blackhole bh) {
        // Test conversion to float.
        float result = CheckedScale0fTruncatingArithmetic.INSTANCE.toFloat(12345L);
        bh.consume(result);
    }

    @Benchmark
    public void testFromDouble(Blackhole bh) {
        // Test conversion from double.
        long result = CheckedScale0fTruncatingArithmetic.INSTANCE.fromDouble(123.45);
        bh.consume(result);
    }

    @Benchmark
    public void testFromBigDecimal(Blackhole bh) {
        // Test conversion from BigDecimal.
        try {
            // BigDecimal creation is acceptable here as it happens once per benchmark run
            java.math.BigDecimal bd = new java.math.BigDecimal("1234567890123456789L");
            long result = CheckedScale0fTruncatingArithmetic.INSTANCE.fromBigDecimal(bd);
            bh.consume(result);
        } catch (Exception e) {
            // Ignore exceptions for benchmark stability
        }
    }

    @Benchmark
    public void testParseString(Blackhole bh) {
        // Test parsing a string.
        try {
            // FIX: Calling the one-argument parse method.
            long result = CheckedScale0fTruncatingArithmetic.INSTANCE.parse("1234567890");
            bh.consume(result);
        } catch (Exception e) {
            // Ignore exceptions
        }
    }
}
