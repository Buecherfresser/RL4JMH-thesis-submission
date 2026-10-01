package bench.generated.c047;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.tokens.BlockEntryToken;
import org.yaml.snakeyaml.tokens.Token.ID;
import org.yaml.snakeyaml.error.Mark;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class BlockEntryTokenBenchmark {

    private Mark startMark;
    private Mark endMark;
    private BlockEntryToken token;

    @Setup(Level.Trial)
    public void setUp() {
        char[] buffer = "dummy".toCharArray();
        // Mark constructor: Mark(String name, int line, int column, int index, char[] buffer, int pointer)
        startMark = new Mark("test", 0, 0, 0, buffer, 0);
        endMark = new Mark("test", 0, 0, 0, buffer, 0);
        token = new BlockEntryToken(startMark, endMark);
    }

    @Benchmark
    public BlockEntryToken benchmarkCreateToken() {
        return new BlockEntryToken(startMark, endMark);
    }

    @Benchmark
    public ID benchmarkGetTokenId() {
        return token.getTokenId();
    }

    @Benchmark
    public void benchmarkGetTokenIdConsume(Blackhole bh) {
        bh.consume(token.getTokenId());
    }
}
