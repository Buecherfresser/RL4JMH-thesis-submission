package bench.generated.c052;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.tokens.DocumentEndToken;
import org.yaml.snakeyaml.tokens.Token;
import org.yaml.snakeyaml.error.Mark;

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

    @Setup(Level.Trial)
    public void setUp() {
        char[] buffer = new char[0];
        startMark = new Mark("start", 0, 0, 0, buffer, 0);
        endMark = new Mark("end", 0, 0, 0, buffer, 0);
        token = new DocumentEndToken(startMark, endMark);
    }

    @Benchmark
    public DocumentEndToken benchmarkCreateToken() {
        return new DocumentEndToken(startMark, endMark);
    }

    @Benchmark
    public Token.ID benchmarkGetTokenId() {
        return token.getTokenId();
    }

    @Benchmark
    public String benchmarkToString() {
        return token.toString();
    }

    @Benchmark
    public int benchmarkHashCode() {
        return token.hashCode();
    }
}
