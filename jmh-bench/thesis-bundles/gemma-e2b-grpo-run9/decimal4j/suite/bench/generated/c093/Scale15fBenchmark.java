package bench.generated.c093;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.math.BigDecimal;
import java.math.BigInteger;

import org.decimal4j.scale.Scale15f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Scale15fBenchmark {

    // We use the singleton instance for benchmarking
    private final Scale15f scale15f = Scale15f.INSTANCE;

    // State fields for read-only or reusable inputs
    private long testValue;

    @Setup
    public void setup() {
        // Initialize a value that is within the valid range for testing
        this.testValue = 1000000000000000L; // A large number, but safe for testing
    }

    @Benchmark
    public void getScale(Blackhole bh) {
        bh.consume(scale15f.getScale());
    }

    @Benchmark
    public void getScaleFactor(Blackhole bh) {
        bh.consume(scale15f.getScaleFactor());
    }

    @Benchmark
    public void getScaleFactorNumberOfLeadingZeros(Blackhole bh) {
        bh.consume(scale15f.getScaleFactorNumberOfLeadingZeros());
    }

    @Benchmark
    public void multiplyByScaleFactor(Blackhole bh) {
        try {
            scale15f.multiplyByScaleFactor(1000L);
        } catch (Exception e) {
            // Expected if overflow occurs, which is fine for testing the method path
        }
    }

    @Benchmark
    public void multiplyByScaleFactorExact(Blackhole bh) {
        try {
            scale15f.multiplyByScaleFactorExact(1000L);
        } catch (Exception e) {
            // Expected if overflow occurs
        }
    }

    @Benchmark
    public void mulloByScaleFactor(Blackhole bh) {
        try {
            scale15f.mulloByScaleFactor(10);
        } catch (Exception e) {
            // Catch potential exceptions if the implementation throws them
        }
    }

    @Benchmark
    public void mulhiByScaleFactor(Blackhole bh) {
        try {
            scale15f.mulhiByScaleFactor(10);
        } catch (Exception e) {
            // Catch potential exceptions if the implementation throws them
        }
    }

    @Benchmark
    public void divideByScaleFactor(Blackhole bh) {
        try {
            scale15f.divideByScaleFactor(1000000000000000L);
        } catch (Exception e) {
            // Expected if division by zero or other issues occur
        }
    }

    @Benchmark
    public void divideUnsignedByScaleFactor(Blackhole bh) {
        try {
            scale15f.divideUnsignedByScaleFactor(1000000000000000L);
        } catch (Exception e) {
            // Expected if division by zero or other issues occur
        }
    }

    @Benchmark
    public void moduloByScaleFactor(Blackhole bh) {
        try {
            scale15f.moduloByScaleFactor(1000000000000000L);
        } catch (Exception e) {
            // Expected if division by zero or other issues occur
        }
    }

    @Benchmark
    public void toString(Blackhole bh) {
        try {
            scale15f.toString(testValue);
        } catch (Exception e) {
            // Expected if toString throws exceptions
        }
    }
}
