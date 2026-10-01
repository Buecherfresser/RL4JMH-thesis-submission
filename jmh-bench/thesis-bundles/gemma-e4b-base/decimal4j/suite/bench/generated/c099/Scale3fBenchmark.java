package bench.generated.c099;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.scale.Scale3f;
import java.math.BigDecimal;
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
public class Scale3fBenchmark {

    private Scale3f scale3fInstance;
    private long testLongValue;
    private long overflowTestValue;

    @Setup(Level.Trial)
    public void setup() {
        scale3fInstance = Scale3f.INSTANCE;
        // Standard value for arithmetic tests
        testLongValue = 1234567890123L;
        // Value designed to potentially cause overflow when multiplied by 1000
        overflowTestValue = Long.MAX_VALUE / 2; 
    }

    @Benchmark
    public void testMultiplyByScaleFactor(Blackhole bh) {
        long result = scale3fInstance.multiplyByScaleFactor(testLongValue);
        bh.consume(result);
    }

    @Benchmark
    public void testMultiplyByScaleFactorExact(Blackhole bh) {
        // Test a value that should not overflow
        long result = scale3fInstance.multiplyByScaleFactorExact(testLongValue);
        bh.consume(result);
    }

    @Benchmark
    public void testDivideByScaleFactor(Blackhole bh) {
        long result = scale3fInstance.divideByScaleFactor(testLongValue);
        bh.consume(result);
    }

    @Benchmark
    public void testModuloByScaleFactor(Blackhole bh) {
        long result = scale3fInstance.moduloByScaleFactor(testLongValue);
        bh.consume(result);
    }

    @Benchmark
    public void testToStringLong(Blackhole bh) {
        // Tests the usage of the default arithmetic backend
        String result = scale3fInstance.toString(testLongValue);
        bh.consume(result);
    }

    @Benchmark
    public void testGetArithmeticRoundingMode(Blackhole bh) {
        // Test retrieval of a specific arithmetic backend
        // Using HALF_UP as a representative mode
        Object result = scale3fInstance.getArithmetic(RoundingMode.HALF_UP);
        bh.consume(result);
    }

    // Removed testGetArithmeticTruncationPolicy because TruncationPolicy is abstract and cannot be instantiated.
}
