package bench.generated.c092;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.math.RoundingMode;
import java.math.BigDecimal;
import org.decimal4j.scale.Scale14f;
import org.decimal4j.api.DecimalArithmetic;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Scale14fBenchmark {

    private Scale14f scaleInstance;

    // Inputs for arithmetic operations
    private long multiplicationFactor;
    private long divisionDividend;
    private int mulloFactor;
    private int mulhiFactor;

    // Inputs for lookup operations
    private RoundingMode testRoundingMode;

    @Setup(Level.Trial)
    public void setup() {
        scaleInstance = Scale14f.INSTANCE;

        // Setup inputs
        multiplicationFactor = 1234567890123L;
        divisionDividend = 987654321098765L;
        mulloFactor = 0xDEADBEEF;
        mulhiFactor = 0xCAFEBABE;

        // Setup lookup inputs
        testRoundingMode = RoundingMode.HALF_UP;
    }

    @Benchmark
    public long testMultiplyByScaleFactor() {
        long result = scaleInstance.multiplyByScaleFactor(multiplicationFactor);
        return result;
    }

    @Benchmark
    public long testMultiplyByScaleFactorExact() {
        // Use a factor that is within bounds to avoid exception overhead skewing results
        long factor = 100L;
        long result = scaleInstance.multiplyByScaleFactorExact(factor);
        return result;
    }

    @Benchmark
    public long testDivideByScaleFactor() {
        long result = scaleInstance.divideByScaleFactor(divisionDividend);
        return result;
    }

    @Benchmark
    public long testModuloByScaleFactor() {
        long result = scaleInstance.moduloByScaleFactor(divisionDividend);
        return result;
    }

    @Benchmark
    public long testMulloByScaleFactor() {
        long result = scaleInstance.mulloByScaleFactor(mulloFactor);
        return result;
    }

    @Benchmark
    public long testMulhiByScaleFactor() {
        long result = scaleInstance.mulhiByScaleFactor(mulhiFactor);
        return result;
    }

    @Benchmark
    public BigDecimal testGetScaleFactorAsBigDecimal() {
        BigDecimal result = scaleInstance.getScaleFactorAsBigDecimal();
        return result;
    }

    @Benchmark
    public DecimalArithmetic testGetArithmeticByRoundingMode() {
        DecimalArithmetic result = scaleInstance.getArithmetic(testRoundingMode);
        return result;
    }
}
