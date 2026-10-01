package bench.generated.c062;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
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
    public void setUp() {
        char[] buffer = "benchmark".toCharArray();
        // Mark constructor: (name, index, line, column, buffer, pointer)
        startMark = new Mark("start", 0, 0, 0, buffer, 0);
        endMark = new Mark("end", 0, 0, 0, buffer, 0);
        token = new StreamStartToken(startMark, endMark);
    }

    @Benchmark
    public StreamStartToken benchmarkCreateToken() {
        return new StreamStartToken(startMark, endMark);
    }

    @Benchmark
    public Token.ID benchmarkGetTokenId() {
        return token.getTokenId();
    }
}
