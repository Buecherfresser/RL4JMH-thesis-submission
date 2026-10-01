package bench.generated.c048;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.tokens.BlockMappingStartToken;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.tokens.Token;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class BlockMappingStartTokenBenchmark {

    private BlockMappingStartToken tokenInstance;

    @Setup
    public void setup() {
        // Mark requires specific arguments. We use dummy values for setup.
        // Constructor signature used: Mark(String, int, int, int, char[], int)
        Mark startMark = new Mark("start", 0, 0, 0, new char[0], 0);
        Mark endMark = new Mark("end", 0, 0, 0, new char[0], 0);

        // Construct the Subject Under Test
        tokenInstance = new BlockMappingStartToken(startMark, endMark);
    }

    @Benchmark
    public void testGetTokenId(Blackhole bh) {
        // Call the subject method exactly once
        Token.ID id = tokenInstance.getTokenId();
        // Consume the result to prevent dead code elimination
        bh.consume(id);
    }
}
