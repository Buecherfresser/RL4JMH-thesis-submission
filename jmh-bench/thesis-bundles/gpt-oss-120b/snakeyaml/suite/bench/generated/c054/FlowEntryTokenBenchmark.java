package bench.generated.c054;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.tokens.FlowEntryToken;
import org.yaml.snakeyaml.tokens.Token.ID;
import org.yaml.snakeyaml.error.Mark;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class FlowEntryTokenBenchmark {

    private Mark startMark;
    private Mark endMark;
    private FlowEntryToken preCreatedToken;

    @Setup
    public void setup() {
        char[] emptyBuffer = new char[0];
        // Mark(String name, int line, int column, int index, char[] buffer, int bufferPos)
        this.startMark = new Mark("benchmark", 0, 0, 0, emptyBuffer, 0);
        this.endMark = new Mark("benchmark", 0, 0, 0, emptyBuffer, 0);
        this.preCreatedToken = new FlowEntryToken(startMark, endMark);
    }

    @Benchmark
    public FlowEntryToken benchmarkCreateToken() {
        return new FlowEntryToken(startMark, endMark);
    }

    @Benchmark
    public ID benchmarkGetTokenId() {
        return preCreatedToken.getTokenId();
    }

    @Benchmark
    public void benchmarkConsumeToken(Blackhole bh) {
        FlowEntryToken token = new FlowEntryToken(startMark, endMark);
        bh.consume(token);
    }
}
