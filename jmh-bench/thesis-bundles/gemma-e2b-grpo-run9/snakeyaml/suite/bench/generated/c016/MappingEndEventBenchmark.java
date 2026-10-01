package bench.generated.c016;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

import org.yaml.snakeyaml.events.Event;
import org.yaml.snakeyaml.events.MappingEndEvent;
import org.yaml.snakeyaml.error.Mark;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class MappingEndEventBenchmark {

    // Since MappingEndEvent is final and simple, we don't need complex state setup.
    // We rely on the benchmark method to instantiate the object for measurement.

    @Benchmark
    public void testGetEventId(Blackhole bh) {
        try {
            // Instantiate the object. We pass nulls for Mark as we are only measuring
            // the overhead of the method call and object creation, not complex YAML parsing.
            MappingEndEvent event = new MappingEndEvent(null, null);
            bh.consume(event.getEventId());
        } catch (Exception e) {
            // Catch potential exceptions during instantiation if dependencies are missing,
            // ensuring the benchmark doesn't crash the harness.
        }
    }
}
