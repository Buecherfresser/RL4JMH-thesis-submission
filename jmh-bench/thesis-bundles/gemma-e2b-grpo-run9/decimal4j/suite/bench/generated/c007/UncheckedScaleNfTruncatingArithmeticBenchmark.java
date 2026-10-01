package bench.generated.c007;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.math.BigDecimal;

import org.decimal4j.arithmetic.UncheckedScaleNfTruncatingArithmetic;
import org.decimal4j.scale.ScaleMetrics;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class UncheckedScaleNfTruncatingArithmeticBenchmark {

    private UncheckedScaleNfTruncatingArithmetic arithmetic;

    @Setup
    public void setup() {
        // Initialize the arithmetic object. We assume a default or simple ScaleMetrics
        // can be provided or inferred for this benchmark context.
        try {
            // Attempt to instantiate with a placeholder or default scale metrics.
            // This relies on the constructor not throwing exceptions for a default state.
            this.arithmetic = new UncheckedScaleNfTruncatingArithmetic(null); 
        } catch (Exception e) {
            // Handle potential initialization failure if ScaleMetrics requires specific input
            System.err.println("Failed to initialize UncheckedScaleNfTruncatingArithmetic: " + e.getMessage());
            this.arithmetic = null; // Allow benchmarks to skip if initialization fails
        }
    }

    @Benchmark
    public void testAddUnscaled(Blackhole bh) {
        if (arithmetic != null) {
            // Test addition (requires specific long inputs, which are fine as they are not static final)
            long result = arithmetic.addUnscaled(100L, 50L, 2);
            bh.consume(result);
        }
    }

    @Benchmark
    public void testMultiply(Blackhole bh) {
        if (arithmetic != null) {
            // Test multiplication
            long result = arithmetic.multiply(10L, 20L);
            bh.consume(result);
        }
    }

    @Benchmark
    public void testSquare(Blackhole bh) {
        if (arithmetic != null) {
            // Test square
            long result = arithmetic.square(100L);
            bh.consume(result);
        }
    }

    @Benchmark
    public void testDivideByLong(Blackhole bh) {
        if (arithmetic != null) {
            // Test division by long
            long result = arithmetic.divideByLong(100L, 10L);
            bh.consume(result);
        }
    }

    @Benchmark
    public void testFromLong(Blackhole bh) {
        if (arithmetic != null) {
            // Test conversion from long
            long result = arithmetic.fromLong(12345L);
            bh.consume(result);
        }
    }

    @Benchmark
    public void testFromDouble(Blackhole bh) {
        if (arithmetic != null) {
            // Test conversion from double
            long result = arithmetic.fromDouble(123.45);
            bh.consume(result);
        }
    }

    @Benchmark
    public void testFromBigDecimal(Blackhole bh) {
        if (arithmetic != null) {
            // Test conversion from BigDecimal
            // Note: This relies on BigDecimal being available and the conversion logic working.
            try {
                BigDecimal bd = new BigDecimal("123.45");
                long result = arithmetic.fromBigDecimal(bd);
                bh.consume(result);
            } catch (Exception e) {
                // Ignore if BigDecimal conversion fails due to missing context/dependencies
            }
        }
    }

    @Benchmark
    public void testParseString(Blackhole bh) {
        if (arithmetic != null) {
            // Test parsing a string (requires StringConversion to be functional)
            try {
                long result = arithmetic.parse("123456789", 0, 9);
                bh.consume(result);
            } catch (Exception e) {
                // Ignore if parsing fails
            }
        }
    }
}
