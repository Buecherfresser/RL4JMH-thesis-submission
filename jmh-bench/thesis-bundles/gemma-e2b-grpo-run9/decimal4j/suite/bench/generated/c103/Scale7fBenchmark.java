package bench.generated.c103;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

import org.decimal4j.scale.Scale7f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Scale7fBenchmark {

    // Since Scale7f is an enum singleton, no complex state setup is required.

    @Setup
    public void setup() {
        // No setup required for the singleton instance.
    }

    // --- Benchmarks for simple getters and constants ---

    @Benchmark
    public void getScale(Blackhole bh) {
        bh.consume(Scale7f.INSTANCE.getScale());
    }

    @Benchmark
    public void getScaleFactor(Blackhole bh) {
        bh.consume(Scale7f.INSTANCE.getScaleFactor());
    }

    @Benchmark
    public void getScaleFactorAsBigInteger(Blackhole bh) {
        bh.consume(Scale7f.INSTANCE.getScaleFactorAsBigInteger());
    }

    @Benchmark
    public void isValidIntegerValue(Blackhole bh) {
        // Test a valid value
        bh.consume(Scale7f.INSTANCE.isValidIntegerValue(10000000L));
        // Test an invalid value (overflow check)
        bh.consume(Scale7f.INSTANCE.isValidIntegerValue(Long.MAX_VALUE / Scale7f.INSTANCE.getScaleFactor() + 1));
    }

    // --- Benchmarks for multiplication/division by scale factor ---

    @Benchmark
    public void multiplyByScaleFactor(Blackhole bh) {
        // Test a small factor
        bh.consume(Scale7f.INSTANCE.multiplyByScaleFactor(100L));
    }

    @Benchmark
    public void multiplyByScaleFactorExact(Blackhole bh) {
        // Test a factor that should succeed
        try {
            bh.consume(Scale7f.INSTANCE.multiplyByScaleFactorExact(100L));
        } catch (ArithmeticException e) {
            // Ignore expected exception for timing purposes
        }
    }

    @Benchmark
    public void mulloByScaleFactor(Blackhole bh) {
        // Test a factor that fits within the long mask (0xffffffffL)
        bh.consume(Scale7f.INSTANCE.mulloByScaleFactor(10000000));
    }

    @Benchmark
    public void mulhiByScaleFactor(Blackhole bh) {
        // Test a factor that should result in 0
        bh.consume(Scale7f.INSTANCE.mulhiByScaleFactor(10000000));
    }

    @Benchmark
    public void divideByScaleFactor(Blackhole bh) {
        // Test division
        bh.consume(Scale7f.INSTANCE.divideByScaleFactor(1000000000L));
    }

    @Benchmark
    public void divideUnsignedByScaleFactor(Blackhole bh) {
        // Test unsigned division
        bh.consume(Scale7f.INSTANCE.divideUnsignedByScaleFactor(Long.MAX_VALUE));
    }

    @Benchmark
    public void moduloByScaleFactor(Blackhole bh) {
        // Test modulo operation
        bh.consume(Scale7f.INSTANCE.moduloByScaleFactor(10000001L));
    }

    // --- Benchmarks for arithmetic back-ends ---

    @Benchmark
    public void getArithmeticDefault(Blackhole bh) {
        bh.consume(Scale7f.INSTANCE.getDefaultArithmetic());
    }

    @Benchmark
    public void getArithmeticRoundingDown(Blackhole bh) {
        bh.consume(Scale7f.INSTANCE.getRoundingDownArithmetic());
    }

    @Benchmark
    public void getArithmeticRoundingHalfEven(Blackhole bh) {
        bh.consume(Scale7f.INSTANCE.getRoundingHalfEvenArithmetic());
    }

    @Benchmark
    public void getArithmeticChecked(Blackhole bh) {
        bh.consume(Scale7f.INSTANCE.getDefaultCheckedArithmetic());
    }

    // --- Benchmarks for toString (which relies on arithmetic) ---

    @Benchmark
    public void toString(Blackhole bh) {
        // Test conversion of a long value
        bh.consume(Scale7f.INSTANCE.toString(123456789L));
    }
}
