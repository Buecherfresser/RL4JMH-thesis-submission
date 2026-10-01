package bench.generated.c111;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.truncate.UncheckedRounding;
import org.decimal4j.truncate.CheckedRounding;
import java.math.RoundingMode;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class UncheckedRoundingBenchmark {

    private UncheckedRounding[] uncheckedValues;
    private RoundingMode[] roundingModes;

    @Setup(Level.Trial)
    public void setup() {
        uncheckedValues = UncheckedRounding.values();
        roundingModes = RoundingMode.values();
    }

    // getRoundingMode benchmarks
    @Benchmark
    public RoundingMode getRoundingMode_UP() {
        return UncheckedRounding.UP.getRoundingMode();
    }

    @Benchmark
    public RoundingMode getRoundingMode_DOWN() {
        return UncheckedRounding.DOWN.getRoundingMode();
    }

    @Benchmark
    public RoundingMode getRoundingMode_CEILING() {
        return UncheckedRounding.CEILING.getRoundingMode();
    }

    @Benchmark
    public RoundingMode getRoundingMode_FLOOR() {
        return UncheckedRounding.FLOOR.getRoundingMode();
    }

    @Benchmark
    public RoundingMode getRoundingMode_HALF_UP() {
        return UncheckedRounding.HALF_UP.getRoundingMode();
    }

    @Benchmark
    public RoundingMode getRoundingMode_HALF_DOWN() {
        return UncheckedRounding.HALF_DOWN.getRoundingMode();
    }

    @Benchmark
    public RoundingMode getRoundingMode_HALF_EVEN() {
        return UncheckedRounding.HALF_EVEN.getRoundingMode();
    }

    @Benchmark
    public RoundingMode getRoundingMode_UNNECESSARY() {
        return UncheckedRounding.UNNECESSARY.getRoundingMode();
    }

    // toCheckedRounding benchmarks
    @Benchmark
    public CheckedRounding toCheckedRounding_UP() {
        return UncheckedRounding.UP.toCheckedRounding();
    }

    @Benchmark
    public CheckedRounding toCheckedRounding_DOWN() {
        return UncheckedRounding.DOWN.toCheckedRounding();
    }

    @Benchmark
    public CheckedRounding toCheckedRounding_CEILING() {
        return UncheckedRounding.CEILING.toCheckedRounding();
    }

    @Benchmark
    public CheckedRounding toCheckedRounding_FLOOR() {
        return UncheckedRounding.FLOOR.toCheckedRounding();
    }

    @Benchmark
    public CheckedRounding toCheckedRounding_HALF_UP() {
        return UncheckedRounding.HALF_UP.toCheckedRounding();
    }

    @Benchmark
    public CheckedRounding toCheckedRounding_HALF_DOWN() {
        return UncheckedRounding.HALF_DOWN.toCheckedRounding();
    }

    @Benchmark
    public CheckedRounding toCheckedRounding_HALF_EVEN() {
        return UncheckedRounding.HALF_EVEN.toCheckedRounding();
    }

    @Benchmark
    public CheckedRounding toCheckedRounding_UNNECESSARY() {
        return UncheckedRounding.UNNECESSARY.toCheckedRounding();
    }

    // toString benchmarks
    @Benchmark
    public String toString_UP() {
        return UncheckedRounding.UP.toString();
    }

    @Benchmark
    public String toString_DOWN() {
        return UncheckedRounding.DOWN.toString();
    }

    @Benchmark
    public String toString_CEILING() {
        return UncheckedRounding.CEILING.toString();
    }

    @Benchmark
    public String toString_FLOOR() {
        return UncheckedRounding.FLOOR.toString();
    }

    @Benchmark
    public String toString_HALF_UP() {
        return UncheckedRounding.HALF_UP.toString();
    }

    @Benchmark
    public String toString_HALF_DOWN() {
        return UncheckedRounding.HALF_DOWN.toString();
    }

    @Benchmark
    public String toString_HALF_EVEN() {
        return UncheckedRounding.HALF_EVEN.toString();
    }

    @Benchmark
    public String toString_UNNECESSARY() {
        return UncheckedRounding.UNNECESSARY.toString();
    }

    // static valueOf(RoundingMode) benchmarks
    @Benchmark
    public UncheckedRounding valueOf_UP() {
        return UncheckedRounding.valueOf(RoundingMode.UP);
    }

    @Benchmark
    public UncheckedRounding valueOf_DOWN() {
        return UncheckedRounding.valueOf(RoundingMode.DOWN);
    }

    @Benchmark
    public UncheckedRounding valueOf_CEILING() {
        return UncheckedRounding.valueOf(RoundingMode.CEILING);
    }

    @Benchmark
    public UncheckedRounding valueOf_FLOOR() {
        return UncheckedRounding.valueOf(RoundingMode.FLOOR);
    }

    @Benchmark
    public UncheckedRounding valueOf_HALF_UP() {
        return UncheckedRounding.valueOf(RoundingMode.HALF_UP);
    }

    @Benchmark
    public UncheckedRounding valueOf_HALF_DOWN() {
        return UncheckedRounding.valueOf(RoundingMode.HALF_DOWN);
    }

    @Benchmark
    public UncheckedRounding valueOf_HALF_EVEN() {
        return UncheckedRounding.valueOf(RoundingMode.HALF_EVEN);
    }

    @Benchmark
    public UncheckedRounding valueOf_UNNECESSARY() {
        return UncheckedRounding.valueOf(RoundingMode.UNNECESSARY);
    }

    // VALUES set containment benchmark
    @Benchmark
    public boolean valuesContains_UP() {
        return UncheckedRounding.VALUES.contains(UncheckedRounding.UP);
    }

    @Benchmark
    public boolean valuesContains_DOWN() {
        return UncheckedRounding.VALUES.contains(UncheckedRounding.DOWN);
    }

    @Benchmark
    public boolean valuesContains_CEILING() {
        return UncheckedRounding.VALUES.contains(UncheckedRounding.CEILING);
    }

    @Benchmark
    public boolean valuesContains_FLOOR() {
        return UncheckedRounding.VALUES.contains(UncheckedRounding.FLOOR);
    }

    @Benchmark
    public boolean valuesContains_HALF_UP() {
        return UncheckedRounding.VALUES.contains(UncheckedRounding.HALF_UP);
    }

    @Benchmark
    public boolean valuesContains_HALF_DOWN() {
        return UncheckedRounding.VALUES.contains(UncheckedRounding.HALF_DOWN);
    }

    @Benchmark
    public boolean valuesContains_HALF_EVEN() {
        return UncheckedRounding.VALUES.contains(UncheckedRounding.HALF_EVEN);
    }

    @Benchmark
    public boolean valuesContains_UNNECESSARY() {
        return UncheckedRounding.VALUES.contains(UncheckedRounding.UNNECESSARY);
    }
}
