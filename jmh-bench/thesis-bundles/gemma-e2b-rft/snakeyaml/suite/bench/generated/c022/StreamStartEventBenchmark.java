package bench.generated.c022;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.events.StreamStartEvent;
import org.yaml.snakeyaml.error.Mark;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class StreamStartEventBenchmark {

    private Mark mark1;
    private Mark mark2;

    @Setup
    public void setup() {
        // Instantiate Mark objects using a constructor that matches the required signature
        // to resolve compilation errors, assuming the error message reflects the required signature.
        this.mark1 = new Mark("", 0, 0, 0, new char[0], 0);
        this.mark2 = new Mark("", 0, 0, 0, new char[0], 0);
    }

    @Benchmark
    public void benchmarkStreamStartEvent(Blackhole bh) {
        // Call the subject method exactly once per invocation.
        StreamStartEvent event = new StreamStartEvent(mark1, mark2);
        bh.consume(event);
    }
}
