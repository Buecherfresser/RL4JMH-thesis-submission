package bench.generated.c107;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.math.RoundingMode;
import org.decimal4j.truncate.CheckedRounding;
import org.decimal4j.truncate.UncheckedRounding;
import org.decimal4j.truncate.OverflowMode;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class CheckedRoundingBenchmark {

    // Input setup: A set of RoundingModes to test the static valueOf method.
    private RoundingMode[] roundingModes;

    @Setup
    public void setup() {
        // Define the set of RoundingModes we want to test against.
        roundingModes = new RoundingMode[]{
            RoundingMode.UP,
            RoundingMode.DOWN,
            RoundingMode.CEILING,
            RoundingMode.FLOOR,
            RoundingMode.HALF_UP,
            RoundingMode.HALF_DOWN,
            RoundingMode.HALF_EVEN,
            RoundingMode.UNNECESSARY
        };
    }

    /**
     * Benchmark testing the static factory method valueOf(RoundingMode).
     * This tests the mapping from RoundingMode to CheckedRounding enum constant.
     */
    @Benchmark
    public void testValueOfMapping(Blackhole bh) {
        // Cycle through the defined RoundingModes
        for (RoundingMode mode : roundingModes) {
            CheckedRounding constant = CheckedRounding.valueOf(mode);
            bh.consume(constant);
        }
    }

    /**
     * Benchmark testing the property retrieval (getRoundingMode) for a specific constant.
     * This ensures the constant correctly holds its associated RoundingMode.
     */
    @Benchmark
    public void testGetRoundingMode_HALF_UP(Blackhole bh) {
        CheckedRounding constant = CheckedRounding.HALF_UP;
        bh.consume(constant.getRoundingMode());
    }

    /**
     * Benchmark testing the conversion method toUncheckedRounding() for a specific constant.
     * This ensures the policy correctly maps to its unchecked counterpart.
     */
    @Benchmark
    public void testToUncheckedRounding_FLOOR(Blackhole bh) {
        CheckedRounding constant = CheckedRounding.FLOOR;
        bh.consume(constant.toUncheckedRounding());
    }

    /**
     * Benchmark testing the overflow mode property, which should always be CHECKED.
     */
    @Benchmark
    public void testGetOverflowMode(Blackhole bh) {
        CheckedRounding constant = CheckedRounding.UP;
        bh.consume(constant.getOverflowMode());
    }
}
