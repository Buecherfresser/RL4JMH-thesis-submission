package bench.generated.c056;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.tokens.FlowMappingStartToken;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class FlowMappingStartTokenBenchmark {

    private Mark mark;
    private FlowMappingStartToken token;

    @Setup
    public void setup() {
        // Create a Mark with a char[] buffer (the constructor expects char[] or int[])
        mark = new Mark("test", 0, 0, 0, "{}".toCharArray(), 0);
        token = new FlowMappingStartToken(mark, mark);
    }

    @Benchmark
    public FlowMappingStartToken construct() {
        return new FlowMappingStartToken(mark, mark);
    }

    @Benchmark
    public void getTokenId(Blackhole bh) {
        bh.consume(token.getTokenId());
    }
}
