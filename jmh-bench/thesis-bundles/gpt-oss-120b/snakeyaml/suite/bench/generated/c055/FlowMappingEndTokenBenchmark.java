package bench.generated.c055;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.tokens.FlowMappingEndToken;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.tokens.Token.ID;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class FlowMappingEndTokenBenchmark {

    private Mark startMark;
    private Mark endMark;
    private FlowMappingEndToken prebuiltToken;

    @Setup
    public void setup() {
        char[] empty = new char[0];
        startMark = new Mark("start", 0, 0, 0, empty, 0);
        endMark = new Mark("end", 0, 0, 0, empty, 0);
        prebuiltToken = new FlowMappingEndToken(startMark, endMark);
    }

    @Benchmark
    public FlowMappingEndToken constructToken() {
        return new FlowMappingEndToken(startMark, endMark);
    }

    @Benchmark
    public ID getTokenId() {
        return prebuiltToken.getTokenId();
    }
}
