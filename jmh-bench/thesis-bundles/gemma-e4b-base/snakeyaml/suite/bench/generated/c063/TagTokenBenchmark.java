package bench.generated.c063;

import org.openjdk.jmh.annotations.*;
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

    private TagToken tagToken;
    private TagTuple tagTuple;
    private Mark startMark;
    private Mark endMark;

    @Setup(Level.Trial)
    public void setup() {
        // Initialize TagTuple using the required String, String constructor
        this.tagTuple = new TagTuple("tag", "value");
        
        // Initialize Mark using a constructor matching the required signature (String, int, int, int, int[], int)
        this.startMark = new Mark("stream", 0, 0, 0, new int[0], 0);
        this.endMark = new Mark("stream", 0, 0, 0, new int[0], 0);

        // Initialize the Subject Under Test
        this.tagToken = new TagToken(this.tagTuple, this.startMark, this.endMark);
    }

    @Benchmark
    public void benchmarkGetValue(Blackhole bh) {
        // Exercise the getValue() method
        TagTuple result = tagToken.getValue();
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkGetTokenId(Blackhole bh) {
        // Exercise the getTokenId() method
        Token.ID result = tagToken.getTokenId();
        bh.consume(result);
    }
}
