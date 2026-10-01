package bench.generated.c045;

import org.openjdk.jmh.annotations.*;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.tokens.AnchorToken;
import org.yaml.snakeyaml.tokens.Token;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class AnchorTokenBenchmark {

    private Mark startMark;
    private Mark endMark;
    private String anchorValue;
    private AnchorToken token;

    @Setup(Level.Trial)
    public void setUp() {
        anchorValue = "anchor1";
        char[] buffer = anchorValue.toCharArray();
        startMark = new Mark("anchor", 0, 0, 0, buffer, 0);
        endMark = new Mark("anchor", anchorValue.length(), 0, anchorValue.length(), buffer, anchorValue.length());
        token = new AnchorToken(anchorValue, startMark, endMark);
    }

    @Benchmark
    public AnchorToken constructToken() {
        return new AnchorToken(anchorValue, startMark, endMark);
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
