package bench.generated.c050;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.tokens.CommentToken;
import org.yaml.snakeyaml.comments.CommentType;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.tokens.Token.ID;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class CommentTokenBenchmark {

    private CommentToken token;
    private CommentType commentType;
    private String commentValue;
    private Mark startMark;
    private Mark endMark;

    @Setup(Level.Trial)
    public void setUp() {
        // Simple marks with empty buffer
        this.startMark = new Mark("benchmark", 0, 0, 0, new char[0], 0);
        this.endMark = new Mark("benchmark", 0, 0, 0, new char[0], 0);
        this.commentType = CommentType.IN_LINE;
        this.commentValue = "This is a benchmark comment.";
        this.token = new CommentToken(commentType, commentValue, startMark, endMark);
    }

    @Benchmark
    public CommentToken benchmarkConstructor() {
        return new CommentToken(commentType, commentValue, startMark, endMark);
    }

    @Benchmark
    public CommentType benchmarkGetCommentType() {
        return token.getCommentType();
    }

    @Benchmark
    public String benchmarkGetValue() {
        return token.getValue();
    }

    @Benchmark
    public ID benchmarkGetTokenId() {
        return token.getTokenId();
    }
}
