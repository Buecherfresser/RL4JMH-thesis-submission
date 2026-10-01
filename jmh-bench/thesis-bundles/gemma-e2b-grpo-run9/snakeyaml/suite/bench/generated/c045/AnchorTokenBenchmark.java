package bench.generated.c045;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.tokens.AnchorToken;
import org.yaml.snakeyaml.error.Mark;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class AnchorTokenBenchmark {

    // State field to hold an instance of the token.
    // Since AnchorToken is final and immutable, we can reuse it safely.
    private AnchorToken anchorToken;

    @Setup
    public void setup() {
        // Initialize the token once per benchmark run (Trial scope).
        // We use null for Mark objects as we don't have access to the actual implementation
        // or need to mock them for this simple read operation.
        try {
            this.anchorToken = new AnchorToken("test_anchor_value", null, null);
        } catch (Exception e) {
            // Handle potential instantiation errors if Mark requires specific initialization
            System.err.println("Failed to initialize AnchorToken: " + e.getMessage());
        }
    }

    @Benchmark
    public String benchmarkGetValue(Blackhole bh) {
        // Call the public method and consume the result via Blackhole.
        // This ensures the result is used and prevents dead code elimination.
        String value = this.anchorToken.getValue();
        bh.consume(value);
        return null; // Void return is fine, but we must consume the result if we were returning it.
    }

    @Benchmark
    public void benchmarkTokenId(Blackhole bh) {
        // Call the overridden method and consume the result.
        this.anchorToken.getTokenId();
        bh.consume(null);
    }
}
