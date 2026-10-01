package bench.generated.c052;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.tokens.DocumentEndToken;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.tokens.Token;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class DocumentEndTokenBenchmark {

    private Mark startMark;
    private Mark endMark;
    private DocumentEndToken documentEndToken;

    @Setup(Level.Trial)
    public void setup() {
        // Create dummy Mark objects for input using a valid constructor
        // Mark(String, int, int, int, char[], int)
        startMark = new Mark("start", 0, 0, 0, new char[0], 0);
        endMark = new Mark("end", 0, 0, 0, new char[0], 0);

        // Create the subject instance
        documentEndToken = new DocumentEndToken(startMark, endMark);
    }

    /**
     * Benchmarks the retrieval of the token ID.
     */
    @Benchmark
    public void testGetTokenId(Blackhole bh) {
        Token.ID id = documentEndToken.getTokenId();
        bh.consume(id);
    }
}
