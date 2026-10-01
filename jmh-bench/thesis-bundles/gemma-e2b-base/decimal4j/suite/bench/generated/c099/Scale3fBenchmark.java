package bench.generated.c099;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

import org.decimal4j.scale.Scale3f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class Scale3fBenchmark {

    // Inputs prepared in Setup
    private long largeFactor;
    private long largeDividend;
    private long validValue;
    private long invalidValue;

    @Setup(Level.Trial)
    public void setup() {
        // Setup large inputs that are within reasonable bounds for testing
        // MAX_INTEGER_VALUE is Long.MAX_VALUE / 1000L
        this.largeFactor = Long.MAX_VALUE / 1000L - 100;
        this.largeDividend = Long.MAX_VALUE / 1000L * 2;
        
        // Valid value: within [MIN_INTEGER_VALUE, MAX_INTEGER_VALUE]
        this.validValue = (long) (Long.MAX_VALUE / 1000L / 2);
        
        // Invalid value: outside the bounds
        this.invalidValue = Long.MAX_VALUE + 1000L;
    }

    @Benchmark
    public void multiplyByScaleFactor(Blackhole bh) {
        long result = Scale3f.INSTANCE.multiplyByScaleFactor(largeFactor);
        bh.consume(result);
    }

    @Benchmark
    public void multiplyByScaleFactorExact(Blackhole bh) {
        // Test multiplication that should succeed
        long result = Scale3f.INSTANCE.multiplyByScaleFactorExact(largeFactor);
        bh.consume(result);
    }

    @Benchmark
    public void divideByScaleFactor(Blackhole bh) {
        long result = Scale3f.INSTANCE.divideByScaleFactor(largeDividend);
        bh.consume(result);
    }

    @Benchmark
    public void divideUnsignedByScaleFactor(Blackhole bh) {
        // Use a large unsigned value for this test
        long unsignedDividend = Long.MAX_VALUE;
        long result = Scale3f.INSTANCE.divideUnsignedByScaleFactor(unsignedDividend);
        bh.consume(result);
    }

    @Benchmark
    public void moduloByScaleFactor(Blackhole bh) {
        long result = Scale3f.INSTANCE.moduloByScaleFactor(largeDividend);
        bh.consume(result);
    }

    @Benchmark
    public void isValidIntegerValue(Blackhole bh) {
        // Test valid input
        boolean resultValid = Scale3f.INSTANCE.isValidIntegerValue(validValue);
        bh.consume(resultValid);

        // Test invalid input
        boolean resultInvalid = Scale3f.INSTANCE.isValidIntegerValue(invalidValue);
        bh.consume(resultInvalid);
    }

    @Benchmark
    public void toString(Blackhole bh) {
        // Test conversion utility
        long value = largeFactor;
        String result = Scale3f.INSTANCE.toString(value);
        bh.consume(result);
    }
}
