package bench.generated.c102;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.math.BigDecimal;
import java.math.BigInteger;

import org.decimal4j.scale.Scale6f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Scale6fBenchmark {

    // We use the singleton instance for benchmarking
    // Since Scale6f is an enum, we can access it statically or via INSTANCE
    private final Scale6f scale6f = Scale6f.INSTANCE;

    @Setup
    public void setup() {
        // No complex setup required for the singleton enum
    }

    // --- Benchmarks for getters and constants (read-only operations) ---

    @Benchmark
    public void getScale(Blackhole bh) {
        bh.consume(scale6f.getScale());
    }

    @Benchmark
    public void getScaleFactor(Blackhole bh) {
        bh.consume(scale6f.getScaleFactor());
    }

    @Benchmark
    public void getScaleFactorAsBigInteger(Blackhole bh) {
        bh.consume(scale6f.getScaleFactorAsBigInteger());
    }

    @Benchmark
    public void isValidIntegerValue(Blackhole bh) {
        // Test a valid value
        bh.consume(scale6f.isValidIntegerValue(1000000L));
        // Test an invalid value (should throw exception internally, but we test the return path)
        try {
            bh.consume(scale6f.isValidIntegerValue(Long.MAX_VALUE / scale6f.getScaleFactor() + 1));
        } catch (Exception e) {
            // Ignore expected exceptions during timing
        }
    }

    // --- Benchmarks for multiplication/division by scale factor ---

    @Benchmark
    public void multiplyByScaleFactor(Blackhole bh) {
        // Test a small factor
        bh.consume(scale6f.multiplyByScaleFactor(10));
    }

    @Benchmark
    public void multiplyByScaleFactorExact(Blackhole bh) {
        // Test a factor that results in an overflow if the input is too large
        try {
            bh.consume(scale6f.multiplyByScaleFactorExact(2000000L));
        } catch (ArithmeticException e) {
            // Expected for large inputs, ignore exception for timing purposes
        }
    }

    @Benchmark
    public void mulloByScaleFactor(Blackhole bh) {
        // Test a small factor
        bh.consume(scale6f.mulloByScaleFactor(5));
    }

    @Benchmark
    public void mulhiByScaleFactor(Blackhole bh) {
        // Test a small factor
        bh.consume(scale6f.mulhiByScaleFactor(5));
    }

    @Benchmark
    public void divideByScaleFactor(Blackhole bh) {
        // Test division
        bh.consume(scale6f.divideByScaleFactor(1000000L));
    }

    @Benchmark
    public void divideUnsignedByScaleFactor(Blackhole bh) {
        // Test unsigned division
        bh.consume(scale6f.divideUnsignedByScaleFactor(1000000L));
    }

    @Benchmark
    public void moduloByScaleFactor(Blackhole bh) {
        // Test modulo
        bh.consume(scale6f.moduloByScaleFactor(1000001L));
    }

    // --- Benchmarks for arithmetic back-ends ---

    @Benchmark
    public void getArithmeticDefault(Blackhole bh) {
        bh.consume(scale6f.getDefaultArithmetic());
    }

    @Benchmark
    public void getCheckedArithmeticDefault(Blackhole bh) {
        bh.consume(scale6f.getDefaultCheckedArithmetic());
    }

    @Benchmark
    public void getRoundingHalfEvenArithmetic(Blackhole bh) {
        bh.consume(scale6f.getRoundingHalfEvenArithmetic());
    }

    @Benchmark
    public void getArithmeticWithRoundingMode(Blackhole bh) {
        // Test a specific rounding mode (e.g., HALF_UP)
        bh.consume(scale6f.getArithmetic(java.math.RoundingMode.HALF_UP));
    }

    @Benchmark
    public void getCheckedArithmeticWithRoundingMode(Blackhole bh) {
        // Test a specific rounding mode for checked arithmetic
        bh.consume(scale6f.getCheckedArithmetic(java.math.RoundingMode.HALF_EVEN));
    }

    // --- Benchmarks for String conversion ---

    @Benchmark
    public void toString(Blackhole bh) {
        // Test conversion of a large number
        bh.consume(scale6f.toString(Long.MAX_VALUE));
    }
}
