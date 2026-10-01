package bench.generated.c089;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

import org.decimal4j.scale.Scale11f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Scale11fBenchmark {

    // Since Scale11f is a singleton enum, we don't need instance fields.

    @Benchmark
    public void getScale(Blackhole bh) {
        // Call the static method implicitly via the singleton instance
        bh.consume(Scale11f.INSTANCE.getScale());
    }

    @Benchmark
    public void getScaleFactor(Blackhole bh) {
        bh.consume(Scale11f.INSTANCE.getScaleFactor());
    }

    @Benchmark
    public void getScaleFactorNumberOfLeadingZeros(Blackhole bh) {
        bh.consume(Scale11f.INSTANCE.getScaleFactorNumberOfLeadingZeros());
    }

    @Benchmark
    public void multiplyByScaleFactor(Blackhole bh) {
        // Test a standard multiplication using a long factor
        bh.consume(Scale11f.INSTANCE.multiplyByScaleFactor(1000L));
    }

    @Benchmark
    public void mulloByScaleFactor(Blackhole bh) {
        // Test multiplication using int factor
        bh.consume(Scale11f.INSTANCE.mulloByScaleFactor(10));
    }

    @Benchmark
    public void mulhiByScaleFactor(Blackhole bh) {
        // Test multiplication using int factor. Using a value that fits in an int
        // to avoid compilation errors related to large literals.
        bh.consume(Scale11f.INSTANCE.mulhiByScaleFactor(100000000));
    }

    @Benchmark
    public void divideByScaleFactor(Blackhole bh) {
        // Test division
        bh.consume(Scale11f.INSTANCE.divideByScaleFactor(1000000000000L));
    }

    @Benchmark
    public void divideUnsignedByScaleFactor(Blackhole bh) {
        // Test unsigned division
        bh.consume(Scale11f.INSTANCE.divideUnsignedByScaleFactor(1000000000000L));
    }

    @Benchmark
    public void moduloByScaleFactor(Blackhole bh) {
        // Test modulo operation
        bh.consume(Scale11f.INSTANCE.moduloByScaleFactor(100000000000L));
    }

    @Benchmark
    public void getMinIntegerValue(Blackhole bh) {
        bh.consume(Scale11f.INSTANCE.getMinIntegerValue());
    }

    @Benchmark
    public void isValidIntegerValue(Blackhole bh) {
        // Test a valid value
        bh.consume(Scale11f.INSTANCE.isValidIntegerValue(100000000000L));
    }

    @Benchmark
    public void toString(Blackhole bh) {
        // Test toString, which takes a long input
        bh.consume(Scale11f.INSTANCE.toString(123456789L));
    }
}
