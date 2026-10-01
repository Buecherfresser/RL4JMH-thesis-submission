package bench.generated.c104;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.scale.Scale8f;
import java.math.RoundingMode;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Scale8fBenchmark {

    private Scale8f scale;

    // Inputs for testing
    private long testFactor;
    private long testDividend;
    private long testValue;
    private RoundingMode testRoundingMode;

    @Setup(Level.Trial)
    public void setup() {
        scale = Scale8f.INSTANCE;
        
        // Representative inputs
        testFactor = 100L;
        testDividend = 1234567890123L;
        testValue = 987654321L;
        
        // Test various modes
        testRoundingMode = RoundingMode.HALF_UP;
    }

    @Benchmark
    public void benchmarkGetScale(Blackhole bh) {
        bh.consume(scale.getScale());
    }

    @Benchmark
    public void benchmarkGetScaleFactor(Blackhole bh) {
        bh.consume(scale.getScaleFactor());
    }

    @Benchmark
    public void benchmarkGetScaleFactorNumberOfLeadingZeros(Blackhole bh) {
        bh.consume(scale.getScaleFactorNumberOfLeadingZeros());
    }

    @Benchmark
    public void benchmarkMultiplyByScaleFactor(Blackhole bh) {
        bh.consume(scale.multiplyByScaleFactor(testFactor));
    }

    @Benchmark
    public void benchmarkGetScaleFactorAsBigInteger(Blackhole bh) {
        bh.consume(scale.getScaleFactorAsBigInteger());
    }

    @Benchmark
    public void benchmarkGetScaleFactorAsBigDecimal(Blackhole bh) {
        bh.consume(scale.getScaleFactorAsBigDecimal());
    }

    @Benchmark
    public void benchmarkGetMaxIntegerValue(Blackhole bh) {
        bh.consume(scale.getMaxIntegerValue());
    }

    @Benchmark
    public void benchmarkGetMinIntegerValue(Blackhole bh) {
        bh.consume(scale.getMinIntegerValue());
    }

    @Benchmark
    public void benchmarkIsValidIntegerValue(Blackhole bh) {
        bh.consume(scale.isValidIntegerValue(testValue));
    }

    @Benchmark
    public void benchmarkMultiplyByScaleFactorExact(Blackhole bh) {
        bh.consume(scale.multiplyByScaleFactorExact(testFactor));
    }
	
    @Benchmark
    public void benchmarkMulloByScaleFactor(Blackhole bh) {
        bh.consume(scale.mulloByScaleFactor(123));
    }

    @Benchmark
    public void benchmarkMulhiByScaleFactor(Blackhole bh) {
        bh.consume(scale.mulhiByScaleFactor(123));
    }

    @Benchmark
    public void benchmarkDivideByScaleFactor(Blackhole bh) {
        bh.consume(scale.divideByScaleFactor(testDividend));
    }

    @Benchmark
    public void benchmarkDivideUnsignedByScaleFactor(Blackhole bh) {
        bh.consume(scale.divideUnsignedByScaleFactor(testDividend));
    }

    @Benchmark
    public void benchmarkModuloByScaleFactor(Blackhole bh) {
        bh.consume(scale.moduloByScaleFactor(testDividend));
    }

    @Benchmark
    public void benchmarkToStringLong(Blackhole bh) {
        bh.consume(scale.toString(testValue));
    }

    @Benchmark
    public void benchmarkGetDefaultArithmetic(Blackhole bh) {
        bh.consume(scale.getDefaultArithmetic());
    }

    @Benchmark
    public void benchmarkGetDefaultCheckedArithmetic(Blackhole bh) {
        bh.consume(scale.getDefaultCheckedArithmetic());
    }

    @Benchmark
    public void benchmarkGetRoundingDownArithmetic(Blackhole bh) {
        bh.consume(scale.getRoundingDownArithmetic());
    }

    @Benchmark
    public void benchmarkGetRoundingFloorArithmetic(Blackhole bh) {
        bh.consume(scale.getRoundingFloorArithmetic());
    }

    @Benchmark
    public void benchmarkGetRoundingHalfEvenArithmetic(Blackhole bh) {
        bh.consume(scale.getRoundingHalfEvenArithmetic());
    }

    @Benchmark
    public void benchmarkGetRoundingUnnecessaryArithmetic(Blackhole bh) {
        bh.consume(scale.getRoundingUnnecessaryArithmetic());
    }

    @Benchmark
    public void benchmarkGetArithmeticRoundingMode(Blackhole bh) {
        bh.consume(scale.getArithmetic(testRoundingMode));
    }

    @Benchmark
    public void benchmarkGetCheckedArithmeticRoundingMode(Blackhole bh) {
        bh.consume(scale.getCheckedArithmetic(testRoundingMode));
    }

    @Benchmark
    public void benchmarkToString(Blackhole bh) {
        bh.consume(scale.toString());
    }
}
