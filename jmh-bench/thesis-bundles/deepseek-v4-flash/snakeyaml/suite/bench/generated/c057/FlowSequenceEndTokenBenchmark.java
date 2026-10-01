package bench.generated.c057;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.tokens.FlowSequenceEndToken;
import org.yaml.snakeyaml.tokens.Token;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class FlowSequenceEndTokenBenchmark {

    private Mark startMark;
    private Mark endMark;
    private FlowSequenceEndToken token;

    @Setup(Level.Trial)
    public void setup() {
        int[] buffer = new int[] { 1, 2, 3, 4, 5 };
        startMark = new Mark("test", 0, 0, 0, buffer, 0);
        endMark = new Mark("test", 5, 0, 5, buffer, 5);
        token = new FlowSequenceEndToken(startMark, endMark);
    }

    @Benchmark
    public FlowSequenceEndToken constructToken() {
        return new FlowSequenceEndToken(startMark, endMark);
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
