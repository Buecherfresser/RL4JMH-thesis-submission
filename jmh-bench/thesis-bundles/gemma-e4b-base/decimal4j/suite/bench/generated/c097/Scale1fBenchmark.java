package bench.generated.c097;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.scale.Scale1f;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import org.decimal4j.api.DecimalArithmetic;
import org.decimal4j.truncate.OverflowMode;
import org.decimal4j.truncate.TruncationPolicy;
import org.decimal4j.truncate.DecimalRounding;
import org.decimal4j.arithmetic.CheckedScaleNfRoundingArithmetic;
import org.decimal4j.arithmetic.CheckedScaleNfTruncatingArithmetic;
import org.decimal4j.arithmetic.UncheckedScaleNfRoundingArithmetic;
import org.decimal4j.arithmetic.UncheckedScaleNfTruncatingArithmetic;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Scale1fBenchmark {

    private Scale1f scale1f;

    // Inputs for testing
    private long typicalValue;
    private long nearMaxValidValue;
    private long nearMinValidValue;
    private long overflowCandidate;
    private int smallIntInput; // Added for methods requiring int input

    @Setup(Level.Trial)
    public void setup() {
        scale1f = Scale1f.INSTANCE;
        
        // Typical value
        typicalValue = 1234567890123L;
        
        // Value near MAX_INTEGER_VALUE (Long.MAX_VALUE / 10)
        nearMaxValidValue = Scale1f.INSTANCE.getMaxIntegerValue() - 1;
        
        // Value near MIN_INTEGER_VALUE (Long.MIN_VALUE / 10)
        nearMinValidValue = Scale1f.INSTANCE.getMinIntegerValue() + 1;
        
        // Value that would overflow when multiplied by 10 (e.g., Long.MAX_VALUE / 5)
        overflowCandidate = Long.MAX_VALUE / 5;
        
        // Small integer input for methods expecting int
        smallIntInput = 123;
    }

    // --- Basic Getters ---

    @Benchmark
    public void benchmarkGetScale(Blackhole bh) {
        bh.consume(scale1f.getScale());
    }

    @Benchmark
    public void benchmarkGetScaleFactor(Blackhole bh) {
        bh.consume(scale1f.getScaleFactor());
    }

    @Benchmark
    public void benchmarkGetScaleFactorNumberOfLeadingZeros(Blackhole bh) {
        bh.consume(scale1f.getScaleFactorNumberOfLeadingZeros());
    }

    // --- Scale Factor Conversions ---

    @Benchmark
    public void benchmarkMultiplyByScaleFactor(Blackhole bh) {
        bh.consume(scale1f.multiplyByScaleFactor(typicalValue));
    }

    @Benchmark
    public void benchmarkGetScaleFactorAsBigInteger(Blackhole bh) {
        bh.consume(scale1f.getScaleFactorAsBigInteger());
    }

    @Benchmark
    public void benchmarkGetScaleFactorAsBigDecimal(Blackhole bh) {
        bh.consume(scale1f.getScaleFactorAsBigDecimal());
    }

    // --- Range Checks and Exact Operations ---

    @Benchmark
    public void benchmarkGetMaxIntegerValue(Blackhole bh) {
        bh.consume(scale1f.getMaxIntegerValue());
    }

    @Benchmark
    public void benchmarkGetMinIntegerValue(Blackhole bh) {
        bh.consume(scale1f.getMinIntegerValue());
    }

    @Benchmark
    public void benchmarkIsValidIntegerValue(Blackhole bh) {
        bh.consume(scale1f.isValidIntegerValue(typicalValue));
    }

    @Benchmark
    public void benchmarkMultiplyByScaleFactorExact(Blackhole bh) {
        // Use a value known not to overflow
        bh.consume(scale1f.multiplyByScaleFactorExact(nearMaxValidValue));
    }

    // --- Bitwise and Integer Operations ---

    @Benchmark
    public void benchmarkMulloByScaleFactor(Blackhole bh) {
        // Use smallIntInput (int)
        bh.consume(scale1f.mulloByScaleFactor(smallIntInput));
    }

    @Benchmark
    public void benchmarkMulhiByScaleFactor(Blackhole bh) {
        // Use smallIntInput (int)
        bh.consume(scale1f.mulhiByScaleFactor(smallIntInput));
    }

    // --- Division and Modulo ---

    @Benchmark
    public void benchmarkDivideByScaleFactor(Blackhole bh) {
        bh.consume(scale1f.divideByScaleFactor(typicalValue));
    }

    @Benchmark
    public void benchmarkDivideUnsignedByScaleFactor(Blackhole bh) {
        // Use a large unsigned value
        long unsignedDividend = 0xFFFFFFFFFFFFFFFFL;
        bh.consume(scale1f.divideUnsignedByScaleFactor(unsignedDividend));
    }

    @Benchmark
    public void benchmarkModuloByScaleFactor(Blackhole bh) {
        bh.consume(scale1f.moduloByScaleFactor(typicalValue));
    }

    // --- String Conversion ---

    @Benchmark
    public void benchmarkToStringLong(Blackhole bh) {
        bh.consume(scale1f.toString(typicalValue));
    }

    @Benchmark
    public void benchmarkToStringDefault(Blackhole bh) {
        bh.consume(scale1f.toString());
    }

    // --- Arithmetic Backend Retrieval ---

    @Benchmark
    public void benchmarkGetDefaultArithmetic(Blackhole bh) {
        bh.consume(scale1f.getDefaultArithmetic());
    }

    @Benchmark
    public void benchmarkGetDefaultCheckedArithmetic(Blackhole bh) {
        bh.consume(scale1f.getDefaultCheckedArithmetic());
    }

    @Benchmark
    public void benchmarkGetRoundingDownArithmetic(Blackhole bh) {
        bh.consume(scale1f.getRoundingDownArithmetic());
    }

    @Benchmark
    public void benchmarkGetRoundingFloorArithmetic(Blackhole bh) {
        bh.consume(scale1f.getRoundingFloorArithmetic());
    }

    @Benchmark
    public void benchmarkGetRoundingHalfEvenArithmetic(Blackhole bh) {
        bh.consume(scale1f.getRoundingHalfEvenArithmetic());
    }

    @Benchmark
    public void benchmarkGetRoundingUnnecessaryArithmetic(Blackhole bh) {
        bh.consume(scale1f.getRoundingUnnecessaryArithmetic());
    }

    // --- Arithmetic Backend Retrieval by Policy/Mode ---

    @Benchmark
    public void benchmarkGetArithmeticByRoundingMode(Blackhole bh) {
        // Test HALF_UP
        bh.consume(scale1f.getArithmetic(RoundingMode.HALF_UP));
    }

    @Benchmark
    public void benchmarkGetCheckedArithmeticByRoundingMode(Blackhole bh) {
        // Test DOWN
        bh.consume(scale1f.getCheckedArithmetic(RoundingMode.DOWN));
    }
}
