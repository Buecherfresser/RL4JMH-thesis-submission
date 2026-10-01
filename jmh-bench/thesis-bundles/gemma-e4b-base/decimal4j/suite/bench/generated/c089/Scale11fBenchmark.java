package bench.generated.c089;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.math.RoundingMode;
import org.decimal4j.scale.Scale11f;
import org.decimal4j.truncate.OverflowMode;
import org.decimal4j.api.DecimalArithmetic;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Scale11fBenchmark {

    private Scale11f scale11f;

    // Inputs for arithmetic operations
    private long testLong;
    private int testInt;
    private RoundingMode testRoundingMode;

    @Setup(Level.Trial)
    public void setup() {
        scale11f = Scale11f.INSTANCE;
        
        // Representative inputs
        testLong = 1234567890123L;
        testInt = 100;
        testRoundingMode = RoundingMode.HALF_UP;
    }

    // --- Basic Arithmetic Operations ---

    @Benchmark
    public void multiplyByScaleFactor_long(Blackhole bh) {
        long result = scale11f.multiplyByScaleFactor(testLong);
        bh.consume(result);
    }

    @Benchmark
    public void multiplyByScaleFactorExact_long(Blackhole bh) {
        // Using a value known not to overflow
        long result = scale11f.multiplyByScaleFactorExact(testLong / 10);
        bh.consume(result);
    }

    @Benchmark
    public void mulloByScaleFactor_int(Blackhole bh) {
        long result = scale11f.mulloByScaleFactor(testInt);
        bh.consume(result);
    }

    @Benchmark
    public void mulhiByScaleFactor_int(Blackhole bh) {
        long result = scale11f.mulhiByScaleFactor(testInt);
        bh.consume(result);
    }

    @Benchmark
    public void divideByScaleFactor_long(Blackhole bh) {
        long result = scale11f.divideByScaleFactor(testLong);
        bh.consume(result);
    }

    @Benchmark
    public void divideUnsignedByScaleFactor_long(Blackhole bh) {
        long unsignedDividend = testLong | 0xFFFFFFFF00000000L; // Ensure it's treated as unsigned
        long result = scale11f.divideUnsignedByScaleFactor(unsignedDividend);
        bh.consume(result);
    }

    @Benchmark
    public void moduloByScaleFactor_long(Blackhole bh) {
        long result = scale11f.moduloByScaleFactor(testLong);
        bh.consume(result);
    }

    // --- Conversions and String Representation ---

    @Benchmark
    public void toString_long(Blackhole bh) {
        String result = scale11f.toString(testLong);
        bh.consume(result);
    }

    // --- Validation and Limits ---

    @Benchmark
    public void isValidIntegerValue_long(Blackhole bh) {
        boolean result = scale11f.isValidIntegerValue(testLong);
        bh.consume(result);
    }

    // --- Arithmetic Backend Lookups ---

    @Benchmark
    public void getArithmetic_roundingMode(Blackhole bh) {
        // Test lookup based on RoundingMode
        DecimalArithmetic result = scale11f.getArithmetic(testRoundingMode);
        bh.consume(result);
    }

    @Benchmark
    public void getCheckedArithmetic_roundingMode(Blackhole bh) {
        // Test lookup based on RoundingMode
        DecimalArithmetic result = scale11f.getCheckedArithmetic(testRoundingMode);
        bh.consume(result);
    }
}
