package bench.generated.c047;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.tokens.BlockEntryToken;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.tokens.Token;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class BlockEntryTokenBenchmark {

    private Mark startMark;
    private Mark endMark;

    @Setup(Level.Trial)
    public void setup() {
        // Initialize dummy Mark objects using a valid constructor based on compiler errors.
        // Using Mark(String, int, int, int, char[], int)
        char[] dummyChars = new char[0];
        startMark = new Mark("start", 0, 0, 0, dummyChars, 0);
        endMark = new Mark("end", 0, 0, 0, dummyChars, 0);
    }

    @Benchmark
    public BlockEntryToken benchmarkConstruction() {
        // Benchmark the constructor call
        BlockEntryToken token = new BlockEntryToken(startMark, endMark);
        return token;
    }

    @Benchmark
    public void benchmarkGetTokenId(Blackhole bh) {
        // Construct the token instance for the benchmark invocation
        BlockEntryToken token = new BlockEntryToken(startMark, endMark);
        
        // Benchmark the getter method
        Token.ID id = token.getTokenId();
        bh.consume(id);
    }
}
