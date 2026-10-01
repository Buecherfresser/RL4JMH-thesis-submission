package bench.generated.c094;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.scale.Scale16f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Scale16fBenchmark {

    // Since Scale16f is an enum singleton, no instance state is required.

    @Benchmark
    public void testGetScale(Blackhole bh) {
        // Test a simple getter method
        bh.consume(Scale16f.INSTANCE.getScale());
    }

    @Benchmark
    public void testGetScaleFactor(Blackhole bh) {
        // Test another simple getter method
        bh.consume(Scale16f.INSTANCE.getScaleFactor());
    }

    @Benchmark
    public void testMultiplyByScaleFactor(Blackhole bh) {
        // Test a method that performs multiplication
        try {
            Scale16f.INSTANCE.multiplyByScaleFactor(1000L);
        } catch (ArithmeticException e) {
            // Expected if overflow occurs, which is fine for a benchmark test
        }
        bh.consume(null);
    }

    @Benchmark
    public void testMulloByScaleFactor(Blackhole bh) {
        // Test a method using low bits
        try {
            Scale16f.INSTANCE.mulloByScaleFactor(100);
        } catch (Exception e) {
            // Ignore exceptions for this test
        }
        bh.consume(null);
    }

    @Benchmark
    public void testMulhiByScaleFactor(Blackhole bh) {
        // Test a method using high bits
        try {
            Scale16f.INSTANCE.mulhiByScaleFactor(100);
        } catch (Exception e) {
            // Ignore exceptions for this test
        }
        bh.consume(null);
    }

    @Benchmark
    public void testDivideByScaleFactor(Blackhole bh) {
        // Test division
        try {
            Scale16f.INSTANCE.divideByScaleFactor(10000000000000000L);
        } catch (Exception e) {
            // Ignore exceptions for this test
        }
        bh.consume(null);
    }

    @Benchmark
    public void testDivideUnsignedByScaleFactor(Blackhole bh) {
        // Test unsigned division
        try {
            Scale16f.INSTANCE.divideUnsignedByScaleFactor(10000000000000000L);
        } catch (Exception e) {
            // Ignore exceptions for this test
        }
        bh.consume(null);
    }

    @Benchmark
    public void testModuloByScaleFactor(Blackhole bh) {
        // Test modulo operation
        try {
            Scale16f.INSTANCE.moduloByScaleFactor(10000000000000000L + 1);
        } catch (Exception e) {
            // Ignore exceptions for this test
        }
        bh.consume(null);
    }

    @Benchmark
    public void testToString(Blackhole bh) {
        // Test the toString method which relies on internal arithmetic
        try {
            Scale16f.INSTANCE.toString(123456789012345678L);
        } catch (Exception e) {
            // Ignore exceptions for this test
        }
        bh.consume(null);
    }
}
