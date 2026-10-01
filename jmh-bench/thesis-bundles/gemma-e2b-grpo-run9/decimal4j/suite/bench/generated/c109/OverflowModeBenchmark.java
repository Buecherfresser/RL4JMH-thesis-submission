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

    // Since OverflowMode is a static enum, no instance state is required.

    @Benchmark
    public void checkUnchecked(Blackhole bh) {
        // Call the method and consume the result to prevent dead code elimination
        bh.consume(OverflowMode.UNCHECKED.isChecked());
    }

    @Benchmark
    public void checkChecked(Blackhole bh) {
        // Call the method and consume the result
        bh.consume(OverflowMode.CHECKED.isChecked());
    }
}
