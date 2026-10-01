package bench.generated.c053;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.tokens.DocumentStartToken;
import org.yaml.snakeyaml.tokens.Token;
import org.yaml.snakeyaml.error.Mark;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class DocumentStartTokenBenchmark {

    private DocumentStartToken documentStartToken;

    @Setup(Level.Trial)
    public void setup() {
        // Initialize Mark objects required for DocumentStartToken construction
        Mark startMark = new Mark("doc", 0, 0, 0, new char[0], 0);
        Mark endMark = new Mark("doc", 1, 1, 0, new char[0], 0);

        // Initialize the Subject Under Test
        documentStartToken = new DocumentStartToken(startMark, endMark);
    }

    @Benchmark
    public void testGetTokenId(Blackhole bh) {
        // Call the subject method exactly once
        Token.ID result = documentStartToken.getTokenId();
        // Consume the result to prevent dead code elimination
        bh.consume(result);
    }
}
