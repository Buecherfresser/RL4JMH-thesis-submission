package bench.generated.c111;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.math.RoundingMode;
import org.decimal4j.truncate.UncheckedRounding;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class UncheckedRoundingBenchmark {

    private UncheckedRounding roundingUp;
    private UncheckedRounding roundingDown;
    private UncheckedRounding roundingHalfEven;

    @Setup(Level.Trial)
    public void setup() {
        // Initialize constants for benchmarking
        roundingUp = UncheckedRounding.UP;
        roundingDown = UncheckedRounding.DOWN;
        roundingHalfEven = UncheckedRounding.HALF_EVEN;
    }

    @Benchmark
    public void benchmarkGetRoundingMode_UP(Blackhole bh) {
        bh.consume(roundingUp.getRoundingMode());
    }

    @Benchmark
    public void benchmarkGetRoundingMode_DOWN(Blackhole bh) {
        bh.consume(roundingDown.getRoundingMode());
    }

    @Benchmark
    public void benchmarkGetRoundingMode_HALF_EVEN(Blackhole bh) {
        bh.consume(roundingHalfEven.getRoundingMode());
    }

    @Benchmark
    public void benchmarkToCheckedRounding_UP(Blackhole bh) {
        bh.consume(roundingUp.toCheckedRounding());
    }

    @Benchmark
    public void benchmarkToCheckedRounding_DOWN(Blackhole bh) {
        bh.consume(roundingDown.toCheckedRounding());
    }

    @Benchmark
    public void benchmarkToCheckedRounding_HALF_EVEN(Blackhole bh) {
        bh.consume(roundingHalfEven.toCheckedRounding());
    }

    @Benchmark
    public void benchmarkGetOverflowMode(Blackhole bh) {
        bh.consume(roundingUp.getOverflowMode());
    }

    @Benchmark
    public void benchmarkToString_UP(Blackhole bh) {
        bh.consume(roundingUp.toString());
    }

    @Benchmark
    public void benchmarkToString_HALF_EVEN(Blackhole bh) {
        bh.consume(roundingHalfEven.toString());
    }

    @Benchmark
    public void benchmarkValueOf_UP(Blackhole bh) {
        bh.consume(UncheckedRounding.valueOf(RoundingMode.UP));
    }

    @Benchmark
    public void benchmarkValueOf_HALF_EVEN(Blackhole bh) {
        bh.consume(UncheckedRounding.valueOf(RoundingMode.HALF_EVEN));
    }
}
