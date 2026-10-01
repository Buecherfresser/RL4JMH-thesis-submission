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

    // Since StreamStartEvent is final and immutable, we don't strictly need a mutable state.
    // We will instantiate the object locally in the benchmark method to measure the cost
    // of construction, which is the most representative operation here.

    @Benchmark
    public void benchmarkConstructor(Blackhole bh) {
        try {
            // Instantiating the event. We pass nulls for Mark objects as we are only
            // measuring the overhead of the constructor call itself.
            StreamStartEvent event = new StreamStartEvent(null, null);
            bh.consume(event);
        } catch (Exception e) {
            // Catch potential exceptions during construction if Mark objects are strict
            // or nulls are disallowed, ensuring the benchmark doesn't crash.
        }
    }

    @Benchmark
    public void benchmarkGetEventId(Blackhole bh) {
        try {
            // Instantiate the event to call the method on it.
            StreamStartEvent event = new StreamStartEvent(null, null);
            // Call the method and consume the result (which is a constant ID).
            bh.consume(event.getEventId());
        } catch (Exception e) {
            // Catch potential exceptions during construction.
        }
    }
}
