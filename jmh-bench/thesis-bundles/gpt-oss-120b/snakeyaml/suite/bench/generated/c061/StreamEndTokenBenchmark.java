package bench.generated.c061;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.tokens.StreamEndToken;
import org.yaml.snakeyaml.tokens.Token.ID;
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

    @Setup
    public void setup() {
        char[] buffer = new char[0];
        this.startMark = new Mark("start", 0, 0, 0, buffer, 0);
        this.endMark = new Mark("end", 0, 0, 0, buffer, 0);
        this.token = new StreamEndToken(startMark, endMark);
    }

    @Benchmark
    public StreamEndToken benchmarkConstructToken() {
        return new StreamEndToken(startMark, endMark);
    }

    @Benchmark
    public ID benchmarkGetTokenId() {
        return token.getTokenId();
    }

    @Benchmark
    public void benchmarkConsumeTokenId(Blackhole bh) {
        bh.consume(token.getTokenId());
    }
}
