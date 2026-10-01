package bench.generated.c047;

import org.openjdk.jmh.annotations.*;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.tokens.BlockEntryToken;
import org.yaml.snakeyaml.tokens.Token;
import org.yaml.snakeyaml.error.Mark;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class BlockEntryTokenBenchmark {

    private Mark startMark;
    private Mark endMark;
    private BlockEntryToken token;

    @Setup(Level.Trial)
    public void setup() {
        int[] buffer = new int[] { 1, 2, 3 };
        startMark = new Mark("test", 0, 0, 0, buffer, 0);
        endMark = new Mark("test", 1, 0, 1, buffer, 1);
        token = new BlockEntryToken(startMark, endMark);
    }

    @Benchmark
    public BlockEntryToken construct() {
        return new BlockEntryToken(startMark, endMark);
    }

    @Benchmark
    public Token.ID getTokenId() {
        return token.getTokenId();
    }
}
