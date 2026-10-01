package bench.generated.c095;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.scale.Scale17f;
import java.math.RoundingMode;
import org.decimal4j.truncate.OverflowMode;
import org.decimal4j.api.DecimalArithmetic;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Scale17fBenchmark {

    private Scale17f scale = Scale17f.INSTANCE;

    // Inputs for arithmetic operations
    private long testLongValue = 123456789L;
    private long largeLongValue = 900000000000000000L;
    private int testIntFactor = 10;
    private int largeIntFactor = 2000000000;

    // Inputs for complex method calls
    private RoundingMode testRoundingMode = RoundingMode.HALF_UP;

    @Setup
    public void setup() {
        // Inputs are simple primitives, so setup is minimal.
    }

    // --- Simple Read Operations ---

    @Benchmark
    public int benchmarkGetScale() {
        return scale.getScale();
    }

    @Benchmark
    public long benchmarkGetScaleFactor() {
        return scale.getScaleFactor();
    }

    @Benchmark
    public int benchmarkGetScaleFactorNumberOfLeadingZeros() {
        return scale.getScaleFactorNumberOfLeadingZeros();
    }

    @Benchmark
    public String benchmarkToString() {
        return scale.toString();
    }

    // --- Arithmetic Operations ---

    @Benchmark
    public long benchmarkMultiplyByScaleFactor() {
        return scale.multiplyByScaleFactor(testLongValue);
    }

    @Benchmark
    public long benchmarkMultiplyByScaleFactorWithLargeInput() {
        return scale.multiplyByScaleFactor(largeLongValue);
    }

    @Benchmark
    public long benchmarkMultiplyByScaleFactorExact() {
        // Use a value that should not overflow
        return scale.multiplyByScaleFactorExact(testLongValue);
    }

    @Benchmark
    public long benchmarkMulloByScaleFactor() {
        return scale.mulloByScaleFactor(testIntFactor);
    }

    @Benchmark
    public long benchmarkMulhiByScaleFactor() {
        return scale.mulhiByScaleFactor(testIntFactor);
    }

    @Benchmark
    public long benchmarkDivideByScaleFactor() {
        return scale.divideByScaleFactor(testLongValue);
    }

    @Benchmark
    public long benchmarkDivideUnsignedByScaleFactor() {
        return scale.divideUnsignedByScaleFactor(largeLongValue);
    }

    @Benchmark
    public long benchmarkModuloByScaleFactor() {
        return scale.moduloByScaleFactor(testLongValue);
    }

    // --- Validation and Conversion ---

    @Benchmark
    public boolean benchmarkIsValidIntegerValue() {
        return scale.isValidIntegerValue(testLongValue);
    }

    @Benchmark
    public String benchmarkToStringWithLongValue() {
        return scale.toString(testLongValue);
    }

    // --- DecimalArithmetic Retrieval ---

    @Benchmark
    public DecimalArithmetic benchmarkGetDefaultArithmetic() {
        return scale.getDefaultArithmetic();
    }

    @Benchmark
    public DecimalArithmetic benchmarkGetRoundingDownArithmetic() {
        return scale.getRoundingDownArithmetic();
    }

    @Benchmark
    public DecimalArithmetic benchmarkGetRoundingFloorArithmetic() {
        return scale.getRoundingFloorArithmetic();
    }

    @Benchmark
    public DecimalArithmetic benchmarkGetRoundingHalfEvenArithmetic() {
        return scale.getRoundingHalfEvenArithmetic();
    }

    @Benchmark
    public DecimalArithmetic benchmarkGetRoundingUnnecessaryArithmetic() {
        return scale.getRoundingUnnecessaryArithmetic();
    }

    @Benchmark
    public DecimalArithmetic benchmarkGetArithmeticByRoundingMode() {
        return scale.getArithmetic(testRoundingMode);
    }

    @Benchmark
    public DecimalArithmetic benchmarkGetCheckedArithmeticByRoundingMode() {
        return scale.getCheckedArithmetic(testRoundingMode);
    }
}
