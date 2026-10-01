package bench.generated.c065;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.tokens.ValueToken;
import org.yaml.snakeyaml.tokens.Token;
import org.yaml.snakeyaml.error.Mark;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ValueTokenBenchmark {

    private Mark startMark;
    private Mark endMark;
    private ValueToken reusableToken;

    @Setup(Level.Trial)
    public void setUp() {
        char[] buffer = "benchmark".toCharArray();
        // Mark(name, line, column, index, buffer, pointer)
        startMark = new Mark("benchmark", 0, 0, 0, buffer, 0);
        endMark = new Mark("benchmark", 0, buffer.length, 0, buffer, buffer.length);
        reusableToken = new ValueToken(startMark, endMark);
    }

    @Benchmark
    public ValueToken constructValueToken() {
        return new ValueToken(startMark, endMark);
    }

    @Benchmark
    public Token.ID getTokenId() {
        return reusableToken.getTokenId();
    }
}
