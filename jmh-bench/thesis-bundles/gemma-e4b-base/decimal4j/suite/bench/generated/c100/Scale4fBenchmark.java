package bench.generated.c100;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.scale.Scale4f;
import java.math.RoundingMode;
import org.decimal4j.truncate.TruncationPolicy;
import org.decimal4j.truncate.OverflowMode;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Scale4fBenchmark {

    private long representativeLongValue;
    private long largePositiveValue;
    private long largeNegativeValue;
    private long maxIntegerValue;
    private long minIntegerValue;

    @Setup
    public void setup() {
        // Representative values for testing arithmetic
        representativeLongValue = 1234567890123L;
        largePositiveValue = Long.MAX_VALUE / 2;
        largeNegativeValue = Long.MIN_VALUE / 2;

        // Pre-calculate boundary values
        maxIntegerValue = Scale4f.INSTANCE.getMaxIntegerValue();
        minIntegerValue = Scale4f.INSTANCE.getMinIntegerValue();
    }

    // --- Simple Arithmetic Operations ---

    @Benchmark
    public void multiplyByScaleFactor_Representative(Blackhole bh) {
        long result = Scale4f.INSTANCE.multiplyByScaleFactor(representativeLongValue);
        bh.consume(result);
    }

    @Benchmark
    public void divideByScaleFactor_Representative(Blackhole bh) {
        long result = Scale4f.INSTANCE.divideByScaleFactor(representativeLongValue);
        bh.consume(result);
    }

    @Benchmark
    public void moduloByScaleFactor_Representative(Blackhole bh) {
        long result = Scale4f.INSTANCE.moduloByScaleFactor(representativeLongValue);
        bh.consume(result);
    }

    @Benchmark
    public void divideUnsignedByScaleFactor_Representative(Blackhole bh) {
        // Use a large positive value as unsigned dividend
        long unsignedDividend = largePositiveValue;
        long result = Scale4f.INSTANCE.divideUnsignedByScaleFactor(unsignedDividend);
        bh.consume(result);
    }

    // --- Boundary and Validation Checks ---

    @Benchmark
    public void isValidIntegerValue_Valid(Blackhole bh) {
        long value = representativeLongValue;
        boolean result = Scale4f.INSTANCE.isValidIntegerValue(value);
        bh.consume(result);
    }

    @Benchmark
    public void isValidIntegerValue_InvalidTooHigh(Blackhole bh) {
        long value = maxIntegerValue + 1;
        boolean result = Scale4f.INSTANCE.isValidIntegerValue(value);
        bh.consume(result);
    }

    @Benchmark
    public void multiplyByScaleFactorExact_Valid(Blackhole bh) {
        long factor = representativeLongValue;
        long result = Scale4f.INSTANCE.multiplyByScaleFactorExact(factor);
        bh.consume(result);
    }

    @Benchmark
    public void multiplyByScaleFactorExact_Overflow(Blackhole bh) {
        // Use a value that will cause overflow when multiplied by 10000
        long factor = maxIntegerValue + 1;
        try {
            Scale4f.INSTANCE.multiplyByScaleFactorExact(factor);
        } catch (ArithmeticException e) {
            // Expected exception, consume it to prevent dead code elimination
            bh.consume(e);
        }
    }

    // --- Complex Operations (String Conversion) ---

    @Benchmark
    public void toString_LongValue(Blackhole bh) {
        long value = representativeLongValue;
        String result = Scale4f.INSTANCE.toString(value);
        bh.consume(result);
    }

    // --- Arithmetic Lookup Methods ---

    @Benchmark
    public void getArithmetic_RoundingMode_HalfUp(Blackhole bh) {
        RoundingMode rm = RoundingMode.HALF_UP;
        org.decimal4j.api.DecimalArithmetic arithmetic = Scale4f.INSTANCE.getArithmetic(rm);
        bh.consume(arithmetic);
    }

    @Benchmark
    public void getArithmetic_RoundingMode_Down(Blackhole bh) {
        RoundingMode rm = RoundingMode.DOWN;
        org.decimal4j.api.DecimalArithmetic arithmetic = Scale4f.INSTANCE.getArithmetic(rm);
        bh.consume(arithmetic);
    }

    // Note: Benchmarks relying on TruncationPolicyImpl have been removed as the class is not publicly available.

    // --- Constant/Metadata Retrieval ---

    @Benchmark
    public void getScaleFactorAsBigDecimal(Blackhole bh) {
        java.math.BigDecimal result = Scale4f.INSTANCE.getScaleFactorAsBigDecimal();
        bh.consume(result);
    }

    @Benchmark
    public void getScaleFactorAsBigInteger(Blackhole bh) {
        java.math.BigInteger result = Scale4f.INSTANCE.getScaleFactorAsBigInteger();
        bh.consume(result);
    }
}
