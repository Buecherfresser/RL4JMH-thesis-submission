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
    public void setUp() {
        // Construct marks using the available constructor (name, index, line, column, buffer, pointer)
        this.startMark = new Mark("benchmark", 0, 0, 0, new char[0], 0);
        this.endMark = new Mark("benchmark", 0, 0, 0, new char[0], 0);
        this.token = new BlockMappingStartToken(startMark, endMark);
    }

    @Benchmark
    public BlockMappingStartToken benchmarkConstructToken() {
        // Construct a new token per invocation.
        return new BlockMappingStartToken(startMark, endMark);
    }

    @Benchmark
    public Token.ID benchmarkGetTokenId() {
        // Retrieve the token ID.
        return token.getTokenId();
    }
}
