package bench.generated.c011;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.emitter.EmitterException;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class EmitterExceptionBenchmark {

    // Since EmitterException is a simple exception constructor, 
    // we do not require complex state fields or setup for this benchmark.

    @Benchmark
    public void createException(Blackhole bh) {
        try {
            // Call the constructor to measure its execution time
            new EmitterException("Test message for benchmarking");
        } catch (Exception e) {
            // Catching exceptions here is safe, as we are measuring the cost 
            // of the construction attempt itself.
        }
        // Consume the result (or lack thereof) to prevent dead code elimination
        bh.consume(null);
    }
}
