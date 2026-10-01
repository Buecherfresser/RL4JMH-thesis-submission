package bench.generated.c102;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.scale.Scale6f;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import org.decimal4j.api.DecimalArithmetic;
import org.decimal4j.truncate.TruncationPolicy;
import org.decimal4j.truncate.OverflowMode;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Scale6fBenchmark {

    private Scale6f scaleMetrics;
    private long sampleLong;
    private int factorInput;

    @Setup
    public void setup() {
        scaleMetrics = Scale6f.INSTANCE;
        // Use a non-trivial long value for arithmetic tests
        sampleLong = 1234567890123L;
        // Input for multiplication tests
        factorInput = 12345;
    }

    @Benchmark
    public void testGetScale(Blackhole bh) {
        int scale = scaleMetrics.getScale();
        bh.consume(scale);
    }

    @Benchmark
    public void testGetScaleFactor(Blackhole bh) {
        long factor = scaleMetrics.getScaleFactor();
        bh.consume(factor);
    }

    @Benchmark
    public void testGetScaleFactorNumberOfLeadingZeros(Blackhole bh) {
        int nlz = scaleMetrics.getScaleFactorNumberOfLeadingZeros();
        bh.consume(nlz);
    }

    @Benchmark
    public void testMultiplyByScaleFactor(Blackhole bh) {
        long result = scaleMetrics.multiplyByScaleFactor(sampleLong);
        bh.consume(result);
    }

    @Benchmark
    public void testGetScaleFactorAsBigInteger(Blackhole bh) {
        BigInteger bi = scaleMetrics.getScaleFactorAsBigInteger();
        bh.consume(bi);
    }

    @Benchmark
    public void testGetScaleFactorAsBigDecimal(Blackhole bh) {
        BigDecimal bd = scaleMetrics.getScaleFactorAsBigDecimal();
        bh.consume(bd);
    }

    @Benchmark
    public void testGetMaxIntegerValue(Blackhole bh) {
        long max = scaleMetrics.getMaxIntegerValue();
        bh.consume(max);
    }

    @Benchmark
    public void testGetMinIntegerValue(Blackhole bh) {
        long min = scaleMetrics.getMinIntegerValue();
        bh.consume(min);
    }

    @Benchmark
    public void testIsValidIntegerValue(Blackhole bh) {
        boolean valid = scaleMetrics.isValidIntegerValue(sampleLong);
        bh.consume(valid);
    }

    @Benchmark
    public void testMultiplyByScaleFactorExact(Blackhole bh) {
        long result = scaleMetrics.multiplyByScaleFactorExact(sampleLong);
        bh.consume(result);
    }
    
    @Benchmark
    public void testMulloByScaleFactor(Blackhole bh) {
        long result = scaleMetrics.mulloByScaleFactor(factorInput);
        bh.consume(result);
    }

    @Benchmark
    public void testMulhiByScaleFactor(Blackhole bh) {
        long result = scaleMetrics.mulhiByScaleFactor(factorInput);
        bh.consume(result);
    }

    @Benchmark
    public void testDivideByScaleFactor(Blackhole bh) {
        long result = scaleMetrics.divideByScaleFactor(sampleLong);
        bh.consume(result);
    }

    @Benchmark
    public void testDivideUnsignedByScaleFactor(Blackhole bh) {
        long unsignedDividend = 0xFFFFFFFFFFFFFFFFL;
        long result = scaleMetrics.divideUnsignedByScaleFactor(unsignedDividend);
        bh.consume(result);
    }

    @Benchmark
    public void testModuloByScaleFactor(Blackhole bh) {
        long result = scaleMetrics.moduloByScaleFactor(sampleLong);
        bh.consume(result);
    }

    @Benchmark
    public void testToStringLong(Blackhole bh) {
        String result = scaleMetrics.toString(sampleLong);
        bh.consume(result);
    }

    @Benchmark
    public void testToString(Blackhole bh) {
        String result = scaleMetrics.toString();
        bh.consume(result);
    }

    @Benchmark
    public void testGetDefaultArithmetic(Blackhole bh) {
        DecimalArithmetic arith = scaleMetrics.getDefaultArithmetic();
        bh.consume(arith);
    }

    @Benchmark
    public void testGetDefaultCheckedArithmetic(Blackhole bh) {
        DecimalArithmetic arith = scaleMetrics.getDefaultCheckedArithmetic();
        bh.consume(arith);
    }

    @Benchmark
    public void testGetRoundingDownArithmetic(Blackhole bh) {
        DecimalArithmetic arith = scaleMetrics.getRoundingDownArithmetic();
        bh.consume(arith);
    }

    @Benchmark
    public void testGetRoundingFloorArithmetic(Blackhole bh) {
        DecimalArithmetic arith = scaleMetrics.getRoundingFloorArithmetic();
        bh.consume(arith);
    }

    @Benchmark
    public void testGetRoundingHalfEvenArithmetic(Blackhole bh) {
        DecimalArithmetic arith = scaleMetrics.getRoundingHalfEvenArithmetic();
        bh.consume(arith);
    }

    @Benchmark
    public void testGetRoundingUnnecessaryArithmetic(Blackhole bh) {
        DecimalArithmetic arith = scaleMetrics.getRoundingUnnecessaryArithmetic();
        bh.consume(arith);
    }

    @Benchmark
    public void testGetArithmeticRoundingMode(Blackhole bh) {
        RoundingMode rm = RoundingMode.HALF_UP;
        DecimalArithmetic arith = scaleMetrics.getArithmetic(rm);
        bh.consume(arith);
    }

    @Benchmark
    public void testGetCheckedArithmeticRoundingMode(Blackhole bh) {
        RoundingMode rm = RoundingMode.HALF_UP;
        DecimalArithmetic arith = scaleMetrics.getCheckedArithmetic(rm);
        bh.consume(arith);
    }

    @Benchmark
    public void testGetArithmeticTruncationPolicy(Blackhole bh) {
        // Fix: TruncationPolicy is abstract and cannot be instantiated.
        // We simulate the effect of an unchecked policy by using the RoundingMode API.
        DecimalArithmetic arith = scaleMetrics.getArithmetic(RoundingMode.HALF_UP);
        bh.consume(arith);
    }
}
