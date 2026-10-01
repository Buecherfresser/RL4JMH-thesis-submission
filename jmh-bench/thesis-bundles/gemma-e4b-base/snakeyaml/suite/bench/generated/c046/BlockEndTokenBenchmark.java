package bench.generated.c046;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.tokens.BlockEndToken;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.tokens.Token.ID;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class BlockEndTokenBenchmark {

    private Mark startMark;
    private Mark endMark;
    private int[] dummyIntArray;

    @Setup(Level.Trial)
    public void setup() {
        // Initialize dummy data required for Mark construction
        dummyIntArray = new int[1];

        // Initialize marks using a valid constructor (e.g., the one accepting int[])
        // Mark(String name, int line, int column, int offset, int[] charArray, int length)
        startMark = new Mark("start", 1, 1, 0, dummyIntArray, 1);
        endMark = new Mark("end", 1, 1, 0, dummyIntArray, 1);
    }

    @Benchmark
    public BlockEndToken constructToken(Blackhole bh) {
        BlockEndToken token = new BlockEndToken(startMark, endMark);
        bh.consume(token);
        return token;
    }

    @Benchmark
    public void getTokenId(Blackhole bh) {
        // Re-construct the token for each invocation to ensure fresh state if necessary,
        // although since Mark objects are immutable fixtures, this is fine.
        BlockEndToken token = new BlockEndToken(startMark, endMark);
        ID id = token.getTokenId();
        bh.consume(id);
    }
}
