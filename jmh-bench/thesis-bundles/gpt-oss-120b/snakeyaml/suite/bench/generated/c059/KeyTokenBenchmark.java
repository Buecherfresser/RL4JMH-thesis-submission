package bench.generated.c059;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.tokens.KeyToken;
import org.yaml.snakeyaml.tokens.Token;
import org.yaml.snakeyaml.error.Mark;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class KeyTokenBenchmark {

    private Mark startMark;
    private Mark endMark;
    private KeyToken prebuiltToken;

    @Setup(Level.Trial)
    public void setUp() {
        char[] buffer = "key: value".toCharArray();
        startMark = new Mark("benchmark", 0, 0, 0, buffer, 0);
        endMark = new Mark("benchmark", buffer.length, 0, buffer.length, buffer, buffer.length);
        prebuiltToken = new KeyToken(startMark, endMark);
    }

    @Benchmark
    public Token.ID benchmarkCreateAndGetId() {
        KeyToken token = new KeyToken(startMark, endMark);
        return token.getTokenId();
    }

    @Benchmark
    public Token.ID benchmarkGetIdPrebuilt() {
        return prebuiltToken.getTokenId();
    }

    @Benchmark
    public void benchmarkConsumeId(Blackhole bh) {
        bh.consume(prebuiltToken.getTokenId());
    }
}
