package bench.generated.c107;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.decimal4j.truncate.CheckedRounding;
import org.decimal4j.truncate.OverflowMode;
import org.decimal4j.truncate.UncheckedRounding;

import java.math.RoundingMode;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class CheckedRoundingBenchmark {

    private RoundingMode[] roundingModes;
    private CheckedRounding[] checkedRoundings;

    private int getRoundingModeIndex;
    private int toUncheckedRoundingIndex;
    private int getOverflowModeIndex;
    private int toStringIndex;
    private int valueOfIndex;
    private int valuesContainsIndex;

    @Setup(Level.Trial)
    public void setUp() {
        roundingModes = RoundingMode.values();
        checkedRoundings = CheckedRounding.values();
    }

    private int nextIndex(int index, int length) {
        return index + 1 < length ? index + 1 : 0;
    }

    @Benchmark
    public RoundingMode getRoundingMode() {
        int idx = getRoundingModeIndex;
        getRoundingModeIndex = nextIndex(idx, checkedRoundings.length);
        return checkedRoundings[idx].getRoundingMode();
    }

    @Benchmark
    public UncheckedRounding toUncheckedRounding() {
        int idx = toUncheckedRoundingIndex;
        toUncheckedRoundingIndex = nextIndex(idx, checkedRoundings.length);
        return checkedRoundings[idx].toUncheckedRounding();
    }

    @Benchmark
    public OverflowMode getOverflowMode() {
        int idx = getOverflowModeIndex;
        getOverflowModeIndex = nextIndex(idx, checkedRoundings.length);
        return checkedRoundings[idx].getOverflowMode();
    }

    @Benchmark
    public String stringRepresentation() {
        int idx = toStringIndex;
        toStringIndex = nextIndex(idx, checkedRoundings.length);
        return checkedRoundings[idx].toString();
    }

    @Benchmark
    public CheckedRounding valueOfRoundingMode() {
        int idx = valueOfIndex;
        valueOfIndex = nextIndex(idx, roundingModes.length);
        return CheckedRounding.valueOf(roundingModes[idx]);
    }

    @Benchmark
    public boolean valuesContains() {
        int idx = valuesContainsIndex;
        valuesContainsIndex = nextIndex(idx, checkedRoundings.length);
        return CheckedRounding.VALUES.contains(checkedRoundings[idx]);
    }
}
