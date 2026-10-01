package bench.generated.c029;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.nodes.NodeId;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.Tag;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ScalarNodeBenchmark {

    private ScalarNode plainScalar;
    private ScalarNode quotedScalar;
    private Tag tag;
    private String value;
    private Mark startMark;
    private Mark endMark;
    private DumperOptions.ScalarStyle plainStyle;
    private DumperOptions.ScalarStyle quotedStyle;
    private boolean resolved;

    @Setup(Level.Trial)
    public void setUp() {
        value = "benchmark scalar value";
        tag = new Tag("tag:yaml.org,2002:str");
        char[] buffer = value.toCharArray();
        startMark = new Mark("benchmark", 0, 0, 0, buffer, 0);
        endMark = new Mark("benchmark", value.length(), 0, value.length(), buffer, value.length());
        plainStyle = DumperOptions.ScalarStyle.PLAIN;
        quotedStyle = DumperOptions.ScalarStyle.SINGLE_QUOTED;
        resolved = true;

        plainScalar = new ScalarNode(tag, value, startMark, endMark, plainStyle);
        quotedScalar = new ScalarNode(tag, !resolved, value, startMark, endMark, quotedStyle);
    }

    @Benchmark
    public ScalarNode constructScalarNode() {
        return new ScalarNode(tag, value, startMark, endMark, plainStyle);
    }

    @Benchmark
    public ScalarNode constructScalarNodeWithResolved() {
        return new ScalarNode(tag, resolved, value, startMark, endMark, quotedStyle);
    }

    @Benchmark
    public String scalarGetValue() {
        return plainScalar.getValue();
    }

    @Benchmark
    public void scalarGetValueConsumed(Blackhole bh) {
        bh.consume(plainScalar.getValue());
    }

    @Benchmark
    public DumperOptions.ScalarStyle scalarGetScalarStyle() {
        return plainScalar.getScalarStyle();
    }

    @Benchmark
    public NodeId scalarGetNodeId() {
        return plainScalar.getNodeId();
    }

    @Benchmark
    public boolean scalarIsPlainPlain() {
        return plainScalar.isPlain();
    }

    @Benchmark
    public boolean scalarIsPlainQuoted() {
        return quotedScalar.isPlain();
    }

    @Benchmark
    public String scalarToString() {
        return plainScalar.toString();
    }

    @Benchmark
    public Tag scalarGetTag() {
        return plainScalar.getTag();
    }

    @Benchmark
    public Mark scalarGetStartMark() {
        return plainScalar.getStartMark();
    }

    @Benchmark
    public Mark scalarGetEndMark() {
        return plainScalar.getEndMark();
    }
}
