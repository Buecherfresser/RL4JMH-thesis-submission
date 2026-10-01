package bench.generated.c087;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

import org.decimal4j.scale.Scale0f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Scale0fBenchmark {

    // Since Scale0f is an enum singleton, we rely on static calls.

    @Benchmark
    public void testGetScale(Blackhole bh) {
        // Test a simple getter method
        bh.consume(Scale0f.INSTANCE.getScale());
    }

    @Benchmark
    public void testGetScaleFactor(Blackhole bh) {
        // Test a simple getter method
        bh.consume(Scale0f.INSTANCE.getScaleFactor());
    }

    @Benchmark
    public void testMultiplyByScaleFactor(Blackhole bh) {
        // Test a multiplication method. Input 100L is a long.
        Scale0f.INSTANCE.multiplyByScaleFactor(100L);
    }

    @Benchmark
    public void testMulloByScaleFactor(Blackhole bh) {
        // Test a method that uses a mask. Input 10 is an int, which is promoted to long.
        Scale0f.INSTANCE.mulloByScaleFactor(10);
    }

    @Benchmark
    public void testToString(Blackhole bh) {
        // Test the toString method. Input 12345L is a long.
        bh.consume(Scale0f.INSTANCE.toString(12345L));
    }

    @Benchmark
    public void testArithmeticDefault(Blackhole bh) {
        // Test accessing a default arithmetic implementation
        bh.consume(Scale0f.INSTANCE.getDefaultArithmetic());
    }

    @Benchmark
    public void testArithmeticChecked(Blackhole bh) {
        // Test accessing a checked arithmetic implementation
        bh.consume(Scale0f.INSTANCE.getDefaultCheckedArithmetic());
    }

    @Benchmark
    public void testArithmeticRoundingDown(Blackhole bh) {
        // Test accessing a specific rounding arithmetic implementation
        bh.consume(Scale0f.INSTANCE.getRoundingDownArithmetic());
    }

    @Benchmark
    public void testArithmeticHalfEven(Blackhole bh) {
        // Test accessing a specific rounding arithmetic implementation
        bh.consume(Scale0f.INSTANCE.getRoundingHalfEvenArithmetic());
    }

    @Benchmark
    public void testArithmeticUnchecked(Blackhole bh) {
        // Test accessing an unchecked arithmetic implementation
        bh.consume(Scale0f.INSTANCE.getArithmetic(java.math.RoundingMode.HALF_UP));
    }
}
