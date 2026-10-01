package bench.generated.c059;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.tokens.KeyToken;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.tokens.Token;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class KeyTokenBenchmark {

    private Mark startMark;
    private Mark endMark;
    private KeyToken keyToken;

    @Setup(Level.Trial)
    public void setup() {
        // Initialize required inputs (Marks).
        // Since Mark requires specific arguments, we use dummy values matching the constructor signature.
        // Constructor signature used: Mark(String name, int start, int length, int line, char[] chars, int charIndex)
        startMark = new Mark("start", 0, 0, 0, new char[0], 0);
        endMark = new Mark("end", 0, 0, 0, new char[0], 0);
    }

    @Setup(Level.Iteration)
    public void setupInstance() {
        // Initialize the subject instance for method testing
        keyToken = new KeyToken(startMark, endMark);
    }

    @Benchmark
    public void benchmarkKeyTokenConstruction(Blackhole bh) {
        // Test the constructor
        KeyToken token = new KeyToken(startMark, endMark);
        bh.consume(token);
    }

    @Benchmark
    public void benchmarkKeyTokenGetTokenId(Blackhole bh) {
        // Test the getTokenId() method
        Token.ID id = keyToken.getTokenId();
        bh.consume(id);
    }
}
