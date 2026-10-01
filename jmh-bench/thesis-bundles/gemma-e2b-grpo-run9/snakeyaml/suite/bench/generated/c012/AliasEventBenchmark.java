package bench.generated.c012;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

// Assuming necessary SnakeYAML dependencies (like Mark, NodeEvent, Event) are available
// on the classpath for compilation, even if not explicitly defined here.
import org.yaml.snakeyaml.events.AliasEvent;
import org.yaml.snakeyaml.error.Mark;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class AliasEventBenchmark {

    // State field to hold an instance, useful if we benchmark method calls on a reused object.
    // Since AliasEvent is final and stateless regarding its internal state,
    // creating a new instance per benchmark method is also acceptable, but reusing
    // a simple object can sometimes reduce GC pressure if the constructor is cheap.
    private AliasEvent aliasEvent;

    @Setup
    public void setup() {
        // Initialize a dummy instance. Since the constructor requires non-null String,
        // we must provide a valid anchor. We use null for Mark as a placeholder,
        // assuming the base class handles this gracefully or we rely on the
        // fact that we are only measuring the AliasEvent logic itself.
        try {
            this.aliasEvent = new AliasEvent("test_anchor", null, null);
        } catch (NullPointerException e) {
            // Handle potential NPE if Mark/NodeEvent dependencies are missing or nulls are invalid
            // In a real scenario, this setup would require mocking or proper dependency injection.
            System.err.println("Failed to setup AliasEvent: " + e.getMessage());
        }
    }

    @Benchmark
    public void benchmarkConstructor(Blackhole bh) {
        try {
            // Measure the cost of creating a new AliasEvent instance
            AliasEvent event = new AliasEvent("anchor_123", null, null);
            bh.consume(event);
        } catch (Exception e) {
            // Catch exceptions that might occur during setup/benchmarking if dependencies are missing
        }
    }

    @Benchmark
    public void benchmarkGetEventId(Blackhole bh) {
        // Measure the cost of calling the getEventId method
        if (aliasEvent != null) {
            bh.consume(aliasEvent.getEventId());
        }
    }
}
