package bench.generated.c063;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.tokens.TagToken;
import org.yaml.snakeyaml.tokens.TagTuple;
import org.yaml.snakeyaml.tokens.Token;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class TagTokenBenchmark {

    private Mark startMark;
    private Mark endMark;
    private TagTuple tagTuple;
    private TagToken token;

    @Setup(Level.Trial)
    public void setup() {
        startMark = new Mark("test", 0, 0, 0, new char[]{'a'}, 0);
        endMark = new Mark("test", 1, 0, 1, new char[]{'a'}, 1);
        tagTuple = new TagTuple("!", "tag");
        token = new TagToken(tagTuple, startMark, endMark);
    }

    @Benchmark
    public TagToken constructTagToken() {
        return new TagToken(tagTuple, startMark, endMark);
    }

    @Benchmark
    public TagTuple getValue() {
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
