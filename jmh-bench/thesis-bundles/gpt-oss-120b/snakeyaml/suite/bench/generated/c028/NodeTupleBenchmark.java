package bench.generated.c028;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.DumperOptions;

@State(org.openjdk.jmh.annotations.Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class NodeTupleBenchmark {

    private NodeTuple tuple;
    private ScalarNode keyNode;
    private ScalarNode valueNode;

    @Setup(Level.Trial)
    public void setUp() {
        // Use null marks and plain style for simplicity
        Mark dummyMark = null;
        this.keyNode = new ScalarNode(Tag.STR, "benchmarkKey", dummyMark, dummyMark, DumperOptions.ScalarStyle.PLAIN);
        this.valueNode = new ScalarNode(Tag.STR, "benchmarkValue", dummyMark, dummyMark, DumperOptions.ScalarStyle.PLAIN);
        this.tuple = new NodeTuple(keyNode, valueNode);
    }

    @Benchmark
    public NodeTuple benchmarkConstructor() {
        return new NodeTuple(keyNode, valueNode);
    }

    @Benchmark
    public org.yaml.snakeyaml.nodes.Node benchmarkGetKeyNode() {
        return tuple.getKeyNode();
    }

    @Benchmark
    public org.yaml.snakeyaml.nodes.Node benchmarkGetValueNode() {
        return tuple.getValueNode();
    }

    @Benchmark
    public String benchmarkToString() {
        return tuple.toString();
    }

    @Benchmark
    public void benchmarkConsumeKeyNode(Blackhole bh) {
        bh.consume(tuple.getKeyNode());
    }

    @Benchmark
    public void benchmarkConsumeValueNode(Blackhole bh) {
        bh.consume(tuple.getValueNode());
    }

    @Benchmark
    public void benchmarkConsumeToString(Blackhole bh) {
        bh.consume(tuple.toString());
    }
}
