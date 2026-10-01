package bench.generated.c062;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.tokens.StreamStartToken;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.tokens.Token.ID;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class StreamStartTokenBenchmark {

    private Mark startMark;
    private Mark endMark;
    private StreamStartToken tokenInstance;

    @Setup(Level.Trial)
    public void setup() {
        // Initialize required input objects (Marks).
        // We must use a valid constructor for Mark, based on compilation errors.
        // Using dummy values to satisfy the compiler.
        startMark = new Mark("dummy", 0, 0, 0, new char[0], 0);
        endMark = new Mark("dummy", 0, 0, 0, new char[0], 0);

        // Pre-create the token instance for method call benchmarks
        tokenInstance = new StreamStartToken(startMark, endMark);
    }

    @Benchmark
    public void benchmarkConstruction(Blackhole bh) {
        // Measure the cost of creating the token
        StreamStartToken token = new StreamStartToken(startMark, endMark);
        bh.consume(token);
    }

    @Benchmark
    public void benchmarkGetTokenId(Blackhole bh) {
        // Measure the cost of calling getTokenId() on an existing instance
        ID id = tokenInstance.getTokenId();
        bh.consume(id);
    }
}
