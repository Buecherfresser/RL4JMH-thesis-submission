package bench.generated.c057;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.tokens.FlowSequenceEndToken;
import org.yaml.snakeyaml.tokens.Token;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class FlowSequenceEndTokenBenchmark {

    /**
     * Benchmark for creating a new FlowSequenceEndToken instance.
     * This tests the constructor overhead.
     */
    @Benchmark
    public void createToken(Blackhole bh) {
        try {
            // Create a dummy Mark object for the constructor call
            FlowSequenceEndToken token = new FlowSequenceEndToken(null, null);
            bh.consume(token);
        } catch (Exception e) {
            // Ignore exceptions for benchmarking purposes
        }
    }

    /**
     * Benchmark for calling the getTokenId method.
     * This tests the overhead of calling the overridden method.
     */
    @Benchmark
    public void getTokenId(Blackhole bh) {
        try {
            // Create a dummy instance to call the method on
            FlowSequenceEndToken token = new FlowSequenceEndToken(null, null);
            // Accessing the nested ID via the imported Token class
            bh.consume(token.getTokenId());
        } catch (Exception e) {
            // Ignore exceptions
        }
    }
}
