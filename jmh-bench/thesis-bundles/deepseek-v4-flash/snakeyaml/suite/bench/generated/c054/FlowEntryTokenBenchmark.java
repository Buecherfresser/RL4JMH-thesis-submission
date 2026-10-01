package bench.generated.c054;

import org.openjdk.jmh.annotations.*;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.tokens.FlowEntryToken;
import org.yaml.snakeyaml.tokens.Token;

import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class FlowEntryTokenBenchmark {

    private Mark startMark;
    private Mark endMark;
    private FlowEntryToken token;
    private FlowEntryToken otherToken;

    @Setup(Level.Trial)
    public void setup() {
        char[] buffer = new char[] {'x'};
        startMark = new Mark("benchmark", 0, 0, 0, buffer, 0);
        endMark = new Mark("benchmark", 1, 0, 1, buffer, 0);
        token = new FlowEntryToken(startMark, endMark);
        otherToken = new FlowEntryToken(startMark, endMark);
    }

    @Benchmark
    public FlowEntryToken createToken() {
        return new FlowEntryToken(startMark, endMark);
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

    @Benchmark
    public String tokenToString() {
        return token.toString();
    }

    @Benchmark
    public int tokenHashCode() {
        return token.hashCode();
    }

    @Benchmark
    public boolean tokenEquals() {
        return token.equals(otherToken);
    }
}
