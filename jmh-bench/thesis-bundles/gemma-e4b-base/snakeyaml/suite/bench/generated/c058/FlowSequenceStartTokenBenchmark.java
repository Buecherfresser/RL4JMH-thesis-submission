package bench.generated.c058;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.tokens.FlowSequenceStartToken;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.tokens.Token;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class FlowSequenceStartTokenBenchmark {

    private Mark startMark;
    private Mark endMark;
    private FlowSequenceStartToken tokenInstance;

    @Setup(Level.Trial)
    public void setup() {
        // Initialize required inputs (Mark objects).
        // Mark requires parameters, so we use dummy data to satisfy the constructor:
        // Mark(String, int, int, int, char[], int)
        char[] dummyChars = new char[1];
        startMark = new Mark("start", 0, 0, 0, dummyChars, 0);
        endMark = new Mark("end", 0, 0, 0, dummyChars, 0);
    }

    @Setup(Level.Iteration)
    public void setupTokenInstance() {
        // Initialize the token instance for getter benchmarks
        tokenInstance = new FlowSequenceStartToken(startMark, endMark);
    }

    @Benchmark
    public void benchmarkConstructor(Blackhole bh) {
        // Measure the cost of creating the token
        FlowSequenceStartToken token = new FlowSequenceStartToken(startMark, endMark);
        bh.consume(token);
    }

    @Benchmark
    public void benchmarkGetTokenId(Blackhole bh) {
        // Measure the cost of calling the getter on an existing instance
        Token.ID id = tokenInstance.getTokenId();
        bh.consume(id);
    }
}
