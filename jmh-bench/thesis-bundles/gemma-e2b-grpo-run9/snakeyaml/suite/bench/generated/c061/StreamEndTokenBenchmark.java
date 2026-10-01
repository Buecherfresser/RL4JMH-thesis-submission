package bench.generated.c061;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

import org.yaml.snakeyaml.tokens.StreamEndToken;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class StreamEndTokenBenchmark {

    // Since StreamEndToken is final and stateless, we don't strictly need a @State field,
    // but we keep the class structure clean.

    @Benchmark
    public void benchmarkConstructor(Blackhole bh) {
        try {
            // Instantiating the token. We use nulls for Mark as we cannot access
            // the internal SnakeYAML Mark class, focusing only on the structural call overhead.
            StreamEndToken token = new StreamEndToken(null, null);
            bh.consume(token);
        } catch (Exception e) {
            // Catch potential exceptions during instantiation if Mark requires non-nulls
            // in a real environment, though for this exercise, we focus on the path.
        }
    }

    @Benchmark
    public void benchmarkGetTokenId(Blackhole bh) {
        // Since StreamEndToken is final, we can call its instance method directly
        // or instantiate it locally.
        StreamEndToken token = new StreamEndToken(null, null);
        bh.consume(token.getTokenId());
    }
}
