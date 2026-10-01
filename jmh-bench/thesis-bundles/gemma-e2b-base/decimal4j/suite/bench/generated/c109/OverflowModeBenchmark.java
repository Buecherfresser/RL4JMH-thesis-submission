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

    // Since OverflowMode is an enum, we don't need complex state setup,
    // but we can define the constants here if needed, though using them directly
    // in the benchmark is cleaner for simple enum tests.

    @Benchmark
    public void checkUnchecked(Blackhole bh) {
        boolean result = OverflowMode.UNCHECKED.isChecked();
        bh.consume(result);
    }

    @Benchmark
    public void checkChecked(Blackhole bh) {
        boolean result = OverflowMode.CHECKED.isChecked();
        bh.consume(result);
    }
}
