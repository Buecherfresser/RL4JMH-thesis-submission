package bench.generated.c107;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.math.RoundingMode;
import org.decimal4j.truncate.CheckedRounding;
import org.decimal4j.truncate.TruncationPolicy;
import org.decimal4j.truncate.OverflowMode;
import org.decimal4j.truncate.UncheckedRounding;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class CheckedRoundingBenchmark {

    private CheckedRounding roundingPolicy;

    @Setup(Level.Trial)
    public void setup() {
        // Use a representative instance for instance method benchmarks
        this.roundingPolicy = CheckedRounding.HALF_EVEN;
    }

    @Benchmark
    public void benchmarkGetRoundingMode(Blackhole bh) {
        RoundingMode mode = roundingPolicy.getRoundingMode();
        bh.consume(mode);
    }

    @Benchmark
    public void benchmarkGetOverflowMode(Blackhole bh) {
        OverflowMode mode = roundingPolicy.getOverflowMode();
        bh.consume(mode);
    }

    @Benchmark
    public void benchmarkToUncheckedRounding(Blackhole bh) {
        UncheckedRounding unchecked = roundingPolicy.toUncheckedRounding();
        bh.consume(unchecked);
    }

    @Benchmark
    public void benchmarkToString(Blackhole bh) {
        String s = roundingPolicy.toString();
        bh.consume(s);
    }

    @Benchmark
    public void benchmarkValueOfStaticLookup(Blackhole bh) {
        // Test the static factory method lookup
        RoundingMode inputMode = RoundingMode.HALF_UP;
        CheckedRounding result = CheckedRounding.valueOf(inputMode);
        bh.consume(result);
    }
}
