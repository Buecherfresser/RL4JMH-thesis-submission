package bench.generated.c057;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.tokens.FlowSequenceEndToken;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.tokens.Token;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class FlowSequenceEndTokenBenchmark {

    private Mark startMark;
    private Mark endMark;
    private FlowSequenceEndToken tokenInstance;

    @Setup(Level.Trial)
    public void setup() {
        // Initialize required inputs (Marks).
        // Using dummy values to satisfy the required constructor signature of Mark.
        startMark = new Mark("dummy", 0, 0, 0, new char[0], 0);
        endMark = new Mark("dummy", 0, 0, 0, new char[0], 0);

        // Pre-create the token instance for getter benchmarks
        tokenInstance = new FlowSequenceEndToken(startMark, endMark);
    }

    @Benchmark
    public FlowSequenceEndToken createToken() {
        // Benchmark the constructor
        return new FlowSequenceEndToken(startMark, endMark);
    }

    @Benchmark
    public void getTokenId(Blackhole bh) {
        // Benchmark the getter method
        Token.ID id = tokenInstance.getTokenId();
        // Consume the result using the Blackhole parameter
        bh.consume(id);
    }
}
