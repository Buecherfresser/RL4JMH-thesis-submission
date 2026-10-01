package bench.generated.c021;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.events.StreamEndEvent;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.events.Event;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class StreamEndEventBenchmark {

    // Since StreamEndEvent is final and stateless, we don't strictly need a @State field,
    // but we keep the class structure clean.

    @Setup
    public void setup() {
        // Setup phase: No complex setup needed for this simple, stateless class.
    }

    @Benchmark
    public void benchmarkConstructor(Blackhole bh) {
        try {
            // Instantiate the class. We rely on the fact that Mark and Event
            // dependencies are either mocked or available in the classpath context.
            StreamEndEvent event = new StreamEndEvent(null, null);
            bh.consume(event);
        } catch (Exception e) {
            // Catch potential exceptions during instantiation if dependencies fail
        }
    }

    @Benchmark
    public void benchmarkGetEventId(Blackhole bh) {
        try {
            // Instantiate the class
            StreamEndEvent event = new StreamEndEvent(null, null);
            // Call the overridden method and consume the result
            bh.consume(event.getEventId());
        } catch (Exception e) {
            // Catch potential exceptions during method call
        }
    }
}
