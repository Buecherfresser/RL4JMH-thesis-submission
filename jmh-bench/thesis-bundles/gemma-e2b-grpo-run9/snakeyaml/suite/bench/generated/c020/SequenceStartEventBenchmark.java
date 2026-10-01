package bench.generated.c020;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.events.Event;
import org.yaml.snakeyaml.events.Event.ID;
import org.yaml.snakeyaml.events.SequenceStartEvent;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class SequenceStartEventBenchmark {

    /**
     * Benchmark method to test the instantiation of SequenceStartEvent.
     * This tests the overhead of creating the object and calling the overridden method.
     *
     * @param bh Blackhole to consume results.
     */
    @Benchmark
    public void benchmarkInstantiation(Blackhole bh) {
        try {
            // Instantiate the event. The constructor requires 6 arguments.
            // We pass null for the complex/optional arguments to satisfy the signature.
            SequenceStartEvent event = new SequenceStartEvent(
                null, null, false, null, null, null
            );
            
            // Call the overridden method to ensure it executes
            event.getEventId();
            
            bh.consume(event);
        } catch (Exception e) {
            // Catch potential exceptions during instantiation
        }
    }
}
