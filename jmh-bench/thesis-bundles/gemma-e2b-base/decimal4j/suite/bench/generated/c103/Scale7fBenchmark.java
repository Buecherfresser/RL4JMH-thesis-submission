package bench.generated.c103;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.math.RoundingMode;

import org.decimal4j.api.DecimalArithmetic;
import org.decimal4j.scale.Scale7f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Scale7fBenchmark {

    // --- Setup State ---
    private long largeFactor;
    private long smallFactor;
    private long maxIntValue;
    private long minIntValue;
    private long testDividend;
    private long testValueForToString;
    private RoundingMode roundingMode;

    @Setup
    public void setup() {
        // Constants derived from Scale7f
        this.largeFactor = Scale7f.SCALE_FACTOR; // 10,000,000L
        this.smallFactor = 1;

        // Calculate boundary values since private fields cannot be accessed
        this.maxIntValue = Long.MAX_VALUE / Scale7f.SCALE_FACTOR;
        this.minIntValue = Long.MIN_VALUE / Scale7f.SCALE_FACTOR;

        // Test dividend (a value well within bounds)
        this.testDividend = 123456789L;

        // Test value for toString
        this.testValueForToString = 9876543210L;

        // Test rounding mode
        this.roundingMode = RoundingMode.HALF_UP;
    }

    // --- Benchmarks for Scale7f methods ---

    @Benchmark
    public void multiplyByScaleFactor(Blackhole bh) {
        long result = Scale7f.INSTANCE.multiplyByScaleFactor(1000L);
        bh.consume(result);
    }

    @Benchmark
    public void multiplyByScaleFactorExact(Blackhole bh) {
        // Test multiplication near the boundary to trigger potential overflow check
        long factor = 10000000L / 2; // Safe factor
        long result = Scale7f.INSTANCE.multiplyByScaleFactorExact(factor);
        bh.consume(result);
    }

    @Benchmark
    public void divideByScaleFactor(Blackhole bh) {
        long result = Scale7f.INSTANCE.divideByScaleFactor(testDividend);
        bh.consume(result);
    }

    @Benchmark
    public void divideUnsignedByScaleFactor(Blackhole bh) {
        long unsignedDividend = 1000000000000000000L; // Large unsigned number
        long result = Scale7f.INSTANCE.divideUnsignedByScaleFactor(unsignedDividend);
        bh.consume(result);
    }

    @Benchmark
    public void moduloByScaleFactor(Blackhole bh) {
        long result = Scale7f.INSTANCE.moduloByScaleFactor(testDividend);
        bh.consume(result);
    }

    @Benchmark
    public void isValidIntegerValue(Blackhole bh) {
        // Test valid value
        long validValue = maxIntValue / 2;
        boolean isValid = Scale7f.INSTANCE.isValidIntegerValue(validValue);
        bh.consume(isValid);

        // Test invalid value (overflow)
        long invalidValue = maxIntValue + 1;
        boolean isInvalid = Scale7f.INSTANCE.isValidIntegerValue(invalidValue);
        bh.consume(isInvalid);
    }

    @Benchmark
    public void toString(Blackhole bh) {
        String result = Scale7f.INSTANCE.toString(testValueForToString);
        bh.consume(result);
    }

    @Benchmark
    public void getArithmetic(Blackhole bh) {
        // Test default arithmetic lookup
        DecimalArithmetic arithmetic = Scale7f.INSTANCE.getDefaultArithmetic();
        bh.consume(arithmetic);
    }

    @Benchmark
    public void getArithmeticWithRoundingMode(Blackhole bh) {
        // Test specific rounding mode arithmetic lookup
        DecimalArithmetic arithmetic = Scale7f.INSTANCE.getArithmetic(roundingMode);
        bh.consume(arithmetic);
    }
}
