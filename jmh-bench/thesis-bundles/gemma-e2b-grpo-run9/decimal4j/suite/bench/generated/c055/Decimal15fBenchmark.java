package bench.generated.c055;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.math.BigDecimal;
import java.math.BigInteger;

import org.decimal4j.immutable.Decimal15f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class Decimal15fBenchmark {

    // Since all methods benchmarked are static, no instance state is required.

    /**
     * Benchmark for converting a long value to Decimal15f.
     * Uses the static valueOf(long value) method.
     */
    @Benchmark
    public void benchmarkValueOfLong(Blackhole bh) {
        // Test conversion of a simple long value
        Decimal15f result = Decimal15f.valueOf(123456789L);
        bh.consume(result);
    }

    /**
     * Benchmark for converting a double value to Decimal15f.
     * Uses the static valueOf(double value) method.
     */
    @Benchmark
    public void benchmarkValueOfDouble(Blackhole bh) {
        // Test conversion of a simple double value
        Decimal15f result = Decimal15f.valueOf(3.1415926535);
        bh.consume(result);
    }

    /**
     * Benchmark for converting a BigInteger value to Decimal15f.
     * Uses the static valueOf(BigInteger value) method.
     */
    @Benchmark
    public void benchmarkValueOfBigInteger(Blackhole bh) {
        // Test conversion of a simple BigInteger value
        Decimal15f result = Decimal15f.valueOf(new BigInteger("1234567890123456789"));
        bh.consume(result);
    }

    /**
     * Benchmark for converting a BigDecimal value to Decimal15f.
     * Uses the static valueOf(BigDecimal value) method.
     */
    @Benchmark
    public void benchmarkValueOfBigDecimal(Blackhole bh) {
        // Test conversion of a simple BigDecimal value
        BigDecimal bd = new BigDecimal("123.456789012345678");
        Decimal15f result = Decimal15f.valueOf(bd);
        bh.consume(result);
    }

    /**
     * Benchmark for converting an unscaled long value.
     * Uses the static valueOfUnscaled(long unscaledValue) method.
     */
    @Benchmark
    public void benchmarkValueOfUnscaledLong(Blackhole bh) {
        // Test conversion of a value that isn't a predefined constant
        Decimal15f result = Decimal15f.valueOfUnscaled(987654321L);
        bh.consume(result);
    }
    
    /**
     * Benchmark for converting an unscaled long value with a specific scale.
     * Uses the static valueOfUnscaled(long unscaledValue, int scale) method.
     */
    @Benchmark
    public void benchmarkValueOfUnscaledWithScale(Blackhole bh) {
        // Test conversion with a specific scale
        Decimal15f result = Decimal15f.valueOfUnscaled(100L, 5);
        bh.consume(result);
    }
}
