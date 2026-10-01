package bench.generated.c103;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.scale.Scale7f;
import java.math.RoundingMode;
import org.decimal4j.api.DecimalArithmetic;
import org.decimal4j.truncate.OverflowMode;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Scale7fBenchmark {

    private long testFactor;
    private long testDividend;
    private int testIntFactor;
    private RoundingMode testRoundingMode;
    private long testLongValue;

    @Setup(Level.Trial)
    public void setup() {
        // Inputs for multiplication/division
        testFactor = 123456789L;
        testDividend = 9876543210L;
        testIntFactor = 12345;
        testLongValue = 1234567890123L;

        // Inputs for arithmetic backend retrieval
        testRoundingMode = RoundingMode.HALF_UP;
    }

    @Benchmark
    public void benchmarkMultiplyByScaleFactor(Blackhole bh) {
        long result = Scale7f.INSTANCE.multiplyByScaleFactor(testFactor);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkMultiplyByScaleFactorExact(Blackhole bh) {
        // Use a factor that is well within bounds to avoid exception handling overhead
        long result = Scale7f.INSTANCE.multiplyByScaleFactorExact(testFactor);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkDivideByScaleFactor(Blackhole bh) {
        long result = Scale7f.INSTANCE.divideByScaleFactor(testDividend);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkModuloByScaleFactor(Blackhole bh) {
        long result = Scale7f.INSTANCE.moduloByScaleFactor(testDividend);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkToStringLong(Blackhole bh) {
        String result = Scale7f.INSTANCE.toString(testLongValue);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkGetArithmeticRoundingMode(Blackhole bh) {
        DecimalArithmetic result = Scale7f.INSTANCE.getArithmetic(testRoundingMode);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkMulloByScaleFactor(Blackhole bh) {
        long result = Scale7f.INSTANCE.mulloByScaleFactor(testIntFactor);
        bh.consume(result);
    }
}
