package bench.generated.c105;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

import org.decimal4j.scale.Scale9f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Scale9fBenchmark {

    // Since Scale9f is an enum singleton, we rely on static calls.

    // Benchmark for a simple read-only method
    @Benchmark
    public void testGetScale(Blackhole bh) {
        // Calling a method to ensure the enum is initialized/used
        Scale9f.INSTANCE.getScale();
        bh.consume(true);
    }

    // Benchmark for a read-only method that returns a long
    @Benchmark
    public long testGetScaleFactor(Blackhole bh) {
        return Scale9f.INSTANCE.getScaleFactor();
    }

    // Benchmark for a method that performs simple arithmetic (division)
    @Benchmark
    public long testDivideByScaleFactor(Blackhole bh) {
        // Test division by scale factor
        return Scale9f.INSTANCE.divideByScaleFactor(1000000000L);
    }

    // Benchmark for a method that performs modulo operation
    @Benchmark
    public long testModuloByScaleFactor(Blackhole bh) {
        // Test modulo operation
        return Scale9f.INSTANCE.moduloByScaleFactor(1000000000L + 1);
    }

    // Benchmark for a method that performs multiplication by scale factor (exact)
    @Benchmark
    public long testMultiplyByScaleFactorExact(Blackhole bh) {
        // Test multiplication by scale factor (exact)
        try {
            return Scale9f.INSTANCE.multiplyByScaleFactorExact(1000L);
        } catch (ArithmeticException e) {
            // Ignore expected overflow for this test if inputs are too large
            return 0L;
        }
    }

    // Benchmark for a method that performs multiplication by scale factor (simple)
    @Benchmark
    public long testMultiplyByScaleFactor(Blackhole bh) {
        // Test multiplication by scale factor (simple)
        return Scale9f.INSTANCE.multiplyByScaleFactor(100L);
    }

    // Benchmark for a method that performs multiplication by scale factor (int factor)
    @Benchmark
    public long testMulloByScaleFactor(Blackhole bh) {
        // Test multiplication by scale factor (int factor)
        return Scale9f.INSTANCE.mulloByScaleFactor(1000);
    }

    // Benchmark for a method that performs multiplication by scale factor (int factor)
    @Benchmark
    public long testMulhiByScaleFactor(Blackhole bh) {
        // Test multiplication by scale factor (int factor) that should return 0
        return Scale9f.INSTANCE.mulhiByScaleFactor(1000);
    }

    // Benchmark for toString conversion (read-only)
    @Benchmark
    public String testToString(Blackhole bh) {
        // Test toString on a dummy value (since it takes a long)
        return Scale9f.INSTANCE.toString(123456789L);
    }

    // Test arithmetic path selection
    @Benchmark
    public org.decimal4j.api.DecimalArithmetic testGetDefaultArithmetic(Blackhole bh) {
        return Scale9f.INSTANCE.getDefaultArithmetic();
    }
}
