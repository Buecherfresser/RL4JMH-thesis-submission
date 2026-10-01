package bench.generated.c061;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.tokens.StreamEndToken;
import org.yaml.snakeyaml.tokens.Token;
import org.yaml.snakeyaml.error.Mark;

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
        String input = "test";
        int[] buffer = new int[input.length()];
        for (int i = 0; i < input.length(); i++) {
            buffer[i] = input.charAt(i);
        }
        startMark = new Mark("test", 0, 0, 0, buffer, 0);
        endMark = new Mark("test", input.length(), 0, input.length(), buffer, input.length());
        token = new StreamEndToken(startMark, endMark);
    }

    @Benchmark
    public StreamEndToken construct() {
        return new StreamEndToken(startMark, endMark);
    }

    @Benchmark
    public Token.ID getTokenId() {
        return token.getTokenId();
    }

    @Benchmark
    public void getMarks(Blackhole bh) {
        bh.consume(token.getStartMark());
        bh.consume(token.getEndMark());
    }

    @Benchmark
    public String toString() {
        return token.toString();
    }
}
