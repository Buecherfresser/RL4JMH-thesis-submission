package bench.generated.c111;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.math.RoundingMode;
import java.util.Set;
import org.decimal4j.truncate.UncheckedRounding;
import org.decimal4j.truncate.CheckedRounding;
import org.decimal4j.truncate.OverflowMode;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class UncheckedRoundingBenchmark {

    private UncheckedRounding policy;
    private RoundingMode roundingMode;
    private Set<UncheckedRounding> values;

    @Setup(Level.Trial)
    public void setup() {
        policy = UncheckedRounding.HALF_UP;
        roundingMode = RoundingMode.HALF_UP;
        values = UncheckedRounding.VALUES;
    }

    @Benchmark
    public RoundingMode getRoundingMode() {
        return policy.getRoundingMode();
    }

    @Benchmark
    public CheckedRounding toCheckedRounding() {
        return policy.toCheckedRounding();
    }

    @Benchmark
    public OverflowMode getOverflowMode() {
        return policy.getOverflowMode();
    }

    @Benchmark
    public String toStringBenchmark() {
        return policy.toString();
    }

    @Benchmark
    public UncheckedRounding valueOfRoundingMode() {
        return UncheckedRounding.valueOf(roundingMode);
    }

    @Benchmark
    public boolean valuesContains() {
        return values.contains(policy);
    }
}
