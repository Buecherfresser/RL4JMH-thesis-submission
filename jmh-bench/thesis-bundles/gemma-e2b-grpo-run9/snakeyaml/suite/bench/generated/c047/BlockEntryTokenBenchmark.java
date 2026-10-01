package bench.generated.c047;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.tokens.BlockEntryToken;
import org.yaml.snakeyaml.error.Mark;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class BlockEntryTokenBenchmark {

    // Since BlockEntryToken is final and stateless, we don't need complex state,
    // but we can hold an instance if we were testing mutable behavior.
    // For this simple class, we rely on local instantiation or Blackhole consumption.

    @Benchmark
    public void testTokenId(Blackhole bh) {
        // Instantiate the token. This simulates the work being measured.
        BlockEntryToken token = new BlockEntryToken(null, null);
        
        // Call the method and consume the result to prevent dead code elimination.
        bh.consume(token.getTokenId());
    }
}
