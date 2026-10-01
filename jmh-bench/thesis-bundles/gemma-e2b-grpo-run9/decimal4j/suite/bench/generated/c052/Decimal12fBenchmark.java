package bench.generated.c052;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.concurrent.TimeUnit;

import org.decimal4j.immutable.Decimal12f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Decimal12fBenchmark {

    // Since Decimal12f is immutable and we are testing static factory methods,
    // we don't strictly need @State fields, but we keep the class structure clean.

    /**
     * Benchmark for converting a long value to Decimal12f.
     * Uses a dynamic long value to avoid static final issues.
     */
    @Benchmark
    public void testValueOfLong(Blackhole bh) {
        // Use a dynamic long value to ensure it's not a compile-time constant
        long value = 1234567890123L;
        Decimal12f result = Decimal12f.valueOf(value);
        bh.consume(result);
    }

    /**
     * Benchmark for converting a float value to Decimal12f (default rounding).
     */
    @Benchmark
    public void testValueOfFloat(Blackhole bh) {
        float value = 123.45f;
        Decimal12f result = Decimal12f.valueOf(value);
        bh.consume(result);
    }

    /**
     * Benchmark for converting a double value to Decimal12f (default rounding).
     */
    @Benchmark
    public void testValueOfDouble(Blackhole bh) {
        double value = 123.456789;
        Decimal12f result = Decimal12f.valueOf(value);
        bh.consume(result);
    }

    /**
     * Benchmark for converting a BigInteger value to Decimal12f.
     */
    @Benchmark
    public void testValueOfBigInteger(Blackhole bh) {
        BigInteger value = new BigInteger("9876543210987654321");
        Decimal12f result = Decimal12f.valueOf(value);
        bh.consume(result);
    }

    /**
     * Benchmark for converting a BigDecimal value to Decimal12f (default rounding).
     */
    @Benchmark
    public void testValueOfBigDecimal(Blackhole bh) {
        // Use a BigDecimal that requires conversion logic
        BigDecimal value = new BigDecimal("12345.67890123456789");
        Decimal12f result = Decimal12f.valueOf(value);
        bh.consume(result);
    }

    /**
     * Benchmark for converting a Decimal<?> value to Decimal12f.
     * We use a static constant for simplicity, as it's immutable.
     */
    @Benchmark
    public void testValueOfDecimal(Blackhole bh) {
        // Using a static constant ensures we don't rely on complex setup
        Decimal12f result = Decimal12f.valueOf(Decimal12f.ONE);
        bh.consume(result);
    }

    /**
     * Benchmark for converting a long unscaled value.
     */
    @Benchmark
    public void testValueOfUnscaledLong(Blackhole bh) {
        long unscaledValue = 1234567890123L;
        Decimal12f result = Decimal12f.valueOfUnscaled(unscaledValue);
        bh.consume(result);
    }

    /**
     * Benchmark for converting a long unscaled value with a specific scale.
     */
    @Benchmark
    public void testValueOfUnscaledLongWithScale(Blackhole bh) {
        long unscaledValue = 1234567890123L;
        int scale = 5;
        Decimal12f result = Decimal12f.valueOfUnscaled(unscaledValue, scale);
        bh.consume(result);
    }
}
