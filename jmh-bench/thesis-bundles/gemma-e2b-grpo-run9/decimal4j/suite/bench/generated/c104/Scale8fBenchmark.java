package bench.generated.c104;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.scale.Scale8f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Scale8fBenchmark {

    // We use the singleton instance for benchmarking
    // Since Scale8f is an enum, this is safe and efficient.
    private final Scale8f scale8f = Scale8f.INSTANCE;

    @Setup
    public void setup() {
        // No complex setup needed for the singleton enum instance.
    }

    // --- Benchmarks for simple getters and constants ---

    @Benchmark
    public void getScale(Blackhole bh) {
        bh.consume(scale8f.getScale());
    }

    @Benchmark
    public void getScaleFactor(Blackhole bh) {
        bh.consume(scale8f.getScaleFactor());
    }

    @Benchmark
    public void getScaleFactorAsBigInteger(Blackhole bh) {
        bh.consume(scale8f.getScaleFactorAsBigInteger());
    }

    @Benchmark
    public void isValidIntegerValue(Blackhole bh) {
        // Test a valid value
        bh.consume(scale8f.isValidIntegerValue(1000000000L));
        // Test an invalid value (overflow check)
        bh.consume(scale8f.isValidIntegerValue(Long.MAX_VALUE / Scale8f.SCALE_FACTOR + 1));
    }

    // --- Benchmarks for multiplication/division by scale factor ---

    @Benchmark
    public void multiplyByScaleFactor(Blackhole bh) {
        // Test a small factor
        bh.consume(scale8f.multiplyByScaleFactor(100L));
    }

    @Benchmark
    public void multiplyByScaleFactorExact(Blackhole bh) {
        // Test a factor that should not overflow
        bh.consume(scale8f.multiplyByScaleFactorExact(1000L));
    }

    @Benchmark
    public void mulloByScaleFactor(Blackhole bh) {
        // Test a factor that fits within the long mask (0xffffffffL)
        bh.consume(scale8f.mulloByScaleFactor(100000000));
    }

    @Benchmark
    public void mulhiByScaleFactor(Blackhole bh) {
        // Test a factor that should result in 0 (based on implementation)
        bh.consume(scale8f.mulhiByScaleFactor(100000000));
    }

    @Benchmark
    public void divideByScaleFactor(Blackhole bh) {
        // Test division
        bh.consume(scale8f.divideByScaleFactor(1000000000L));
    }

    @Benchmark
    public void divideUnsignedByScaleFactor(Blackhole bh) {
        // Test unsigned division
        bh.consume(scale8f.divideUnsignedByScaleFactor(Long.MAX_VALUE));
    }

    @Benchmark
    public void moduloByScaleFactor(Blackhole bh) {
        // Test modulo operation
        bh.consume(scale8f.moduloByScaleFactor(100000000L + 1));
    }

    // --- Benchmarks for arithmetic back-ends ---

    @Benchmark
    public void getArithmeticDefault(Blackhole bh) {
        bh.consume(scale8f.getDefaultArithmetic());
    }

    @Benchmark
    public void getCheckedArithmeticDefault(Blackhole bh) {
        bh.consume(scale8f.getDefaultCheckedArithmetic());
    }

    @Benchmark
    public void getRoundingDownArithmetic(Blackhole bh) {
        bh.consume(scale8f.getRoundingDownArithmetic());
    }

    @Benchmark
    public void getRoundingHalfEvenArithmetic(Blackhole bh) {
        bh.consume(scale8f.getRoundingHalfEvenArithmetic());
    }

    @Benchmark
    public void getArithmeticHalfUp(Blackhole bh) {
        bh.consume(scale8f.getArithmetic(java.math.RoundingMode.HALF_UP));
    }

    @Benchmark
    public void getArithmeticUnnecessary(Blackhole bh) {
        bh.consume(scale8f.getRoundingUnnecessaryArithmetic());
    }

    // --- Benchmarks for toString ---

    @Benchmark
    public void toString(Blackhole bh) {
        // Test conversion of a long value
        bh.consume(scale8f.toString(123456789L));
    }
}
