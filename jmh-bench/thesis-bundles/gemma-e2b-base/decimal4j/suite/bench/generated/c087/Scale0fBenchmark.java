package bench.generated.c087;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.math.BigDecimal;

import org.decimal4j.scale.Scale0f;
import org.decimal4j.api.DecimalArithmetic;
import org.decimal4j.truncate.OverflowMode;
import org.decimal4j.truncate.TruncationPolicy;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Scale0fBenchmark {

    // Inputs prepared in @Setup
    private long testDividend;
    private long testFactor;
    private int testMulloFactor;
    private int testMulhiFactor;
    private long testUnsignedDividend;

    // Constants derived from Scale0f
    private final long SCALE_FACTOR = Scale0f.SCALE_FACTOR;

    @Setup
    public void setup() {
        // Setup inputs that are not static final literals inside the benchmark methods.
        testDividend = 123456789L;
        testFactor = 10L;
        testMulloFactor = 5;
        testMulhiFactor = 100;
        testUnsignedDividend = 987654321L;
    }

    @Benchmark
    public void multiplyByScaleFactor(Blackhole bh) {
        long result = Scale0f.INSTANCE.multiplyByScaleFactor(testFactor);
        bh.consume(result);
    }

    @Benchmark
    public void multiplyByScaleFactorExact(Blackhole bh) {
        long result = Scale0f.INSTANCE.multiplyByScaleFactorExact(testFactor);
        bh.consume(result);
    }

    @Benchmark
    public void mulloByScaleFactor(Blackhole bh) {
        long result = Scale0f.INSTANCE.mulloByScaleFactor(testMulloFactor);
        bh.consume(result);
    }

    @Benchmark
    public void mulhiByScaleFactor(Blackhole bh) {
        long result = Scale0f.INSTANCE.mulhiByScaleFactor(testMulhiFactor);
        bh.consume(result);
    }

    @Benchmark
    public void divideByScaleFactor(Blackhole bh) {
        long result = Scale0f.INSTANCE.divideByScaleFactor(testDividend);
        bh.consume(result);
    }

    @Benchmark
    public void divideUnsignedByScaleFactor(Blackhole bh) {
        long result = Scale0f.INSTANCE.divideUnsignedByScaleFactor(testUnsignedDividend);
        bh.consume(result);
    }

    @Benchmark
    public void moduloByScaleFactor(Blackhole bh) {
        long result = Scale0f.INSTANCE.moduloByScaleFactor(testDividend);
        bh.consume(result);
    }

    @Benchmark
    public void toString(Blackhole bh) {
        String result = Scale0f.INSTANCE.toString(testDividend);
        bh.consume(result);
    }

    @Benchmark
    public void getDefaultArithmetic(Blackhole bh) {
        DecimalArithmetic result = Scale0f.INSTANCE.getDefaultArithmetic();
        bh.consume(result);
    }

    @Benchmark
    public void getCheckedArithmetic(Blackhole bh) {
        DecimalArithmetic result = Scale0f.INSTANCE.getDefaultCheckedArithmetic();
        bh.consume(result);
    }

    @Benchmark
    public void getArithmeticWithRoundingMode(Blackhole bh) {
        // Test a specific rounding mode (e.g., HALF_UP)
        DecimalArithmetic result = Scale0f.INSTANCE.getArithmetic(java.math.RoundingMode.HALF_UP);
        bh.consume(result);
    }
}
