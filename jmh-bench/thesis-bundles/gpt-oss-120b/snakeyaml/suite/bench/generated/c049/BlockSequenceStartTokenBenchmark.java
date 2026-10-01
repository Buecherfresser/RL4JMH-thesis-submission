package bench.generated.c049;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.tokens.BlockSequenceStartToken;
import org.yaml.snakeyaml.tokens.Token;
import org.yaml.snakeyaml.error.Mark;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class BlockSequenceStartTokenBenchmark {

    private Mark startMark;
    private Mark endMark;
    private BlockSequenceStartToken token;

    @Setup
    public void setUp() {
        char[] buffer = "dummy".toCharArray();
        startMark = new Mark("benchmark", 0, 0, 0, buffer, 0);
        endMark = new Mark("benchmark", 0, 0, 0, buffer, 0);
        token = new BlockSequenceStartToken(startMark, endMark);
    }

    @Benchmark
    public BlockSequenceStartToken constructToken() {
        return new BlockSequenceStartToken(startMark, endMark);
    }

    @Benchmark
    public Token.ID getTokenId() {
        return token.getTokenId();
    }
}
