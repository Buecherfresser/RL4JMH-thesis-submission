package bench.generated.c058;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.tokens.FlowSequenceStartToken;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class FlowSequenceStartTokenBenchmark {

    private Mark startMark;
    private Mark endMark;
    private FlowSequenceStartToken token;

    @Setup(Level.Trial)
    public void setup() {
        char[] buffer = "[]".toCharArray();
        startMark = new Mark("test", 0, 0, 0, buffer, 0);
        endMark = new Mark("test", 2, 0, 2, buffer, 2);
        token = new FlowSequenceStartToken(startMark, endMark);
    }

    @Benchmark
    public void constructToken(Blackhole bh) {
        FlowSequenceStartToken newToken = new FlowSequenceStartToken(startMark, endMark);
        bh.consume(newToken);
    }

    @Benchmark
    public void getTokenId(Blackhole bh) {
        bh.consume(token.getTokenId());
    }
}
