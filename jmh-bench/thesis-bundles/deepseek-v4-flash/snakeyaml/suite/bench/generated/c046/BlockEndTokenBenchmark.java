package bench.generated.c046;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.tokens.BlockEndToken;
import org.yaml.snakeyaml.tokens.Token;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class BlockEndTokenBenchmark {

    private Mark startMark;
    private Mark endMark;
    private BlockEndToken token;

    @Setup
    public void setup() {
        startMark = new Mark("test", 0, 0, 0, new char[0], 0);
        endMark = new Mark("test", 1, 0, 1, new char[0], 0);
        token = new BlockEndToken(startMark, endMark);
    }

    @Benchmark
    public BlockEndToken createBlockEndToken() {
        return new BlockEndToken(startMark, endMark);
    }

    @Benchmark
    public Token.ID getTokenId() {
        return token.getTokenId();
    }
}
