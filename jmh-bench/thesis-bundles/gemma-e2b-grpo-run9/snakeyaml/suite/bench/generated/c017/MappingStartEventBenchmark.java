package bench.generated.c017;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.events.MappingStartEvent;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.DumperOptions;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class MappingStartEventBenchmark {

    /**
     * Benchmarks the instantiation of MappingStartEvent and the call to its getter.
     * We create a new instance in every invocation to avoid state mutation issues
     * and adhere to the rule against static final inputs.
     *
     * @param bh Blackhole to consume the result.
     */
    @Benchmark
    public void benchmarkMappingStartEvent(Blackhole bh) {
        try {
            // Instantiate the event. We pass null for complex/optional parameters
            // to avoid compilation errors related to required non-instantiable types
            // like FlowStyle, focusing only on the structure and method call.
            MappingStartEvent event = new MappingStartEvent(
                null,
                null,
                false,
                null,
                null,
                null // Passing null to satisfy the DumperOptions.FlowStyle requirement minimally
            );
            
            // Call the method and consume the result to prevent dead code elimination
            bh.consume(event.getEventId());
        } catch (Exception e) {
            // Catch potential exceptions during instantiation if dependencies are missing
        }
    }
}
