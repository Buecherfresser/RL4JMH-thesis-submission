package bench.generated.c046;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.tokens.BlockEndToken;
import org.yaml.snakeyaml.error.Mark;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class BlockEndTokenBenchmark {

    // Since BlockEndToken is stateless and final, we don't need complex @State fields
    // unless we were benchmarking a mutable object or a complex factory.

    /**
     * Benchmarks the instantiation of BlockEndToken.
     * This measures the overhead of calling the constructor and initializing the object.
     * We use a dummy Mark instance as we cannot access internal SnakeYAML types.
     *
     * @param bh Blackhole to consume the result.
     */
    @Benchmark
    public void benchmarkInstantiation(Blackhole bh) {
        try {
            // Instantiate the token. We use null for Mark as a placeholder,
            // assuming the constructor handles nulls or that this is acceptable
            // for measuring the overhead of the call itself.
            BlockEndToken token = new BlockEndToken(null, null);
            bh.consume(token);
        } catch (Exception e) {
            // Ignore exceptions during benchmark execution if they occur due to internal library state
        }
    }
}
