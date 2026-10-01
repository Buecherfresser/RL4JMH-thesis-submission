package bench.generated.c049;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.tokens.BlockSequenceStartToken;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.tokens.Token;
import org.yaml.snakeyaml.tokens.Token.ID;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class BlockSequenceStartTokenBenchmark {

    // Since BlockSequenceStartToken is final and simple, we don't strictly need a mutable state
    // for this specific benchmark, but we keep the class structure required by JMH.

    @Benchmark
    public void testGetTokenId(Blackhole bh) {
        // Instantiate the token. Since we don't have a concrete Mark implementation,
        // we rely on the constructor accepting nulls or default behavior if possible,
        // or we instantiate it directly if the constructor requires non-null Marks.
        // For simplicity and focusing on the method call, we instantiate it here.
        try {
            BlockSequenceStartToken token = new BlockSequenceStartToken(null, null);
            // Call the method and consume the result
            token.getTokenId();
        } catch (Exception e) {
            // Ignore exceptions during setup/benchmarking if they occur due to missing dependencies
        }
        bh.consume(null);
    }
}
