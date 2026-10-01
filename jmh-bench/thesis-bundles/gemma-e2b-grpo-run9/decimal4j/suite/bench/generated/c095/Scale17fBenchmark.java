package bench.generated.c095;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

import org.decimal4j.scale.Scale17f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Scale17fBenchmark {

    // Since Scale17f is an enum singleton, we don't need instance fields
    // unless we were benchmarking a mutable object.

    @Benchmark
    public void getScale(Blackhole bh) {
        // Calling the method on the singleton instance
        bh.consume(Scale17f.INSTANCE.getScale());
    }

    @Benchmark
    public void getScaleFactor(Blackhole bh) {
        bh.consume(Scale17f.INSTANCE.getScaleFactor());
    }

    @Benchmark
    public void multiplyByScaleFactor(Blackhole bh) {
        // Test multiplication by a small factor
        try {
            long result = Scale17f.INSTANCE.multiplyByScaleFactor(1000);
            bh.consume(result);
        } catch (ArithmeticException e) {
            // Ignore expected exceptions for this benchmark mode if they occur
        }
    }

    @Benchmark
    public void mulloByScaleFactor(Blackhole bh) {
        // Test multiplication using the low bits
        long result = Scale17f.INSTANCE.mulloByScaleFactor(10);
        bh.consume(result);
    }

    @Benchmark
    public void mulhiByScaleFactor(Blackhole bh) {
        // Test multiplication using the high bits
        long result = Scale17f.INSTANCE.mulhiByScaleFactor(10);
        bh.consume(result);
    }

    @Benchmark
    public void divideByScaleFactor(Blackhole bh) {
        // Test division
        long result = Scale17f.INSTANCE.divideByScaleFactor(100000000000000000L);
        bh.consume(result);
    }

    @Benchmark
    public void divideUnsignedByScaleFactor(Blackhole bh) {
        // Test unsigned division
        long result = Scale17f.INSTANCE.divideUnsignedByScaleFactor(100000000000000000L);
        bh.consume(result);
    }

    @Benchmark
    public void moduloByScaleFactor(Blackhole bh) {
        // Test modulo operation
        long result = Scale17f.INSTANCE.moduloByScaleFactor(100000000000000000L);
        bh.consume(result);
    }

    @Benchmark
    public void toString(Blackhole bh) {
        // Test toString conversion (requires a long input, which we can't easily pass
        // without violating the zero-parameter rule, so we call it with a dummy value
        // or rely on the internal implementation if it doesn't rely on external state).
        // Since the method signature is toString(long value), we must call it with a value.
        // We use a constant long value here.
        bh.consume(Scale17f.INSTANCE.toString(12345L));
    }
}
