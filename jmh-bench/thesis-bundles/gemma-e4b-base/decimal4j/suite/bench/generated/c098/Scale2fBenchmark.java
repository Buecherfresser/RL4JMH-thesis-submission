package bench.generated.c098;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.scale.Scale2f;
import java.math.RoundingMode;
import org.decimal4j.truncate.TruncationPolicy;
import org.decimal4j.truncate.OverflowMode;
import org.decimal4j.api.DecimalArithmetic;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Scale2fBenchmark {

    private Scale2f scale2f;

    // Representative inputs
    private long largePositiveLong;
    private long largeNegativeLong;
    private long boundaryLong;
    private int representativeInt;

    @Setup
    public void setup() {
        scale2f = Scale2f.INSTANCE;
        
        // Inputs for arithmetic operations
        largePositiveLong = 1234567890123L;
        largeNegativeLong = -9876543210987L;
        
        // Input near boundary for testing validity/exact multiplication
        boundaryLong = Scale2f.INSTANCE.getMaxIntegerValue() - 1; 
        
        representativeInt = 123;
    }

    @Benchmark
    public void testMultiplyByScaleFactor(Blackhole bh) {
        long result = scale2f.multiplyByScaleFactor(largePositiveLong);
        bh.consume(result);
    }

    @Benchmark
    public void testMultiplyByScaleFactorNegative(Blackhole bh) {
        long result = scale2f.multiplyByScaleFactor(largeNegativeLong);
        bh.consume(result);
    }

    @Benchmark
    public void testIsValidIntegerValue(Blackhole bh) {
        boolean result = scale2f.isValidIntegerValue(largePositiveLong);
        bh.consume(result);
    }

    @Benchmark
    public void testMultiplyByScaleFactorExact(Blackhole bh) {
        // Test a value that should not overflow
        long result = scale2f.multiplyByScaleFactorExact(boundaryLong);
        bh.consume(result);
    }

    @Benchmark
    public void testDivideByScaleFactor(Blackhole bh) {
        long result = scale2f.divideByScaleFactor(largePositiveLong);
        bh.consume(result);
    }

    @Benchmark
    public void testModuloByScaleFactor(Blackhole bh) {
        long result = scale2f.moduloByScaleFactor(largePositiveLong);
        bh.consume(result);
    }

    @Benchmark
    public void testDivideUnsignedByScaleFactor(Blackhole bh) {
        // Use a large unsigned value
        long unsignedDividend = 0xFFFFFFFFFFFFFFFFL;
        long result = scale2f.divideUnsignedByScaleFactor(unsignedDividend);
        bh.consume(result);
    }

    @Benchmark
    public void testToStringLong(Blackhole bh) {
        String result = scale2f.toString(largePositiveLong);
        bh.consume(result);
    }

    @Benchmark
    public void testGetArithmeticRoundingMode(Blackhole bh) {
        // Test a specific rounding mode lookup
        DecimalArithmetic result = scale2f.getArithmetic(RoundingMode.HALF_UP);
        bh.consume(result);
    }

    @Benchmark
    public void testGetCheckedArithmeticRoundingMode(Blackhole bh) {
        // Test a specific rounding mode lookup
        DecimalArithmetic result = scale2f.getCheckedArithmetic(RoundingMode.DOWN);
        bh.consume(result);
    }
}
