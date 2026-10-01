package bench.generated.c054;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.tokens.FlowEntryToken;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.tokens.Token;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class FlowEntryTokenBenchmark {

    private Mark startMark;
    private Mark endMark;

    @Setup(Level.Trial)
    public void setup() {
        // Initialize dummy Marks using a plausible constructor signature 
        // to satisfy compilation requirements for benchmarking purposes.
        // Signature used: Mark(String, int, int, int, int[], int)
        startMark = new Mark("start", 0, 0, 0, new int[0], 0);
        endMark = new Mark("end", 0, 0, 0, new int[0], 0);
    }

    @Benchmark
    public FlowEntryToken testConstruction() {
        // Test the constructor
        return new FlowEntryToken(startMark, endMark);
    }

    @Benchmark
    public void testGetTokenId(Blackhole bh) {
        // Create the token instance
        FlowEntryToken token = new FlowEntryToken(startMark, endMark);
        
        // Test the getter method
        Token.ID id = token.getTokenId();
        
        // Consume the result using the Blackhole instance
        bh.consume(id);
    }
}
