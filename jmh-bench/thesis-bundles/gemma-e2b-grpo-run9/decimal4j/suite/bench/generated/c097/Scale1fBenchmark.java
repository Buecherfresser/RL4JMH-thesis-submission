package bench.generated.c097;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.math.RoundingMode;

import org.decimal4j.scale.Scale1f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Scale1fBenchmark {

    // We use the singleton instance for benchmarking
    private Scale1f scale1f = Scale1f.INSTANCE;

    @Setup
    public void setup() {
        // Setup logic, if any, can go here.
    }

    // --- Benchmarks for Scale1f methods ---

    @Benchmark
    public void getScale(Blackhole bh) {
        bh.consume(scale1f.getScale());
    }

    @Benchmark
    public void getScaleFactor(Blackhole bh) {
        bh.consume(scale1f.getScaleFactor());
    }

    @Benchmark
    public void getScaleFactorNumberOfLeadingZeros(Blackhole bh) {
        bh.consume(scale1f.getScaleFactorNumberOfLeadingZeros());
    }

    @Benchmark
    public void multiplyByScaleFactor(Blackhole bh) {
        // Test multiplication by a small factor
        bh.consume(scale1f.multiplyByScaleFactor(2L));
    }

    @Benchmark
    public void multiplyByScaleFactorExact(Blackhole bh) {
        // Test multiplication by a factor within bounds
        try {
            bh.consume(scale1f.multiplyByScaleFactorExact(10L));
        } catch (ArithmeticException e) {
            // Expected if bounds are hit
        }
    }

    @Benchmark
    public void mulloByScaleFactor(Blackhole bh) {
        // Test multiplication by an integer factor
        bh.consume(scale1f.mulloByScaleFactor(5));
    }

    @Benchmark
    public void mulhiByScaleFactor(Blackhole bh) {
        // Test multiplication by an integer factor that should result in 0
        bh.consume(scale1f.mulhiByScaleFactor(100));
    }

    @Benchmark
    public void divideByScaleFactor(Blackhole bh) {
        // Test division
        bh.consume(scale1f.divideByScaleFactor(100L));
    }

    @Benchmark
    public void divideUnsignedByScaleFactor(Blackhole bh) {
        // Test unsigned division
        bh.consume(scale1f.divideUnsignedByScaleFactor(1000L));
    }

    @Benchmark
    public void moduloByScaleFactor(Blackhole bh) {
        // Test modulo operation
        bh.consume(scale1f.moduloByScaleFactor(100L));
    }

    @Benchmark
    public void toString(Blackhole bh) {
        // Test toString on a representative value (e.g., 123456789)
        bh.consume(scale1f.toString(123456789L));
    }

    @Benchmark
    public void getDefaultArithmetic(Blackhole bh) {
        bh.consume(scale1f.getDefaultArithmetic());
    }

    @Benchmark
    public void getCheckedArithmetic(Blackhole bh) {
        bh.consume(scale1f.getDefaultCheckedArithmetic());
    }

    @Benchmark
    public void getRoundingDownArithmetic(Blackhole bh) {
        bh.consume(scale1f.getRoundingDownArithmetic());
    }

    @Benchmark
    public void getRoundingHalfEvenArithmetic(Blackhole bh) {
        bh.consume(scale1f.getRoundingHalfEvenArithmetic());
    }
}
