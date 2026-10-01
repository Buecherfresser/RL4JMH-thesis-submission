package bench.generated.c096;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

import org.decimal4j.scale.Scale18f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Scale18fBenchmark {

    // Since Scale18f is a singleton enum, no instance state is required.

    @Benchmark
    public void testGetScale(Blackhole bh) {
        // Accessing the singleton instance
        bh.consume(Scale18f.INSTANCE.getScale());
    }

    @Benchmark
    public void testGetScaleFactor(Blackhole bh) {
        bh.consume(Scale18f.INSTANCE.getScaleFactor());
    }

    @Benchmark
    public void testMultiplyByScaleFactor(Blackhole bh) {
        // Test multiplication with a small factor
        try {
            long result = Scale18f.INSTANCE.multiplyByScaleFactor(1000L);
            bh.consume(result);
        } catch (ArithmeticException e) {
            // Expected if overflow occurs, but we don't need to handle it for benchmarking
        }
    }

    @Benchmark
    public void testMultiplyByScaleFactorLarge(Blackhole bh) {
        // Test multiplication with a factor that might cause overflow (testing the check)
        try {
            Scale18f.INSTANCE.multiplyByScaleFactor(Long.MAX_VALUE / 2);
        } catch (ArithmeticException e) {
            // Expected behavior for overflow
        }
    }

    @Benchmark
    public void testMulloByScaleFactor(Blackhole bh) {
        // Test mulloByScaleFactor (uses low bits)
        try {
            long result = Scale18f.INSTANCE.mulloByScaleFactor(1000000000);
            bh.consume(result);
        } catch (Exception e) {
            // Ignore exceptions for benchmarking purposes if they are expected
        }
    }

    @Benchmark
    public void testMulhiByScaleFactor(Blackhole bh) {
        // Test mulhiByScaleFactor (uses high bits)
        try {
            long result = Scale18f.INSTANCE.mulhiByScaleFactor(1000000000);
            bh.consume(result);
        } catch (Exception e) {
            // Ignore exceptions
        }
    }

    @Benchmark
    public void testDivideByScaleFactor(Blackhole bh) {
        // Test division
        try {
            long result = Scale18f.INSTANCE.divideByScaleFactor(1000000000000000000L);
            bh.consume(result);
        } catch (Exception e) {
            // Ignore exceptions
        }
    }

    @Benchmark
    public void testDivideUnsignedByScaleFactor(Blackhole bh) {
        // Test unsigned division
        try {
            long result = Scale18f.INSTANCE.divideUnsignedByScaleFactor(1000000000000000000L);
            bh.consume(result);
        } catch (Exception e) {
            // Ignore exceptions
        }
    }

    @Benchmark
    public void testModuloByScaleFactor(Blackhole bh) {
        // Test modulo operation
        try {
            long result = Scale18f.INSTANCE.moduloByScaleFactor(1000000000000000001L);
            bh.consume(result);
        } catch (Exception e) {
            // Ignore exceptions
        }
    }

    @Benchmark
    public void testToString(Blackhole bh) {
        // Test toString conversion (requires a long input)
        try {
            // Use a large number to ensure the conversion logic runs
            String result = Scale18f.INSTANCE.toString(Long.MAX_VALUE);
            bh.consume(result);
        } catch (Exception e) {
            // Ignore exceptions
        }
    }
}
