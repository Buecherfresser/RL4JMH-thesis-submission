package bench.generated.c065;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import org.decimal4j.immutable.Decimal7f;
import org.decimal4j.api.Decimal;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(java.util.concurrent.TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Decimal7fBenchmark {

    // Since Decimal7f is immutable and static methods are used, no instance state is strictly required.

    /**
     * Benchmark for converting a long value to Decimal7f.
     * This tests the valueOf(long) static method.
     */
    @Benchmark
    public void benchmarkValueOfLong(Blackhole bh) {
        // Use a large number to stress the conversion logic
        Decimal7f result = Decimal7f.valueOf(Long.MAX_VALUE);
        bh.consume(result);
    }

    /**
     * Benchmark for converting a float value to Decimal7f (default rounding).
     * This tests the valueOf(float) static method.
     */
    @Benchmark
    public void benchmarkValueOfFloat(Blackhole bh) {
        // Use a value that might require rounding
        Decimal7f result = Decimal7f.valueOf(123.456f);
        bh.consume(result);
    }

    /**
     * Benchmark for converting a double value to Decimal7f (default rounding).
     * This tests the valueOf(double) static method.
     */
    @Benchmark
    public void benchmarkValueOfDouble(Blackhole bh) {
        // Use a value that might require rounding
        Decimal7f result = Decimal7f.valueOf(123.456);
        bh.consume(result);
    }

    /**
     * Benchmark for converting a BigInteger value to Decimal7f.
     * This tests the valueOf(BigInteger) static method.
     */
    @Benchmark
    public void benchmarkValueOfBigInteger(Blackhole bh) {
        // Use a large BigInteger
        Decimal7f result = Decimal7f.valueOf(new BigInteger("9223372036854775807"));
        bh.consume(result);
    }

    /**
     * Benchmark for converting a BigDecimal value to Decimal7f (default rounding).
     * This tests the valueOf(BigDecimal) static method.
     */
    @Benchmark
    public void benchmarkValueOfBigDecimal(Blackhole bh) {
        // Use a complex BigDecimal
        Decimal7f result = Decimal7f.valueOf(new BigDecimal("123.4567890123456789"));
        bh.consume(result);
    }

    /**
     * Benchmark for converting a String value to Decimal7f (default rounding).
     * This tests the valueOf(String) static method.
     */
    @Benchmark
    public void benchmarkValueOfString(Blackhole bh) {
        // Use a string representation
        Decimal7f result = Decimal7f.valueOf("123.456");
        bh.consume(result);
    }

    /**
     * Benchmark for converting an unscaled long value.
     * This tests the valueOfUnscaled(long) static method.
     */
    @Benchmark
    public void benchmarkValueOfUnscaledLong(Blackhole bh) {
        // Test a value that is not a special constant
        Decimal7f result = Decimal7f.valueOfUnscaled(123456789L);
        bh.consume(result);
    }

    /**
     * Benchmark for converting an unscaled long value with a specific scale.
     * This tests the valueOfUnscaled(long, int, RoundingMode) static method.
     */
    @Benchmark
    public void benchmarkValueOfUnscaledWithScale(Blackhole bh) {
        // Test conversion with a specific scale and rounding mode
        Decimal7f result = Decimal7f.valueOfUnscaled(123456789L, 2, RoundingMode.HALF_UP);
        bh.consume(result);
    }
}
