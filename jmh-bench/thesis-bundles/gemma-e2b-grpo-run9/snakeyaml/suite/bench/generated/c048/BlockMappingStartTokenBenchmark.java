package bench.generated.c048;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.tokens.BlockMappingStartToken;
import org.yaml.snakeyaml.tokens.Token;
import org.yaml.snakeyaml.error.Mark;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class BlockMappingStartTokenBenchmark {

    @Benchmark
    public void benchmarkInstantiation(Blackhole bh) {
        // Test the cost of creating a new instance.
        BlockMappingStartToken token = new BlockMappingStartToken(null, null);
        bh.consume(token);
    }

    @Benchmark
    public void benchmarkGetTokenId(Blackhole bh) {
        // Test the cost of calling the overridden method.
        BlockMappingStartToken token = new BlockMappingStartToken(null, null);
        token.getTokenId();
        bh.consume(token);
    }
}
