package bench.generated.c063;

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
import org.yaml.snakeyaml.tokens.TagToken;
import org.yaml.snakeyaml.tokens.TagTuple;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.tokens.Token;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class TagTokenBenchmark {

    private TagToken token;
    private TagTuple tuple;
    private Mark startMark;
    private Mark endMark;

    @Setup
    public void setup() {
        char[] buffer = new char[0];
        this.startMark = new Mark("start", 0, 0, 0, buffer, 0);
        this.endMark = new Mark("end", 0, 0, 0, buffer, 0);
        this.tuple = new TagTuple("!", "tag");
        this.token = new TagToken(this.tuple, this.startMark, this.endMark);
    }

    @Benchmark
    public TagToken benchmarkConstructor() {
        return new TagToken(this.tuple, this.startMark, this.endMark);
    }

    @Benchmark
    public TagTuple benchmarkGetValue() {
        return this.token.getValue();
    }

    @Benchmark
    public Token.ID benchmarkGetTokenId() {
        return this.token.getTokenId();
    }
}
