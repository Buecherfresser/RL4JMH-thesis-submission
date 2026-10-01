package bench.generated.c058;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.tokens.FlowSequenceStartToken;
import org.yaml.snakeyaml.error.Mark;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class FlowSequenceStartTokenBenchmark {

    private Mark startMark;
    private Mark endMark;
    private FlowSequenceStartToken token;

    @Setup
    public void setUp() {
        char[] buffer = new char[0];
        // Mark constructor requires an additional index argument (set to 0)
        this.startMark = new Mark("start", 0, 0, 0, buffer, 0);
        this.endMark = new Mark("end", 0, 0, 0, buffer, 0);
        this.token = new FlowSequenceStartToken(startMark, endMark);
    }

    @Benchmark
    public FlowSequenceStartToken benchmarkConstructor() {
        return new FlowSequenceStartToken(startMark, endMark);
    }

    @Benchmark
    public void benchmarkGetTokenId(Blackhole bh) {
        bh.consume(token.getTokenId());
    }

    @Benchmark
    public void benchmarkGetStartMark(Blackhole bh) {
        bh.consume(token.getStartMark());
    }

    @Benchmark
    public void benchmarkGetEndMark(Blackhole bh) {
        bh.consume(token.getEndMark());
    }
}
