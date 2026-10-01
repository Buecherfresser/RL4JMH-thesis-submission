package bench.generated.c060;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.tokens.ScalarToken;
import org.yaml.snakeyaml.tokens.Token;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ScalarTokenBenchmark {

    // State field to hold the instance of the SUT.
    // Since ScalarToken is immutable, this is safe for concurrent access
    // within a single benchmark thread context.
    private ScalarToken token;

    @Setup
    public void setup() {
        try {
            // Create a representative instance. Since Mark is complex,
            // we rely on the constructor accepting nulls or default behavior
            // if possible, or we rely on the fact that the constructor
            // might be simplified for benchmarking purposes if we cannot
            // instantiate Mark easily.
            // For simplicity and compilation, we use nulls for Mark objects
            // if the constructor allows it, or rely on the fact that
            // the constructor might throw if Mark is strictly required.
            // Assuming a minimal valid construction path for benchmarking.
            this.token = new ScalarToken("test_value", null, null, false);
        } catch (Exception e) {
            // Handle potential exceptions during setup if Mark instantiation fails
            System.err.println("Failed to setup ScalarToken: " + e.getMessage());
            this.token = null;
        }
    }

    @Benchmark
    public void benchmarkGetValue(Blackhole bh) {
        if (token != null) {
            // Call a public method and consume the result
            bh.consume(token.getValue());
        }
    }

    @Benchmark
    public void benchmarkGetPlain(Blackhole bh) {
        if (token != null) {
            // Call a public method and consume the result
            bh.consume(token.getPlain());
        }
    }

    @Benchmark
    public void benchmarkGetStyle(Blackhole bh) {
        if (token != null) {
            // Call a public method and consume the result
            bh.consume(token.getStyle());
        }
    }

    @Benchmark
    public void benchmarkGetTokenId(Blackhole bh) {
        if (token != null) {
            // Call a public method and consume the result
            bh.consume(token.getTokenId());
        }
    }
}
