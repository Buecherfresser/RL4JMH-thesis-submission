package bench.generated.c090;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

import org.decimal4j.scale.Scale12f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Scale12fBenchmark {

    // Since Scale12f is a singleton enum, no instance state is required.

    @Benchmark
    public void getScale(Blackhole bh) {
        bh.consume(Scale12f.INSTANCE.getScale());
    }

    @Benchmark
    public void getScaleFactor(Blackhole bh) {
        bh.consume(Scale12f.INSTANCE.getScaleFactor());
    }

    @Benchmark
    public void multiplyByScaleFactor(Blackhole bh) {
        // Test a multiplication operation
        bh.consume(Scale12f.INSTANCE.multiplyByScaleFactor(100L));
    }

    @Benchmark
    public void multiplyByScaleFactorExact(Blackhole bh) {
        // Test an operation that might throw an exception (overflow check)
        try {
            Scale12f.INSTANCE.multiplyByScaleFactorExact(Long.MAX_VALUE / 2);
        } catch (ArithmeticException e) {
            // Expected if the input causes overflow, which is fine for benchmarking error paths
        }
        bh.consume(Scale12f.INSTANCE.multiplyByScaleFactorExact(1000000000000L));
    }

    @Benchmark
    public void mulloByScaleFactor(Blackhole bh) {
        // Test multiplication using the lower bits
        bh.consume(Scale12f.INSTANCE.mulloByScaleFactor(10));
    }

    @Benchmark
    public void mulhiByScaleFactor(Blackhole bh) {
        // Test multiplication using the higher bits
        bh.consume(Scale12f.INSTANCE.mulhiByScaleFactor(10));
    }

    @Benchmark
    public void divideByScaleFactor(Blackhole bh) {
        // Test division
        bh.consume(Scale12f.INSTANCE.divideByScaleFactor(1000000000000L));
    }

    @Benchmark
    public void divideUnsignedByScaleFactor(Blackhole bh) {
        // Test unsigned division
        bh.consume(Scale12f.INSTANCE.divideUnsignedByScaleFactor(Long.MAX_VALUE));
    }

    @Benchmark
    public void moduloByScaleFactor(Blackhole bh) {
        // Test modulo operation
        bh.consume(Scale12f.INSTANCE.moduloByScaleFactor(1000000000001L));
    }

    @Benchmark
    public void toString(Blackhole bh) {
        // Test toString conversion (which relies on internal arithmetic)
        bh.consume(Scale12f.INSTANCE.toString(12345L));
    }
}
