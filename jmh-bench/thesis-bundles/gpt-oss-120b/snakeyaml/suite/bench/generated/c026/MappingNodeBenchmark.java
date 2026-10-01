package bench.generated.c026;

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
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.nodes.NodeId;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.DumperOptions;
import java.util.List;
import java.util.ArrayList;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class MappingNodeBenchmark {

    private MappingNode node;
    private List<NodeTuple> originalValue;
    private List<NodeTuple> otherValue;
    private Tag tag;
    private DumperOptions.FlowStyle flowStyle;

    @Setup(Level.Trial)
    public void setup() {
        tag = new Tag("!test");
        flowStyle = DumperOptions.FlowStyle.BLOCK;

        // Build first list of NodeTuple
        Node key1 = new ScalarNode(tag, "key1", null, null, DumperOptions.ScalarStyle.PLAIN);
        Node value1 = new ScalarNode(tag, "value1", null, null, DumperOptions.ScalarStyle.PLAIN);
        Node key2 = new ScalarNode(tag, "key2", null, null, DumperOptions.ScalarStyle.PLAIN);
        Node value2 = new ScalarNode(tag, "value2", null, null, DumperOptions.ScalarStyle.PLAIN);
        originalValue = new ArrayList<>();
        originalValue.add(new NodeTuple(key1, value1));
        originalValue.add(new NodeTuple(key2, value2));

        // Build second list of NodeTuple for setValue benchmark
        Node key3 = new ScalarNode(tag, "key3", null, null, DumperOptions.ScalarStyle.PLAIN);
        Node value3 = new ScalarNode(tag, "value3", null, null, DumperOptions.ScalarStyle.PLAIN);
        Node key4 = new ScalarNode(tag, "key4", null, null, DumperOptions.ScalarStyle.PLAIN);
        Node value4 = new ScalarNode(tag, "value4", null, null, DumperOptions.ScalarStyle.PLAIN);
        otherValue = new ArrayList<>();
        otherValue.add(new NodeTuple(key3, value3));
        otherValue.add(new NodeTuple(key4, value4));

        node = new MappingNode(tag, originalValue, flowStyle);
    }

    @Benchmark
    public List<NodeTuple> benchmarkGetValue() {
        return node.getValue();
    }

    @Benchmark
    public void benchmarkSetValue(Blackhole bh) {
        node.setValue(otherValue);
        bh.consume(node.getValue());
    }

    @Benchmark
    public void benchmarkSetOnlyKeyType(Blackhole bh) {
        node.setOnlyKeyType(String.class);
        bh.consume(node.getValue());
    }

    @Benchmark
    public void benchmarkSetTypes(Blackhole bh) {
        node.setTypes(String.class, Integer.class);
        bh.consume(node.getValue());
    }

    @Benchmark
    public String benchmarkToString() {
        return node.toString();
    }

    @Benchmark
    public void benchmarkSetMerged(Blackhole bh) {
        node.setMerged(true);
        bh.consume(node.isMerged());
    }

    @Benchmark
    public boolean benchmarkIsMerged() {
        return node.isMerged();
    }

    @Benchmark
    public NodeId benchmarkGetNodeId() {
        return node.getNodeId();
    }

    @Benchmark
    public MappingNode benchmarkConstructor() {
        List<NodeTuple> freshList = new ArrayList<>();
        Node k = new ScalarNode(tag, "k", null, null, DumperOptions.ScalarStyle.PLAIN);
        Node v = new ScalarNode(tag, "v", null, null, DumperOptions.ScalarStyle.PLAIN);
        freshList.add(new NodeTuple(k, v));
        return new MappingNode(tag, freshList, flowStyle);
    }
}
