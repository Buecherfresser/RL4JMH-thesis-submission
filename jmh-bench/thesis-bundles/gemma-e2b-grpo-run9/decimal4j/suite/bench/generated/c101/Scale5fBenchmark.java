package bench.generated.c101;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.scale.Scale5f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Scale5fBenchmark {

    // We use the singleton instance for benchmarking
    private final Scale5f scale5f = Scale5f.INSTANCE;

    @Setup
    public void setup() {
        // No complex setup needed as Scale5f is a singleton enum.
    }

    // --- Benchmarks for simple getters and constants ---

    @Benchmark
    public void getScale(Blackhole bh) {
        bh.consume(scale5f.getScale());
    }

    @Benchmark
    public void getScaleFactor(Blackhole bh) {
        bh.consume(scale5f.getScaleFactor());
    }

    @Benchmark
    public void getScaleFactorAsBigInteger(Blackhole bh) {
        bh.consume(scale5f.getScaleFactorAsBigInteger());
    }

    @Benchmark
    public void isValidIntegerValue(Blackhole bh) {
        // Test a valid value
        bh.consume(scale5f.isValidIntegerValue(1000000L));
    }

    @Benchmark
    public void isValidIntegerValue_Invalid(Blackhole bh) {
        // Test an invalid value (testing bounds)
        bh.consume(scale5f.isValidIntegerValue(Long.MAX_VALUE + 1));
    }

    // --- Benchmarks for multiplication/division by scale factor ---

    @Benchmark
    public void multiplyByScaleFactor(Blackhole bh) {
        // Test multiplication by a small factor
        bh.consume(scale5f.INSTANCE.multiplyByScaleFactor(10));
    }

    @Benchmark
    public void multiplyByScaleFactor_Overflow(Blackhole bh) {
        // Test multiplication that should throw ArithmeticException (if factor is too large)
        try {
            scale5f.INSTANCE.multiplyByScaleFactor(Long.MAX_VALUE / 100000L + 1);
        } catch (ArithmeticException e) {
            // Expected exception, consume the result of the attempt
        }
    }

    @Benchmark
    public void divideByScaleFactor(Blackhole bh) {
        // Test division
        bh.consume(scale5f.INSTANCE.divideByScaleFactor(1000000L));
    }

    @Benchmark
    public void divideUnsignedByScaleFactor(Blackhole bh) {
        // Test unsigned division
        bh.consume(scale5f.INSTANCE.divideUnsignedByScaleFactor(Long.MAX_VALUE));
    }

    @Benchmark
    public void moduloByScaleFactor(Blackhole bh) {
        // Test modulo operation
        bh.consume(scale5f.INSTANCE.moduloByScaleFactor(100001L));
    }

    // --- Benchmarks for arithmetic back-ends ---

    @Benchmark
    public void getDefaultArithmetic(Blackhole bh) {
        // Fix: Assuming DecimalArithmetic.add requires two longs based on compilation error.
        bh.consume(scale5f.INSTANCE.getDefaultArithmetic().add(1L, 0L));
    }

    @Benchmark
    public void getArithmetic_HalfUp(Blackhole bh) {
        // Test retrieval of a specific arithmetic implementation
        bh.consume(scale5f.INSTANCE.getArithmetic(java.math.RoundingMode.HALF_UP));
    }

    @Benchmark
    public void getArithmetic_RoundingDown(Blackhole bh) {
        // Test retrieval of another specific arithmetic implementation
        bh.consume(scale5f.INSTANCE.getRoundingDownArithmetic());
    }

    @Benchmark
    public void arithmetic_Add(Blackhole bh) {
        // Fix: Assuming DecimalArithmetic.multiply requires two longs based on compilation error.
        bh.consume(scale5f.INSTANCE.getDefaultArithmetic().multiply(1L, 2L));
    }

    @Benchmark
    public void arithmetic_Multiply(Blackhole bh) {
        // Test an arithmetic operation
        bh.consume(scale5f.INSTANCE.getDefaultArithmetic().multiply(1L, 2L));
    }

    @Benchmark
    public void getCheckedArithmetic(Blackhole bh) {
        // Test retrieval of checked arithmetic
        bh.consume(scale5f.INSTANCE.getDefaultCheckedArithmetic());
    }

    @Benchmark
    public void arithmetic_Add_Checked(Blackhole bh) {
        // Fix: Assuming DecimalArithmetic.add requires two longs based on compilation error.
        bh.consume(scale5f.INSTANCE.getDefaultCheckedArithmetic().add(1L, 0L));
    }
}
