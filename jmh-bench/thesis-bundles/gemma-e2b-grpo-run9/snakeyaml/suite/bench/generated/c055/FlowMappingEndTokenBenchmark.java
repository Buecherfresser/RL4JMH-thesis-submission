package bench.generated.c055;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.yaml.snakeyaml.tokens.FlowMappingEndToken;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class FlowMappingEndTokenBenchmark {

    // State field. Initialized to null as we cannot guarantee instantiation success
    // without the full SnakeYAML context.
    private FlowMappingEndToken token;

    @Setup
    public void setup() {
        try {
            // Attempt to instantiate the token.
            this.token = new FlowMappingEndToken(null, null);
        } catch (Exception e) {
            // If instantiation fails, we leave token as null.
            System.err.println("Warning: Failed to initialize FlowMappingEndToken for benchmarking: " + e.getMessage());
            this.token = null;
        }
    }

    @Benchmark
    public void getTokenId(Blackhole bh) {
        if (token != null) {
            // Call the public method and consume the result as required by JMH rules.
            bh.consume(token.getTokenId());
        }
    }

    // Optional: Benchmark the constructor if we want to measure object creation cost.
    @Benchmark
    public FlowMappingEndToken createToken(Blackhole bh) {
        try {
            // Create a new instance for each invocation to measure construction time
            return new FlowMappingEndToken(null, null);
        } catch (Exception e) {
            // Return null on failure
            return null;
        }
    }
}
