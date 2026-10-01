package bench.generated.c053;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.tokens.DocumentStartToken;
import org.yaml.snakeyaml.tokens.Token;
import org.yaml.snakeyaml.error.Mark;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class DocumentStartTokenBenchmark {

    private Mark startMark;
    private Mark endMark;
    private DocumentStartToken token;

    @Setup
    public void setup() {
        char[] buffer = "dummy".toCharArray();
        // Mark constructor: (String name, int line, int column, int index, char[] buffer, int bufferStart)
        this.startMark = new Mark("start", 0, 0, 0, buffer, 0);
        this.endMark = new Mark("end", 0, buffer.length, 0, buffer, buffer.length);
        this.token = new DocumentStartToken(startMark, endMark);
    }

    @Benchmark
    public DocumentStartToken benchmarkCreateToken() {
        return new DocumentStartToken(startMark, endMark);
    }

    @Benchmark
    public Token.ID benchmarkGetTokenId() {
        return token.getTokenId();
    }

    @Benchmark
    public void benchmarkConsumeToken(Blackhole bh) {
        bh.consume(token);
    }
}
