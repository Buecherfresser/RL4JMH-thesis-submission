package bench.generated.c061;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.tokens.StreamEndToken;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.tokens.Token;
import org.yaml.snakeyaml.tokens.Token.ID;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class StreamEndTokenBenchmark {

    private Mark startMark;
    private Mark endMark;
    private StreamEndToken token;

    @Setup(Level.Trial)
    public void setup() {
        // Instantiate Mark objects using a valid constructor signature (String, int, int, int, char[], int)
        // Dummy values are used as the benchmark focuses on StreamEndToken operations.
        startMark = new Mark("dummy", 0, 0, 0, new char[0], 0);
        endMark = new Mark("dummy", 0, 0, 0, new char[0], 0);
        
        // Pre-create the token instance for benchmarking construction and method calls
        token = new StreamEndToken(startMark, endMark);
    }

    /**
     * Benchmarks the construction of StreamEndToken.
     */
    @Benchmark
    public void benchmarkConstruction(Blackhole bh) {
        // Recreate the token in each invocation to measure construction cost
        StreamEndToken newToken = new StreamEndToken(startMark, endMark);
        bh.consume(newToken);
    }

    /**
     * Benchmarks the getTokenId() method call on an existing StreamEndToken instance.
     */
    @Benchmark
    public void benchmarkGetTokenId(Blackhole bh) {
        // Use the pre-created instance
        ID id = token.getTokenId();
        bh.consume(id);
    }
}
