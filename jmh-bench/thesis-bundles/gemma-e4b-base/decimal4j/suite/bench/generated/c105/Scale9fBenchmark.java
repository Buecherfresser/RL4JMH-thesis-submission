package bench.generated.c105;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.scale.Scale9f;
import java.math.RoundingMode;
import org.decimal4j.truncate.OverflowMode;
import org.decimal4j.api.DecimalArithmetic;
import java.math.BigInteger;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Scale9fBenchmark {

    private Scale9f scale9f;
    private long testFactor;
    private long testDividend;
    private long testValue;
    private RoundingMode testRoundingMode;

    @Setup
    public void setup() {
        scale9f = Scale9f.INSTANCE;
        
        // Inputs for multiplication/division
        testFactor = 123456789L;
        testDividend = 9876543210L;
        
        // Input for toString(long value)
        testValue = 1234567890123L;

        // Inputs for arithmetic retrieval
        testRoundingMode = RoundingMode.HALF_UP;
    }

    @Benchmark
    public void benchmarkGetScale(Blackhole bh) {
        int scale = scale9f.getScale();
        bh.consume(scale);
    }

    @Benchmark
    public void benchmarkGetScaleFactor(Blackhole bh) {
        long factor = scale9f.getScaleFactor();
        bh.consume(factor);
    }

    @Benchmark
    public void benchmarkGetScaleFactorAsBigInteger(Blackhole bh) {
        BigInteger bi = scale9f.getScaleFactorAsBigInteger();
        bh.consume(bi);
    }

    @Benchmark
    public void benchmarkMultiplyByScaleFactor(Blackhole bh) {
        long result = scale9f.multiplyByScaleFactor(testFactor);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkMultiplyByScaleFactorExact(Blackhole bh) {
        long result = scale9f.multiplyByScaleFactorExact(testFactor);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkDivideByScaleFactor(Blackhole bh) {
        long result = scale9f.divideByScaleFactor(testDividend);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkModuloByScaleFactor(Blackhole bh) {
        long result = scale9f.moduloByScaleFactor(testDividend);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkGetDefaultArithmetic(Blackhole bh) {
        DecimalArithmetic arith = scale9f.getDefaultArithmetic();
        bh.consume(arith);
    }

    @Benchmark
    public void benchmarkGetRoundingDownArithmetic(Blackhole bh) {
        DecimalArithmetic arith = scale9f.getRoundingDownArithmetic();
        bh.consume(arith);
    }

    @Benchmark
    public void benchmarkGetArithmeticByRoundingMode(Blackhole bh) {
        DecimalArithmetic arith = scale9f.getArithmetic(testRoundingMode);
        bh.consume(arith);
    }

    @Benchmark
    public void benchmarkToStringLong(Blackhole bh) {
        String result = scale9f.toString(testValue);
        bh.consume(result);
    }
}
