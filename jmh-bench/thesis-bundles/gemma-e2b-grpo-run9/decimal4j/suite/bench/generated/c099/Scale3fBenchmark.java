package bench.generated.c099;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

import org.decimal4j.scale.Scale3f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Scale3fBenchmark {

    // We use the singleton instance for benchmarking
    private Scale3f scale3f = Scale3f.INSTANCE;

    @Setup
    public void setup() {
        // Setup logic, if any, would go here.
    }

    // --- Benchmarks for simple getters and constants ---

    @Benchmark
    public void getScale(Blackhole bh) {
        bh.consume(scale3f.getScale());
    }

    @Benchmark
    public void getScaleFactor(Blackhole bh) {
        bh.consume(scale3f.getScaleFactor());
    }

    @Benchmark
    public void getScaleFactorAsBigInteger(Blackhole bh) {
        bh.consume(scale3f.getScaleFactorAsBigInteger());
    }

    // --- Benchmarks for arithmetic operations ---

    @Benchmark
    public void multiplyByScaleFactor(Blackhole bh) {
        // Test multiplication by a factor that stays within bounds
        long result = scale3f.multiplyByScaleFactor(100);
        bh.consume(result);
    }

    @Benchmark
    public void multiplyByScaleFactorExact_Success(Blackhole bh) {
        // Test multiplication by a factor that stays within bounds
        try {
            long result = scale3f.multiplyByScaleFactorExact(100);
            bh.consume(result);
        } catch (ArithmeticException e) {
            // Ignore expected exceptions for this specific test case
        }
    }

    @Benchmark
    public void multiplyByScaleFactorExact_Overflow(Blackhole bh) {
        // Test multiplication that causes overflow
        try {
            scale3f.multiplyByScaleFactorExact(Long.MAX_VALUE / 1000L + 1);
        } catch (ArithmeticException e) {
            // Expected
        }
    }

    @Benchmark
    public void divideByScaleFactor(Blackhole bh) {
        // Test division
        long result = scale3f.divideByScaleFactor(1000000L);
        bh.consume(result);
    }

    @Benchmark
    public void divideUnsignedByScaleFactor(Blackhole bh) {
        // Test unsigned division
        long result = scale3f.divideUnsignedByScaleFactor(Long.MAX_VALUE);
        bh.consume(result);
    }

    @Benchmark
    public void moduloByScaleFactor(Blackhole bh) {
        // Test modulo operation
        long result = scale3f.moduloByScaleFactor(1000000L);
        bh.consume(result);
    }

    // --- Benchmarks for arithmetic back-ends (using default arithmetic) ---

    @Benchmark
    public void getArithmetic_Default(Blackhole bh) {
        bh.consume(scale3f.getDefaultArithmetic());
    }

    @Benchmark
    public void getArithmetic_Checked(Blackhole bh) {
        bh.consume(scale3f.getDefaultCheckedArithmetic());
    }

    // --- Benchmarks for arithmetic back-ends (using specific rounding modes) ---

    @Benchmark
    public void getArithmetic_RoundingDown(Blackhole bh) {
        bh.consume(scale3f.getRoundingDownArithmetic());
    }

    @Benchmark
    public void getArithmetic_HalfEven(Blackhole bh) {
        bh.consume(scale3f.getRoundingHalfEvenArithmetic());
    }

    // --- Benchmarks for conversion/toString (using a representative long value) ---

    @Benchmark
    public void toString(Blackhole bh) {
        // Test toString on a value that should be representable
        bh.consume(scale3f.toString(123456789L));
    }
}
