package bench.generated.c062;

import org.openjdk.jmh.annotations.*;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.tokens.StreamStartToken;
import org.yaml.snakeyaml.tokens.Token;
import org.yaml.snakeyaml.error.Mark;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class StreamStartTokenBenchmark {

    private Mark startMark;
    private Mark endMark;
    private StreamStartToken token;

    @Setup(Level.Trial)
    public void setup() {
        int[] buffer = new int[]{0};
        startMark = new Mark("test", 0, 0, 0, buffer, 0);
        endMark = new Mark("test", 0, 0, 0, buffer, 0);
        token = new StreamStartToken(startMark, endMark);
    }

    @Benchmark
    public StreamStartToken construct() {
        return new StreamStartToken(startMark, endMark);
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
}
