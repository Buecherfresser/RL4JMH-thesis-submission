package bench.generated.c111;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.truncate.UncheckedRounding;
import java.math.RoundingMode;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class UncheckedRoundingBenchmark {

    // Since UncheckedRounding is an enum, we don't need complex mutable state.
    // We rely on static methods or enum constants for benchmarking.

    @Setup
    public void setup() {
        // Setup phase is minimal as the SUT is an enum and its methods are static or immutable.
    }

    @Benchmark
    public void benchmarkValueOf(Blackhole bh) {
        // Test the static factory method which maps RoundingMode to an UncheckedRounding constant.
        // We use a constant RoundingMode for consistency.
        UncheckedRounding.valueOf(RoundingMode.HALF_UP);
        bh.consume(null);
    }

    @Benchmark
    public void benchmarkGetRoundingMode_UP(Blackhole bh) {
        // Test a method on a specific enum constant.
        UncheckedRounding.UP.getRoundingMode();
        bh.consume(null);
    }

    @Benchmark
    public void benchmarkGetOverflowMode_DOWN(Blackhole bh) {
        // Test another method on a different constant.
        UncheckedRounding.DOWN.getOverflowMode();
        bh.consume(null);
    }

    @Benchmark
    public void benchmarkToString(Blackhole bh) {
        // Test the toString method (which is final and returns a String).
        UncheckedRounding.HALF_EVEN.toString();
        bh.consume(null);
    }
}
