package bench.generated.c056;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.tokens.FlowMappingStartToken;
import org.yaml.snakeyaml.tokens.Token;
import org.yaml.snakeyaml.error.Mark;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class FlowMappingStartTokenBenchmark {

    /**
     * Benchmark for instantiating FlowMappingStartToken.
     * This tests the cost of object creation and initialization.
     */
    @Benchmark
    public void benchmarkInstantiation(Blackhole bh) {
        // Instantiating the token.
        FlowMappingStartToken token = new FlowMappingStartToken(null, null);
        // Consume the result to prevent dead code elimination
        bh.consume(token);
    }

    /**
     * Benchmark for calling the overridden getTokenId method.
     * This tests the cost of method invocation.
     */
    @Benchmark
    public void benchmarkGetTokenId(Blackhole bh) {
        // Instantiate the token to call the method on an instance.
        FlowMappingStartToken token = new FlowMappingStartToken(null, null);
        
        // Call the method and consume the result (which is a Token.ID)
        bh.consume(token.getTokenId());
    }
}
