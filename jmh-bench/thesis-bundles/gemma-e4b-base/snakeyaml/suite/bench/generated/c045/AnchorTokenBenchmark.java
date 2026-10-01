package bench.generated.c045;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.tokens.AnchorToken;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.tokens.Token;
import org.yaml.snakeyaml.tokens.Token.ID;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class AnchorTokenBenchmark {

    private AnchorToken anchorToken;
    private Mark startMark;
    private Mark endMark;
    private final String testValue = "myAnchor";

    @Setup(Level.Trial)
    public void setup() {
        // Mark does not have a no-argument constructor. We must use one of the available constructors
        // and provide dummy values to satisfy the compiler, as the benchmark focuses on AnchorToken logic.
        // Using the constructor: Mark(String, int, int, int, char[], int)
        this.startMark = new Mark("dummy", 0, 0, 0, new char[0], 0);
        this.endMark = new Mark("dummy", 0, 0, 0, new char[0], 0);
        
        // Initialize the subject under test
        this.anchorToken = new AnchorToken(testValue, startMark, endMark);
    }

    @Benchmark
    public void benchmarkGetValue(Blackhole bh) {
        String result = anchorToken.getValue();
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkGetTokenId(Blackhole bh) {
        ID result = anchorToken.getTokenId();
        bh.consume(result);
    }
}
