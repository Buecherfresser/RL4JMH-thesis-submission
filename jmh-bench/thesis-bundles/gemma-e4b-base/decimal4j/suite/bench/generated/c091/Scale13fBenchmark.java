package bench.generated.c091;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.math.RoundingMode;
import org.decimal4j.scale.Scale13f;
import org.decimal4j.truncate.TruncationPolicy;
import org.decimal4j.truncate.OverflowMode;
import org.decimal4j.api.DecimalArithmetic;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Scale13fBenchmark {

    // Inputs moved to State fields
    private long testFactor;
    private long testDividend;
    private long testModuloDividend;
    private long testValue;

    @Setup(Level.Trial)
    public void setup() {
        // Initialize inputs in setup instead of using static final literals
        testFactor = 1234567890123L;
        testDividend = 987654321098765L;
        testModuloDividend = 10000000000000L + 5;
        testValue = 1234567890123L;
    }

    @Benchmark
    public void multiplyByScaleFactor(Blackhole bh) {
        long result = Scale13f.INSTANCE.multiplyByScaleFactor(testFactor);
        bh.consume(result);
    }

    @Benchmark
    public void divideByScaleFactor(Blackhole bh) {
        long result = Scale13f.INSTANCE.divideByScaleFactor(testDividend);
        bh.consume(result);
    }

    @Benchmark
    public void moduloByScaleFactor(Blackhole bh) {
        long result = Scale13f.INSTANCE.moduloByScaleFactor(testModuloDividend);
        bh.consume(result);
    }

    @Benchmark
    public void multiplyByScaleFactorExact(Blackhole bh) {
        // Test successful path
        long result = Scale13f.INSTANCE.multiplyByScaleFactorExact(testFactor);
        bh.consume(result);
    }

    @Benchmark
    public void toStringLong(Blackhole bh) {
        // toString returns String, not long
        String result = Scale13f.INSTANCE.toString(testValue);
        bh.consume(result);
    }

    @Benchmark
    public void getArithmeticRoundingMode(Blackhole bh) {
        // Test lookup using standard rounding mode
        DecimalArithmetic result = Scale13f.INSTANCE.getArithmetic(RoundingMode.HALF_UP);
        bh.consume(result);
    }
}
