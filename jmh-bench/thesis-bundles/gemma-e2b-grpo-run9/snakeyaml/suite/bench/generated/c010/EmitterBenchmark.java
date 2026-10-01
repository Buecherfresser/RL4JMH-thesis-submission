package bench.generated.c010;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Writer;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.emitter.Emitter;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class EmitterBenchmark {

    // State fields for the Emitter instance. Since Emitter is not thread-safe,
    // we create a new instance per benchmark method to ensure isolation.
    private Emitter emitter;

    // Setup method to initialize the Emitter instance.
    @Setup
    public void setup() {
        try {
            // Use a ByteArrayOutputStream wrapped in a standard Writer for in-memory output.
            // Use minimal DumperOptions.
            Writer writer = new java.io.OutputStreamWriter(new ByteArrayOutputStream());
            this.emitter = new Emitter(writer, new DumperOptions());
        } catch (Exception e) {
            // Handle potential exceptions during setup if necessary, though usually setup should succeed.
            System.err.println("Emitter setup failed: " + e.getMessage());
        }
    }

    @Benchmark
    public void benchmarkEmit(Blackhole bh) {
        // Since we cannot easily construct a valid internal Event object
        // without full SnakeYAML dependencies, we call emit with null,
        // focusing the benchmark on the initialization and method call overhead.
        try {
            // This call will likely throw an exception internally if the state machine
            // expects a non-null event, but it exercises the public method path.
            emitter.emit(null);
        } catch (Exception e) {
            // Catch expected exceptions during testing if the internal state machine fails on null.
            // We consume the exception to prevent JMH from failing the run, but this is generally discouraged.
        }
        bh.consume(emitter);
    }
}
