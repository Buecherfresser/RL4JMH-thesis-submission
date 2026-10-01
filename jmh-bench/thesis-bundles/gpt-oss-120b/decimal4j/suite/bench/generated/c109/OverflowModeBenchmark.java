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

    private OverflowMode unchecked;
    private OverflowMode checked;

    @Setup(Level.Trial)
    public void setUp() {
        unchecked = OverflowMode.UNCHECKED;
        checked = OverflowMode.CHECKED;
    }

    @Benchmark
    public boolean uncheckedIsChecked() {
        return unchecked.isChecked();
    }

    @Benchmark
    public boolean checkedIsChecked() {
        return checked.isChecked();
    }
}
