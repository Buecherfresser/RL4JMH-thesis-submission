package bench.generated.c049;

import org.openjdk.jmh.annotations.*;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.tokens.BlockSequenceStartToken;
import org.yaml.snakeyaml.tokens.Token;
import java.util.concurrent.TimeUnit;

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
    private String buffer;
    private int index;
    private int line;
    private int column;
    private int pointer;

    @Setup(Level.Trial)
    public void setup() {
        buffer = "some yaml content";
        index = 0;
        line = 0;
        column = 0;
        pointer = 0;
        startMark = new Mark("test", index, line, column, buffer.toCharArray(), pointer);
        endMark = new Mark("test", index + 10, line + 1, column + 5, buffer.toCharArray(), pointer + 10);
        token = new BlockSequenceStartToken(startMark, endMark);
    }

    @Benchmark
    public BlockSequenceStartToken constructToken() {
        Mark s = new Mark("test", index, line, column, buffer.toCharArray(), pointer);
        Mark e = new Mark("test", index + 10, line + 1, column + 5, buffer.toCharArray(), pointer + 10);
        return new BlockSequenceStartToken(s, e);
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
