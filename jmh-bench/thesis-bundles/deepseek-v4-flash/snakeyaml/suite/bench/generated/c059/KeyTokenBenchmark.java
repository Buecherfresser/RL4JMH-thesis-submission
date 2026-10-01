package bench.generated.c059;

import org.openjdk.jmh.annotations.*;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.tokens.KeyToken;
import org.yaml.snakeyaml.tokens.Token;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class KeyTokenBenchmark {

    private Mark startMark;
    private Mark endMark;
    private KeyToken token;

    @Setup(Level.Trial)
    public void setup() {
        // Create marks with dummy values; they are immutable and reused.
        startMark = new Mark("test", 0, 0, 0, new char[]{'a'}, 0);
        endMark = new Mark("test", 1, 1, 1, new char[]{'b'}, 1);
        token = new KeyToken(startMark, endMark);
    }

    @Benchmark
    public KeyToken constructKeyToken() {
        return new KeyToken(startMark, endMark);
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
