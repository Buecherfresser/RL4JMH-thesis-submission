package bench.generated.c108;

import org.openjdk.jmh.annotations.*;
import org.decimal4j.truncate.DecimalRounding;
import org.decimal4j.truncate.TruncatedPart;
import java.math.RoundingMode;
import java.util.concurrent.TimeUnit;

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class DecimalRoundingBenchmark {

    @State(Scope.Thread)
    public static class BenchState {
        public TruncatedPart[] truncatedParts = TruncatedPart.values();
        public int[] signs = {1, -1};
        public long[] truncatedValues = {1234567890L, 1234567891L}; // even and odd
        public DecimalRounding[] roundings = DecimalRounding.values();
        public RoundingMode[] roundingModes = RoundingMode.values();
        public TruncatedPart zeroPart;
        public int calculateIndex = 0;
        public int roundingIndex = 0;
        public int roundingModeIndex = 0;

        @Setup(Level.Trial)
        public void setup() {
            zeroPart = TruncatedPart.ZERO;
        }

        public int nextCalculateIndex() {
            int i = calculateIndex;
            calculateIndex = (calculateIndex + 1) % (truncatedParts.length * signs.length * truncatedValues.length);
            return i;
        }

        public int nextRoundingIndex() {
            int i = roundingIndex;
            roundingIndex = (roundingIndex + 1) % roundings.length;
            return i;
        }

        public int nextRoundingModeIndex() {
            int i = roundingModeIndex;
            roundingModeIndex = (roundingModeIndex + 1) % roundingModes.length;
            return i;
        }
    }

    // ---------- calculateRoundingIncrement for each rounding mode ----------

    @Benchmark
    public int calculateIncrement_UP(BenchState state) {
        int idx = state.nextCalculateIndex();
        TruncatedPart part = state.truncatedParts[idx % state.truncatedParts.length];
        int sign = state.signs[(idx / state.truncatedParts.length) % state.signs.length];
        long value = state.truncatedValues[(idx / (state.truncatedParts.length * state.signs.length)) % state.truncatedValues.length];
        return DecimalRounding.UP.calculateRoundingIncrement(sign, value, part);
    }

    @Benchmark
    public int calculateIncrement_DOWN(BenchState state) {
        int idx = state.nextCalculateIndex();
        TruncatedPart part = state.truncatedParts[idx % state.truncatedParts.length];
        int sign = state.signs[(idx / state.truncatedParts.length) % state.signs.length];
        long value = state.truncatedValues[(idx / (state.truncatedParts.length * state.signs.length)) % state.truncatedValues.length];
        return DecimalRounding.DOWN.calculateRoundingIncrement(sign, value, part);
    }

    @Benchmark
    public int calculateIncrement_CEILING(BenchState state) {
        int idx = state.nextCalculateIndex();
        TruncatedPart part = state.truncatedParts[idx % state.truncatedParts.length];
        int sign = state.signs[(idx / state.truncatedParts.length) % state.signs.length];
        long value = state.truncatedValues[(idx / (state.truncatedParts.length * state.signs.length)) % state.truncatedValues.length];
        return DecimalRounding.CEILING.calculateRoundingIncrement(sign, value, part);
    }

    @Benchmark
    public int calculateIncrement_FLOOR(BenchState state) {
        int idx = state.nextCalculateIndex();
        TruncatedPart part = state.truncatedParts[idx % state.truncatedParts.length];
        int sign = state.signs[(idx / state.truncatedParts.length) % state.signs.length];
        long value = state.truncatedValues[(idx / (state.truncatedParts.length * state.signs.length)) % state.truncatedValues.length];
        return DecimalRounding.FLOOR.calculateRoundingIncrement(sign, value, part);
    }

    @Benchmark
    public int calculateIncrement_HALF_UP(BenchState state) {
        int idx = state.nextCalculateIndex();
        TruncatedPart part = state.truncatedParts[idx % state.truncatedParts.length];
        int sign = state.signs[(idx / state.truncatedParts.length) % state.signs.length];
        long value = state.truncatedValues[(idx / (state.truncatedParts.length * state.signs.length)) % state.truncatedValues.length];
        return DecimalRounding.HALF_UP.calculateRoundingIncrement(sign, value, part);
    }

    @Benchmark
    public int calculateIncrement_HALF_DOWN(BenchState state) {
        int idx = state.nextCalculateIndex();
        TruncatedPart part = state.truncatedParts[idx % state.truncatedParts.length];
        int sign = state.signs[(idx / state.truncatedParts.length) % state.signs.length];
        long value = state.truncatedValues[(idx / (state.truncatedParts.length * state.signs.length)) % state.truncatedValues.length];
        return DecimalRounding.HALF_DOWN.calculateRoundingIncrement(sign, value, part);
    }

    @Benchmark
    public int calculateIncrement_HALF_EVEN(BenchState state) {
        int idx = state.nextCalculateIndex();
        TruncatedPart part = state.truncatedParts[idx % state.truncatedParts.length];
        int sign = state.signs[(idx / state.truncatedParts.length) % state.signs.length];
        long value = state.truncatedValues[(idx / (state.truncatedParts.length * state.signs.length)) % state.truncatedValues.length];
        return DecimalRounding.HALF_EVEN.calculateRoundingIncrement(sign, value, part);
    }

    @Benchmark
    public int calculateIncrement_UNNECESSARY(BenchState state) {
        // Only test with ZERO part to avoid exceptions; use a fixed sign and value from state.
        return DecimalRounding.UNNECESSARY.calculateRoundingIncrement(1, state.truncatedValues[0], state.zeroPart);
    }

    // ---------- getRoundingMode ----------

    @Benchmark
    public RoundingMode getRoundingMode(BenchState state) {
        DecimalRounding dr = state.roundings[state.nextRoundingIndex()];
        return dr.getRoundingMode();
    }

    // ---------- valueOf(RoundingMode) ----------

    @Benchmark
    public DecimalRounding valueOf(BenchState state) {
        RoundingMode rm = state.roundingModes[state.nextRoundingModeIndex()];
        return DecimalRounding.valueOf(rm);
    }
}
