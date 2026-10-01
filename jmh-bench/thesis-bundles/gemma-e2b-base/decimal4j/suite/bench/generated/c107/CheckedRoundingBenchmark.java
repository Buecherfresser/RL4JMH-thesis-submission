package bench.generated.c107;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.math.RoundingMode;
import java.util.Set;

import org.decimal4j.truncate.CheckedRounding;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class CheckedRoundingBenchmark {

    // State for static method benchmarking
    private RoundingMode testRoundingMode;

    // State for instance method benchmarking
    private CheckedRounding upConstant;
    private CheckedRounding halfEvenConstant;

    @Setup
    public void setup() {
        // Setup input for static method tests
        this.testRoundingMode = RoundingMode.HALF_EVEN;

        // Setup inputs for instance method tests
        this.upConstant = CheckedRounding.UP;
        this.halfEvenConstant = CheckedRounding.HALF_EVEN;
    }

    @Benchmark
    public void benchmarkValueOf(Blackhole bh) {
        CheckedRounding result = CheckedRounding.valueOf(testRoundingMode);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkGetRoundingMode(Blackhole bh) {
        RoundingMode mode = upConstant.getRoundingMode();
        bh.consume(mode);
    }

    @Benchmark
    public void benchmarkToUncheckedRounding(Blackhole bh) {
        org.decimal4j.truncate.UncheckedRounding unchecked = halfEvenConstant.toUncheckedRounding();
        bh.consume(unchecked);
    }
}
