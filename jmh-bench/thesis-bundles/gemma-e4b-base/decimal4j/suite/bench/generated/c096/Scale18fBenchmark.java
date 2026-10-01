package bench.generated.c096;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.math.RoundingMode;
import org.decimal4j.scale.Scale18f;
import org.decimal4j.api.DecimalArithmetic;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Scale18fBenchmark {

    private Scale18f scale;
    private long testFactor;
    private long testDividend;
    private long testValue;
    private long maxIntValue;
    private long minIntValue;

    @Setup
    public void setup() {
        scale = Scale18f.INSTANCE;
        
        // Inputs for arithmetic operations
        testFactor = 123456789L;
        testDividend = 987654321012345678L;
        testValue = 100000000000000000L; // A value well within bounds

        // Boundary values
        maxIntValue = scale.getMaxIntegerValue();
        minIntValue = scale.getMinIntegerValue();
    }

    @Benchmark
    public void testMultiplyByScaleFactor(Blackhole bh) {
        long result = scale.multiplyByScaleFactor(testFactor);
        bh.consume(result);
    }

    @Benchmark
    public void testIsValidIntegerValue(Blackhole bh) {
        boolean result = scale.isValidIntegerValue(testValue);
        bh.consume(result);
    }

    @Benchmark
    public void testMultiplyByScaleFactorExact(Blackhole bh) {
        // Use a factor that is known to be valid
        long result = scale.multiplyByScaleFactorExact(testFactor);
        bh.consume(result);
    }

    @Benchmark
    public void testMulloByScaleFactor(Blackhole bh) {
        int factor = 0xAAAAAAAA;
        long result = scale.mulloByScaleFactor(factor);
        bh.consume(result);
    }

    @Benchmark
    public void testMulHiByScaleFactor(Blackhole bh) {
        int factor = 0xBBBBBBBB;
        long result = scale.mulhiByScaleFactor(factor);
        bh.consume(result);
    }

    @Benchmark
    public void testDivideByScaleFactor(Blackhole bh) {
        long result = scale.divideByScaleFactor(testDividend);
        bh.consume(result);
    }

    @Benchmark
    public void testModuloByScaleFactor(Blackhole bh) {
        long result = scale.moduloByScaleFactor(testDividend);
        bh.consume(result);
    }

    @Benchmark
    public void testToStringLong(Blackhole bh) {
        String result = scale.toString(testValue);
        bh.consume(result);
    }

    @Benchmark
    public void testGetDefaultArithmetic(Blackhole bh) {
        DecimalArithmetic result = scale.getDefaultArithmetic();
        bh.consume(result);
    }

    @Benchmark
    public void testGetRoundingDownArithmetic(Blackhole bh) {
        DecimalArithmetic result = scale.getRoundingDownArithmetic();
        bh.consume(result);
    }

    @Benchmark
    public void testGetArithmeticByRoundingMode(Blackhole bh) {
        // Test with HALF_UP
        DecimalArithmetic result = scale.getArithmetic(RoundingMode.HALF_UP);
        bh.consume(result);
    }

    @Benchmark
    public void testGetCheckedArithmeticByRoundingMode(Blackhole bh) {
        // Test with DOWN
        DecimalArithmetic result = scale.getCheckedArithmetic(RoundingMode.DOWN);
        bh.consume(result);
    }

    @Benchmark
    public void testGetArithmeticByTruncationPolicy(Blackhole bh) {
        // Since TruncationPolicy is abstract, we use the equivalent method 
        // for unchecked arithmetic with HALF_EVEN rounding mode.
        DecimalArithmetic result = scale.getArithmetic(RoundingMode.HALF_EVEN);
        bh.consume(result);
    }
}
