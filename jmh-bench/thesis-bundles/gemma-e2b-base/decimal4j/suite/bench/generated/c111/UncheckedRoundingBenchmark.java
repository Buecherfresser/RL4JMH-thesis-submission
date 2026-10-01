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
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
public class UncheckedRoundingBenchmark {

    // Input setup: A set of RoundingModes to test the static valueOf method against.
    // We use an array of RoundingMode enums to ensure we cover all possible inputs.
    private RoundingMode[] roundingModes;

    @Setup
    public void setup() {
        // Initialize the array of RoundingMode values for testing the static factory method.
        this.roundingModes = RoundingMode.values();
    }

    /**
     * Benchmark testing the static factory method UncheckedRounding.valueOf(RoundingMode).
     * This tests the internal mapping logic based on the RoundingMode ordinal.
     */
    @Benchmark
    public void testValueOfMapping(Blackhole bh) {
        // Pick a random RoundingMode from the setup array to test the mapping.
        int index = (int) (Math.random() * roundingModes.length);
        RoundingMode inputMode = roundingModes[index];

        UncheckedRounding result = UncheckedRounding.valueOf(inputMode);

        bh.consume(result);
    }

    /**
     * Benchmark testing a method call on a specific enum constant,
     * specifically checking the return of getRoundingMode().
     */
    @Benchmark
    public void testGetRoundingMode_UP(Blackhole bh) {
        RoundingMode result = UncheckedRounding.UP.getRoundingMode();
        bh.consume(result);
    }

    /**
     * Benchmark testing a method call on a specific enum constant,
     * specifically checking the return of toCheckedRounding().
     */
    @Benchmark
    public void testToCheckedRounding_HALF_EVEN(Blackhole bh) {
        org.decimal4j.truncate.CheckedRounding result = UncheckedRounding.HALF_EVEN.toCheckedRounding();
        bh.consume(result);
    }
}
