package bench.generated.c060;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.tokens.ScalarToken;
import org.yaml.snakeyaml.tokens.Token;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.error.Mark;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ScalarTokenBenchmark {

    private String sampleValue;
    private boolean plainFlag;
    private Mark startMark;
    private Mark endMark;
    private DumperOptions.ScalarStyle style;
    private ScalarToken token;

    @Setup
    public void setUp() {
        sampleValue = "example scalar value for benchmarking";
        plainFlag = true;
        char[] buffer = sampleValue.toCharArray();
        startMark = new Mark("benchmark", 0, 0, 0, buffer, 0);
        endMark = new Mark("benchmark", 0, 0, 0, buffer, 0);
        style = DumperOptions.ScalarStyle.DOUBLE_QUOTED;
        token = new ScalarToken(sampleValue, plainFlag, startMark, endMark, style);
    }

    @Benchmark
    public ScalarToken constructFull() {
        return new ScalarToken(sampleValue, plainFlag, startMark, endMark, style);
    }

    @Benchmark
    public ScalarToken constructSimple() {
        return new ScalarToken(sampleValue, startMark, endMark, plainFlag);
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
}
