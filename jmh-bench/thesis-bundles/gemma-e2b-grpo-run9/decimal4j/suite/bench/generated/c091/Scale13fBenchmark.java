package bench.generated.c091;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

import org.decimal4j.scale.Scale13f;
import org.decimal4j.truncate.TruncationPolicy;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Scale13fBenchmark {

    // Since Scale13f is an enum singleton, no instance state is required.

    @Benchmark
    public void testGetScale() {
        // Call a simple getter method
        Scale13f.INSTANCE.getScale();
    }

    @Benchmark
    public void testMultiplyByScaleFactor() {
        // Call a method that performs multiplication
        try {
            Scale13f.INSTANCE.multiplyByScaleFactor(1000L);
        } catch (ArithmeticException e) {
            // Expected if overflow occurs, but we only measure the call path
        }
    }

    @Benchmark
    public void testMulloByScaleFactor() {
        // Call another multiplication method
        try {
            Scale13f.INSTANCE.mulloByScaleFactor(10);
        } catch (ArithmeticException e) {
            // Expected if overflow occurs
        }
    }

    @Benchmark
    public void testDivideByScaleFactor() {
        // Call a division method
        try {
            Scale13f.INSTANCE.divideByScaleFactor(10000000000000L);
        } catch (ArithmeticException e) {
            // Expected if division by zero or other issues occur
        }
    }

    @Benchmark
    public void testDivideUnsignedByScaleFactor() {
        // Call an unsigned division method
        try {
            Scale13f.INSTANCE.divideUnsignedByScaleFactor(10000000000000L);
        } catch (ArithmeticException e) {
            // Expected if division by zero or other issues occur
        }
    }

    @Benchmark
    public void testModuloByScaleFactor() {
        // Call a modulo method
        try {
            Scale13f.INSTANCE.moduloByScaleFactor(10000000000000L);
        } catch (ArithmeticException e) {
            // Expected if division by zero or other issues occur
        }
    }

    @Benchmark
    public void testGetArithmetic() {
        // Benchmark retrieving a specific arithmetic implementation
        try {
            Scale13f.INSTANCE.getArithmetic(java.math.RoundingMode.HALF_UP);
        } catch (Exception e) {
            // Ignore exceptions for benchmarking purposes if they are expected in certain modes
        }
    }

    @Benchmark
    public void testGetCheckedArithmetic() {
        // Benchmark retrieving a checked arithmetic implementation
        try {
            Scale13f.INSTANCE.getCheckedArithmetic(java.math.RoundingMode.HALF_UP);
        } catch (Exception e) {
            // Ignore exceptions
        }
    }

    @Benchmark
    public void testToString(Blackhole bh) {
        // Benchmark the toString method, which takes a long input
        // We pass a constant long value, which is acceptable for benchmarking static methods.
        bh.consume(Scale13f.INSTANCE.toString(123456789L));
    }
}
