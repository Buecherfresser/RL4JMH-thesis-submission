package bench.generated.c049;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.tokens.BlockSequenceStartToken;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.tokens.Token;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class BlockSequenceStartTokenBenchmark {

    private Mark startMark;
    private Mark endMark;
    private BlockSequenceStartToken tokenInstance;

    @Setup(Level.Trial)
    public void setup() {
        // Initialize required inputs (Marks). 
        // Since Mark requires arguments, we use placeholder values to satisfy the constructor signature.
        startMark = new Mark("start", 0, 0, 0, new char[0], 0);
        endMark = new Mark("end", 0, 0, 0, new char[0], 0);
        
        // Pre-create the token instance for getter benchmarks
        tokenInstance = new BlockSequenceStartToken(startMark, endMark);
    }

    @Benchmark
    public void benchmarkConstruction(Blackhole bh) {
        // Benchmark the creation of the token
        BlockSequenceStartToken token = new BlockSequenceStartToken(startMark, endMark);
        bh.consume(token);
    }

    @Benchmark
    public void benchmarkGetTokenId(Blackhole bh) {
        // Benchmark calling the getter method on a pre-existing instance
        Token.ID id = tokenInstance.getTokenId();
        bh.consume(id);
    }
}
