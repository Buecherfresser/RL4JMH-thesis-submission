package bench.generated.c058;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

import org.yaml.snakeyaml.tokens.FlowSequenceStartToken;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class FlowSequenceStartTokenBenchmark {

    // Since FlowSequenceStartToken is stateless and the method is trivial,
    // we do not need complex @State fields or @Setup methods.
    // We instantiate the object inside the benchmark method to measure
    // the cost of creation and method invocation per iteration.

    @Benchmark
    public void testGetTokenId(Blackhole bh) {
        // Create a new instance. This measures object allocation overhead.
        FlowSequenceStartToken token = new FlowSequenceStartToken(null, null);
        
        // Call the method and consume the result (or ignore it, as it's a void return)
        token.getTokenId();
    }
}
