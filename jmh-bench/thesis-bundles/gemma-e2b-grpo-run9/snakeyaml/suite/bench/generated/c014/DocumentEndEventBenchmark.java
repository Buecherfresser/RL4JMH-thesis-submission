package bench.generated.c014;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.events.DocumentEndEvent;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class DocumentEndEventBenchmark {

    // Since DocumentEndEvent is immutable and simple, we don't need complex @State fields
    // or @Setup for input building, as instantiation is cheap and local.

    /**
     * Benchmark for instantiating DocumentEndEvent and checking its explicit flag.
     * We instantiate it locally in the benchmark method to ensure no state leakage
     * between trials, adhering to the anti-pattern avoidance rules.
     */
    @Benchmark
    public void benchmarkGetExplicit(Blackhole bh) {
        // Create a new instance for every invocation.
        DocumentEndEvent event = new DocumentEndEvent(null, null, true);
        
        // Call the method and consume the result.
        bh.consume(event.getExplicit());
    }

    /**
     * Benchmark for instantiating DocumentEndEvent and checking its explicit flag (false).
     */
    @Benchmark
    public void benchmarkGetExplicitFalse(Blackhole bh) {
        // Create a new instance for every invocation.
        DocumentEndEvent event = new DocumentEndEvent(null, null, false);
        
        // Call the method and consume the result.
        bh.consume(event.getExplicit());
    }
}
