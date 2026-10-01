package bench.generated.c108;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.math.RoundingMode;
import org.decimal4j.truncate.DecimalRounding;
import org.decimal4j.truncate.TruncatedPart;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class DecimalRoundingBenchmark {

    @State(Scope.Benchmark)
    public static class InputState {
        DecimalRounding[] roundings;
        RoundingMode[] modes;
        int[] signs;
        long[] truncatedValues;
        TruncatedPart[] parts;
        int idx;

        @Setup
        public void setup() {
            roundings = DecimalRounding.values();
            modes = RoundingMode.values();
            signs = new int[] { 1, -1 };
            truncatedValues = new long[] { 0L, 1L, -1L, 12345L, -12345L };
            parts = TruncatedPart.values();
            idx = 0;
        }

        int nextIndex(int bound) {
            int i = idx;
            idx = (idx + 1) % bound;
            return i;
        }
    }

    @Benchmark
    public DecimalRounding benchValueOf(InputState s) {
        int i = s.nextIndex(s.modes.length);
        return DecimalRounding.valueOf(s.modes[i]);
    }

    @Benchmark
    public RoundingMode benchGetRoundingMode(InputState s) {
        int i = s.nextIndex(s.roundings.length);
        return s.roundings[i].getRoundingMode();
    }

    @Benchmark
    public int benchCalculateRoundingIncrement(InputState s) {
        int i = s.nextIndex(s.roundings.length);
        DecimalRounding dr = s.roundings[i];
        int sign = s.signs[i % s.signs.length];
        long truncatedValue = s.truncatedValues[i % s.truncatedValues.length];
        TruncatedPart tp = s.parts[i % s.parts.length];
        return dr.calculateRoundingIncrement(sign, truncatedValue, tp);
    }
}
