package bench.generated.c107;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.math.RoundingMode;
import org.decimal4j.truncate.CheckedRounding;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class CheckedRoundingBenchmark {

    // Since CheckedRounding is an enum, we don't need instance state.
    // We rely on static calls or enum constant access.

    @Benchmark
    public void benchmarkValueOf(Blackhole bh) {
        // Test static lookup method
        CheckedRounding.valueOf(RoundingMode.HALF_UP);
        bh.consume(null);
    }

    @Benchmark
    public void benchmarkGetRoundingMode(Blackhole bh) {
        // Test method call on an enum constant
        CheckedRounding.HALF_EVEN.getRoundingMode();
        bh.consume(null);
    }

    @Benchmark
    public void benchmarkToUncheckedRounding(Blackhole bh) {
        // Test abstract method call (which is implemented for all constants)
        CheckedRounding.UP.toUncheckedRounding();
        bh.consume(null);
    }

    @Benchmark
    public void benchmarkValueOfUnnecessary(Blackhole bh) {
        // Test another static lookup
        CheckedRounding.valueOf(RoundingMode.UNNECESSARY);
        bh.consume(null);
    }
}
