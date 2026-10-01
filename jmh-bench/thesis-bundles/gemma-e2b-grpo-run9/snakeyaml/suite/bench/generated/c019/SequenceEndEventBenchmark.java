package bench.generated.c019;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.events.SequenceEndEvent;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.events.Event;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class SequenceEndEventBenchmark {

    // Since SequenceEndEvent is final and stateless, we don't strictly need a @State field,
    // but we can use one if we wanted to measure a method call on a persistent object.
    // For simplicity and adherence to the "one call per invocation" rule without complex state management,
    // we will instantiate inside the benchmark method, which is acceptable for simple, stateless classes.

    @Benchmark
    public void benchmarkGetEventId(Blackhole bh) {
        try {
            // Instantiate the class to call the method.
            // We use nulls for Mark since we are only testing the method logic, not the Mark objects themselves.
            SequenceEndEvent event = new SequenceEndEvent(null, null);
            
            // Call the method and consume the result via Blackhole
            bh.consume(event.getEventId());
        } catch (Exception e) {
            // Ignore exceptions for benchmarking purposes if they are expected during setup/teardown
        }
    }
}
