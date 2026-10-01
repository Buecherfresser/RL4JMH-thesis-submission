package bench.generated.c087;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.scale.Scale0f;
import java.math.RoundingMode;
import org.decimal4j.truncate.TruncationPolicy;
import org.decimal4j.truncate.OverflowMode;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Scale0fBenchmark {

    private Scale0f scale0f;
    private long sampleLong;
    private TruncationPolicy sampleTruncationPolicy;

    @Setup(Level.Trial)
    public void setup() {
        scale0f = Scale0f.INSTANCE;
        sampleLong = 123456789L;
        
        // Setup a representative truncation policy
        sampleTruncationPolicy = new TruncationPolicy() {
            @Override
            public OverflowMode getOverflowMode() {
                return OverflowMode.UNCHECKED;
            }

            @Override
            public RoundingMode getRoundingMode() {
                return RoundingMode.HALF_UP;
            }
        };
    }

    @Benchmark
    public void benchmarkGetScale(Blackhole bh) {
        int scale = scale0f.getScale();
        bh.consume(scale);
    }

    @Benchmark
    public void benchmarkGetScaleFactor(Blackhole bh) {
        long factor = scale0f.getScaleFactor();
        bh.consume(factor);
    }

    @Benchmark
    public void benchmarkGetScaleFactorAsBigInteger(Blackhole bh) {
        java.math.BigInteger bigInt = scale0f.getScaleFactorAsBigInteger();
        bh.consume(bigInt);
    }

    @Benchmark
    public void benchmarkToStringLong(Blackhole bh) {
        String result = scale0f.toString(sampleLong);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkToString(Blackhole bh) {
        String result = scale0f.toString();
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkMultiplyByScaleFactor(Blackhole bh) {
        long factor = 100L;
        long result = scale0f.multiplyByScaleFactor(factor);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkMulloByScaleFactor(Blackhole bh) {
        int factor = 0xDEADBEEF;
        long result = scale0f.mulloByScaleFactor(factor);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkGetDefaultArithmetic(Blackhole bh) {
        org.decimal4j.api.DecimalArithmetic arithmetic = scale0f.getDefaultArithmetic();
        bh.consume(arithmetic);
    }

    @Benchmark
    public void benchmarkGetArithmeticByRoundingMode(Blackhole bh) {
        RoundingMode mode = RoundingMode.HALF_EVEN;
        org.decimal4j.api.DecimalArithmetic arithmetic = scale0f.getArithmetic(mode);
        bh.consume(arithmetic);
    }

    @Benchmark
    public void benchmarkGetArithmeticByTruncationPolicy(Blackhole bh) {
        org.decimal4j.api.DecimalArithmetic arithmetic = scale0f.getArithmetic(sampleTruncationPolicy);
        bh.consume(arithmetic);
    }
}
