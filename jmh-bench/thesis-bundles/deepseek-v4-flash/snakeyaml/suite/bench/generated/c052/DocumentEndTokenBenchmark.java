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
    private DocumentEndToken token;
    private DocumentEndToken otherToken;

    @Setup(Level.Trial)
    public void setup() {
        int[] buffer = new int[] { '.', '.', '.' };
        startMark = new Mark("test", 0, 0, 0, buffer, 0);
        endMark = new Mark("test", 3, 0, 3, buffer, 3);
        token = new DocumentEndToken(startMark, endMark);
        otherToken = new DocumentEndToken(startMark, endMark);
    }

    @Benchmark
    public DocumentEndToken constructToken() {
        return new DocumentEndToken(startMark, endMark);
    }

    @Benchmark
    public Token.ID getTokenId() {
        return token.getTokenId();
    }

    @Benchmark
    public Mark getStartMark() {
        return token.getStartMark();
    }

    @Benchmark
    public Mark getEndMark() {
        return token.getEndMark();
    }

    @Benchmark
    public boolean equals() {
        return token.equals(otherToken);
    }

    @Benchmark
    public int hashCode() {
        return token.hashCode();
    }

    @Benchmark
    public String toString() {
        return token.toString();
    }
}
