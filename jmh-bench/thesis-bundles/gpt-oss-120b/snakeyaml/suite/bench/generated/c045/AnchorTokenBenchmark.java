package bench.generated.c045;

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
import org.yaml.snakeyaml.tokens.AnchorToken;
import org.yaml.snakeyaml.tokens.Token;
import org.yaml.snakeyaml.error.Mark;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class AnchorTokenBenchmark {

    private AnchorToken token;
    private String anchorValue;
    private Mark startMark;
    private Mark endMark;

    @Setup
    public void setUp() {
        anchorValue = "myAnchor";
        startMark = new Mark("benchmark", 0, 0, 0, new char[0], 0);
        endMark = startMark;
        token = new AnchorToken(anchorValue, startMark, endMark);
    }

    @Benchmark
    public AnchorToken benchmarkConstructor() {
        return new AnchorToken(anchorValue, startMark, endMark);
    }

    @Benchmark
    public String benchmarkGetValue() {
        return token.getValue();
    }

    @Benchmark
    public Token.ID benchmarkGetTokenId() {
        return token.getTokenId();
    }
}
