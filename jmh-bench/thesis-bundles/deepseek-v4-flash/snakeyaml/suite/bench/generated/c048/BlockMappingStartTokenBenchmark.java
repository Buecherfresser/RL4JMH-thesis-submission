package bench.generated.c048;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.tokens.BlockMappingStartToken;
import org.yaml.snakeyaml.tokens.Token;
import org.yaml.snakeyaml.error.Mark;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class BlockMappingStartTokenBenchmark {

    private Mark startMark;
    private Mark endMark;
    private BlockMappingStartToken token;

    @Setup(Level.Trial)
    public void setup() {
        int[] buffer = new int[]{0};
        startMark = new Mark("test", 0, 0, 0, buffer, 0);
        endMark = new Mark("test", 1, 0, 1, buffer, 1);
        token = new BlockMappingStartToken(startMark, endMark);
    }

    @Benchmark
    public BlockMappingStartToken constructToken() {
        return new BlockMappingStartToken(startMark, endMark);
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
