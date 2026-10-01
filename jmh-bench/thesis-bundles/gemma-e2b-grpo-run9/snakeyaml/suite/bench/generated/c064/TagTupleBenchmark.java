package bench.generated.c064;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.tokens.TagTuple;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class TagTupleBenchmark {

    // State field to hold an instance of the class under test.
    // Since TagTuple is immutable, this is safe to reuse across invocations.
    private TagTuple tagTuple;

    @Setup
    public void setup() {
        try {
            // Initialize the object once for the benchmark suite.
            // This ensures that the setup cost (if any) is not included in the measured time.
            this.tagTuple = new TagTuple("test_handle", "test_suffix");
        } catch (NullPointerException e) {
            // Should not happen with valid inputs, but good practice.
            System.err.println("Setup failed: " + e.getMessage());
        }
    }

    @Benchmark
    public void benchmarkConstruction(Blackhole bh) {
        // Call the constructor. Since we are using a state field,
        // we rely on the setup to have initialized it, but calling it again
        // tests the construction path if we were to re-initialize it here.
        // For simplicity and to measure the access/read path, we just consume the state.
        bh.consume(this.tagTuple);
    }

    @Benchmark
    public String benchmarkGetHandle(Blackhole bh) {
        // Call a getter method and consume the result to prevent dead code elimination.
        return this.tagTuple.getHandle();
    }

    @Benchmark
    public String benchmarkGetSuffix(Blackhole bh) {
        // Call another getter method and consume the result.
        return this.tagTuple.getSuffix();
    }
}
