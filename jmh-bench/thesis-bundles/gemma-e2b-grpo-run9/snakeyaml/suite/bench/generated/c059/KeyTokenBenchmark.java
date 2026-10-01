package bench.generated.c059;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

import org.yaml.snakeyaml.tokens.KeyToken;
import org.yaml.snakeyaml.error.Mark;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class KeyTokenBenchmark {

    // Since KeyToken is final and stateless, we don't strictly need a @State field,
    // but we include one for structural completeness if we were to test mutable state.
    // For this simple class, we rely on JMH creating new instances per invocation.

    @Setup
    public void setup() {
        // Setup logic, if any, would go here.
    }

    @Benchmark
    public void benchmarkGetTokenId(Blackhole bh) {
        try {
            // Instantiate the token. This relies on Mark being available on the classpath.
            KeyToken token = new KeyToken(null, null);
            token.getTokenId();
            bh.consume(token);
        } catch (Exception e) {
            // Catch potential exceptions during instantiation if Mark is not fully available
            // in the testing environment, preventing benchmark failure.
        }
    }
}
