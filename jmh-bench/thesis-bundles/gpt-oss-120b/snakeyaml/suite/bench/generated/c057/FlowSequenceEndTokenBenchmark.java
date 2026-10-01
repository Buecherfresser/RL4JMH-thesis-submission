package bench.generated.c057;

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
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.tokens.FlowSequenceEndToken;
import org.yaml.snakeyaml.tokens.Token;
import org.yaml.snakeyaml.error.Mark;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class FlowSequenceEndTokenBenchmark {

    private Mark startMark;
    private Mark endMark;
    private FlowSequenceEndToken token;

    @Setup
    public void setup() {
        char[] emptyBuffer = new char[0];
        this.startMark = new Mark("benchmark", 0, 0, 0, emptyBuffer, 0);
        this.endMark = new Mark("benchmark", 0, 0, 0, emptyBuffer, 0);
        this.token = new FlowSequenceEndToken(startMark, endMark);
    }

    @Benchmark
    public FlowSequenceEndToken benchmarkCreateToken() {
        return new FlowSequenceEndToken(startMark, endMark);
    }

    @Benchmark
    public Token.ID benchmarkGetTokenId() {
        return token.getTokenId();
    }
}
