package bench.generated.c109;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.truncate.OverflowMode;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class OverflowModeBenchmark {

    private OverflowMode uncheckedMode;
    private OverflowMode checkedMode;

    @Setup(Level.Trial)
    public void setup() {
        // Initialize instances for benchmarking
        uncheckedMode = OverflowMode.UNCHECKED;
        checkedMode = OverflowMode.CHECKED;
    }

    @Benchmark
    public boolean benchmarkUncheckedIsChecked(Blackhole bh) {
        boolean result = uncheckedMode.isChecked();
        bh.consume(result);
        return result;
    }

    @Benchmark
    public boolean benchmarkCheckedIsChecked(Blackhole bh) {
        boolean result = checkedMode.isChecked();
        bh.consume(result);
        return result;
    }
}
