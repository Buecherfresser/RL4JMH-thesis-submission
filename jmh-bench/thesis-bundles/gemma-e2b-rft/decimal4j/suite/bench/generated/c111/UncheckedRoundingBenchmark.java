package bench.generated.c111;

import org.decimal4j.truncate.UncheckedRounding;
import java.math.RoundingMode;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class UncheckedRoundingBenchmark {

    // State fields to hold constants for testing
    private UncheckedRounding upRounding;
    private UncheckedRounding halfEvenRounding;
    private RoundingMode halfEvenMode;

    @Setup
    public void setup() {
        // Initialize constants to be used in benchmarks
        this.upRounding = UncheckedRounding.UP;
        this.halfEvenRounding = UncheckedRounding.HALF_EVEN;
        this.halfEvenMode = RoundingMode.HALF_EVEN;
    }

    /**
     * Benchmark testing the getRoundingMode() method on an enum constant.
     */
    @Benchmark
    public void testGetRoundingMode(Blackhole bh) {
        RoundingMode mode = upRounding.getRoundingMode();
        bh.consume(mode);
    }

    /**
     * Benchmark testing the toCheckedRounding() method on an enum constant.
     */
    @Benchmark
    public void testToCheckedRounding(Blackhole bh) {
        org.decimal4j.truncate.CheckedRounding checked = halfEvenRounding.toCheckedRounding();
        bh.consume(checked);
    }

    /**
     * Benchmark testing the getOverflowMode() method on an enum constant.
     */
    @Benchmark
    public void testGetOverflowMode(Blackhole bh) {
        org.decimal4j.truncate.OverflowMode overflow = upRounding.getOverflowMode();
        bh.consume(overflow);
    }

    /**
     * Benchmark testing the toString() method on an enum constant.
     */
    @Benchmark
    public void testToString(Blackhole bh) {
        String result = halfEvenRounding.toString();
        bh.consume(result);
    }

    /**
     * Benchmark testing the static factory method valueOf(RoundingMode).
     */
    @Benchmark
    public void testValueOfFromRoundingMode(Blackhole bh) {
        UncheckedRounding roundingConstant = UncheckedRounding.valueOf(halfEvenMode);
        bh.consume(roundingConstant);
    }
}
