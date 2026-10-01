package bench.generated.c107;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.truncate.CheckedRounding;
import org.decimal4j.truncate.UncheckedRounding;
import org.decimal4j.truncate.OverflowMode;
import java.math.RoundingMode;

@State(org.openjdk.jmh.annotations.Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class CheckedRoundingBenchmark {

    private CheckedRounding[] policies;
    private RoundingMode[] roundingModes;
    private int modeIdx;

    @Setup(org.openjdk.jmh.annotations.Level.Trial)
    public void setUp() {
        policies = CheckedRounding.values();
        roundingModes = RoundingMode.values();
        modeIdx = 0;
    }

    @Benchmark
    public RoundingMode benchGetRoundingMode() {
        // Use first enum constant as representative
        return policies[0].getRoundingMode();
    }

    @Benchmark
    public OverflowMode benchGetOverflowMode() {
        return policies[0].getOverflowMode();
    }

    @Benchmark
    public UncheckedRounding benchToUncheckedRounding() {
        return policies[0].toUncheckedRounding();
    }

    @Benchmark
    public String benchToString() {
        return policies[0].toString();
    }

    @Benchmark
    public CheckedRounding benchValueOf() {
        RoundingMode mode = roundingModes[modeIdx];
        modeIdx = (modeIdx + 1) % roundingModes.length;
        return CheckedRounding.valueOf(mode);
    }
}
