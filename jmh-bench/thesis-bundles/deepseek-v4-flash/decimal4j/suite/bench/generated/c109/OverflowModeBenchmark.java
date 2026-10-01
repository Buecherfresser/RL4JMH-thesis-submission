package bench.generated.c109;

import org.openjdk.jmh.annotations.*;
import org.decimal4j.truncate.OverflowMode;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class OverflowModeBenchmark {

    @State(Scope.Benchmark)
    public static class BenchmarkState {
        OverflowMode unchecked = OverflowMode.UNCHECKED;
        OverflowMode checked = OverflowMode.CHECKED;
        String checkedName = "CHECKED";
    }

    @Benchmark
    public boolean isCheckedUnchecked(BenchmarkState state) {
        return state.unchecked.isChecked();
    }

    @Benchmark
    public boolean isCheckedChecked(BenchmarkState state) {
        return state.checked.isChecked();
    }

    @Benchmark
    public OverflowMode[] values() {
        return OverflowMode.values();
    }

    @Benchmark
    public OverflowMode valueOf(BenchmarkState state) {
        return OverflowMode.valueOf(state.checkedName);
    }
}
