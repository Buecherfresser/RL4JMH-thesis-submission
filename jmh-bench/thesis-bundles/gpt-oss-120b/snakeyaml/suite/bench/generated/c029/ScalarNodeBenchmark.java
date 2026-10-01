package bench.generated.c029;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.error.Mark;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ScalarNodeBenchmark {

    private Tag tag;
    private Mark startMark;
    private Mark endMark;
    private String plainValue;
    private String quotedValue;
    private ScalarNode plainNode;
    private ScalarNode quotedNode;

    @Setup(Level.Trial)
    public void setup() {
        tag = Tag.STR;
        startMark = new Mark("test", 0, 0, 0, new char[0], 0);
        endMark = new Mark("test", 0, 0, 0, new char[0], 0);
        plainValue = "simple scalar";
        quotedValue = "quoted scalar";
        plainNode = new ScalarNode(tag, plainValue, startMark, endMark, DumperOptions.ScalarStyle.PLAIN);
        quotedNode = new ScalarNode(tag, quotedValue, startMark, endMark, DumperOptions.ScalarStyle.SINGLE_QUOTED);
    }

    @Benchmark
    public DumperOptions.ScalarStyle benchmarkGetScalarStylePlain() {
        return plainNode.getScalarStyle();
    }

    @Benchmark
    public DumperOptions.ScalarStyle benchmarkGetScalarStyleQuoted() {
        return quotedNode.getScalarStyle();
    }

    @Benchmark
    public String benchmarkGetValuePlain() {
        return plainNode.getValue();
    }

    @Benchmark
    public String benchmarkGetValueQuoted() {
        return quotedNode.getValue();
    }

    @Benchmark
    public boolean benchmarkIsPlainTrue() {
        return plainNode.isPlain();
    }

    @Benchmark
    public boolean benchmarkIsPlainFalse() {
        return quotedNode.isPlain();
    }

    @Benchmark
    public String benchmarkToStringPlain() {
        return plainNode.toString();
    }

    @Benchmark
    public String benchmarkToStringQuoted() {
        return quotedNode.toString();
    }

    @Benchmark
    public ScalarNode benchmarkCreatePlainNode() {
        return new ScalarNode(tag, plainValue, startMark, endMark, DumperOptions.ScalarStyle.PLAIN);
    }

    @Benchmark
    public ScalarNode benchmarkCreateQuotedNode() {
        return new ScalarNode(tag, quotedValue, startMark, endMark, DumperOptions.ScalarStyle.SINGLE_QUOTED);
    }
}
