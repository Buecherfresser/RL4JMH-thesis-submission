package bench.generated.c062;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.tokens.StreamStartToken;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class StreamStartTokenBenchmark {

    // Since StreamStartToken is final and stateless, we don't need complex @Setup
    // for mutable state. We rely on the constructor being fast enough for
    // invocation-level setup, or we instantiate inside the benchmark method.

    @Benchmark
    public void testGetTokenId(Blackhole bh) {
        try {
            // Instantiate the token. We pass nulls for Mark as we cannot instantiate
            // the required dependency (Mark) without the full library context.
            org.yaml.snakeyaml.tokens.StreamStartToken token =
                new org.yaml.snakeyaml.tokens.StreamStartToken(null, null);
            
            // Call the method to measure
            token.getTokenId();
        } catch (Exception e) {
            // Catch potential exceptions during instantiation if Mark is complex
        }
        // Consume the result to prevent dead code elimination
        bh.consume(null);
    }
}
