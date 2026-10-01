package bench.generated.c050;

import org.openjdk.jmh.annotations.*;
import org.yaml.snakeyaml.comments.CommentType;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.tokens.CommentToken;
import org.yaml.snakeyaml.tokens.Token;

import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class CommentTokenBenchmark {

    private CommentType type;
    private String value;
    private Mark startMark;
    private Mark endMark;
    private CommentToken token;

    @Setup(Level.Trial)
    public void setUp() {
        type = CommentType.BLOCK;
        value = "# a comment";
        char[] buffer = value.toCharArray();
        startMark = new Mark("test", 0, 0, 0, buffer, 0);
        endMark = new Mark("test", value.length(), 0, value.length(), buffer, value.length());
        token = new CommentToken(type, value, startMark, endMark);
    }

    @Benchmark
    public CommentToken construct() {
        return new CommentToken(type, value, startMark, endMark);
    }

    @Benchmark
    public CommentType getCommentType() {
        return token.getCommentType();
    }

    @Benchmark
    public String getValue() {
        return token.getValue();
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
