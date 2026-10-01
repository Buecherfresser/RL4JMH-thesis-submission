package bench.generated.c056;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.tokens.FlowMappingStartToken;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.tokens.Token;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class FlowMappingStartTokenBenchmark {

    private Mark mark1;
    private Mark mark2;
    private FlowMappingStartToken token;

    @Setup(Level.Trial)
    public void setup() {
        // Initialize Mark objects using a valid constructor, providing dummy data.
        // Mark(String, int, int, int, char[], int)
        mark1 = new Mark("start", 0, 0, 0, new char[0], 0);
        mark2 = new Mark("end", 0, 0, 0, new char[0], 0);
        
        // Initialize the token instance for methods that use it.
        token = new FlowMappingStartToken(mark1, mark2);
    }

    @Benchmark
    public void benchmarkTokenCreation(Blackhole bh) {
        // Measure the cost of creating the token
        FlowMappingStartToken newToken = new FlowMappingStartToken(mark1, mark2);
        bh.consume(newToken);
    }

    @Benchmark
    public void benchmarkGetTokenId(Blackhole bh) {
        // Measure the cost of calling the getter
        Token.ID id = token.getTokenId();
        bh.consume(id);
    }
}
