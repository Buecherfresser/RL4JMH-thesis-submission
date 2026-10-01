package bench.generated.c053;

import org.openjdk.jmh.annotations.*;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.tokens.DocumentStartToken;
import org.yaml.snakeyaml.error.Mark;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class DocumentStartTokenBenchmark {

    private Mark startMark;
    private Mark endMark;
    private DocumentStartToken token;

    @Setup(Level.Trial)
    public void setup() {
        int[] buffer = new int[0];
        startMark = new Mark("benchmark", 0, 0, 0, buffer, 0);
        endMark = new Mark("benchmark", 1, 0, 1, buffer, 1);
        token = new DocumentStartToken(startMark, endMark);
    }

    @Benchmark
    public DocumentStartToken constructor() {
        return new DocumentStartToken(startMark, endMark);
    }

    @Benchmark
    public org.yaml.snakeyaml.tokens.Token.ID getTokenId() {
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
}
