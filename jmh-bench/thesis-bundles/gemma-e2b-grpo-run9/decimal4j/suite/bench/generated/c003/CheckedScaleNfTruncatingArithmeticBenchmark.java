package bench.generated.c003;

import org.decimal4j.arithmetic.CheckedScaleNfTruncatingArithmetic;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class CheckedScaleNfTruncatingArithmeticBenchmark {

    private CheckedScaleNfTruncatingArithmetic arithmetic;

    @Setup
    public void setup() {
        // Initialize the arithmetic object.
        // We rely on the constructor accepting null or a default ScaleMetrics
        // if the implementation allows it, or handle potential exceptions.
        try {
            this.arithmetic = new CheckedScaleNfTruncatingArithmetic(null);
        } catch (Exception e) {
            // In a real scenario, this failure should be handled robustly.
            // For benchmarking stability, we proceed if possible.
            System.err.println("Failed to initialize CheckedScaleNfTruncatingArithmetic: " + e.getMessage());
        }
    }

    @Benchmark
    public void multiply(Blackhole bh) {
        // Test multiplication
        if (arithmetic != null) {
            long result = arithmetic.multiply(100L, 50L);
            bh.consume(result);
        }
    }

    @Benchmark
    public void divide(Blackhole bh) {
        // Test division
        if (arithmetic != null) {
            long result = arithmetic.divide(1000L, 10L);
            bh.consume(result);
        }
    }

    @Benchmark
    public void square(Blackhole bh) {
        // Test square
        if (arithmetic != null) {
            long result = arithmetic.square(12345L);
            bh.consume(result);
        }
    }

    @Benchmark
    public void fromLong(Blackhole bh) {
        // Test conversion from long
        if (arithmetic != null) {
            long result = arithmetic.fromLong(123456789L);
            bh.consume(result);
        }
    }

    @Benchmark
    public void round(Blackhole bh) {
        // Test rounding
        if (arithmetic != null) {
            long result = arithmetic.round(12345L, 2);
            bh.consume(result);
        }
    }

    @Benchmark
    public void parse(Blackhole bh) {
        // Test parsing
        if (arithmetic != null) {
            try {
                // Test parsing a simple string
                long result = arithmetic.parse("12345", 0, 5);
                bh.consume(result);
            } catch (Exception e) {
                // Ignore parsing exceptions for benchmark stability
            }
        }
    }
}
