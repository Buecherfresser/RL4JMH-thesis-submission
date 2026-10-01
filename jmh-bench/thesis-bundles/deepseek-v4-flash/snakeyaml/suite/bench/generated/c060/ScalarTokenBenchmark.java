package bench.generated.c060;

import org.openjdk.jmh.annotations.*;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.tokens.ScalarToken;
import org.yaml.snakeyaml.tokens.Token;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.DumperOptions;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ScalarTokenBenchmark {

    private Mark startMark;
    private Mark endMark;
    private String value;
    private boolean plain;
    private DumperOptions.ScalarStyle style;
    private ScalarToken token;

    @Setup(Level.Trial)
    public void setup() {
        startMark = new Mark("test", 0, 0, 0, new int[0], 0);
        endMark = new Mark("test", 5, 0, 5, new int[0], 5);
        value = "hello world";
        plain = true;
        style = DumperOptions.ScalarStyle.PLAIN;
        token = new ScalarToken(value, plain, startMark, endMark, style);
    }

    @Benchmark
    public ScalarToken constructor4Args() {
        return new ScalarToken(value, startMark, endMark, plain);
    }

    @Benchmark
    public ScalarToken constructor5Args() {
        return new ScalarToken(value, plain, startMark, endMark, style);
    }

    @Benchmark
    public boolean getPlain() {
        return token.getPlain();
    }

    @Benchmark
    public String getValue() {
        return token.getValue();
    }

    @Benchmark
    public DumperOptions.ScalarStyle getStyle() {
        return token.getStyle();
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
