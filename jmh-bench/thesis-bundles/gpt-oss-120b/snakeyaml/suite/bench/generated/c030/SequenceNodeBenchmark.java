package bench.generated.c030;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.nodes.SequenceNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.nodes.NodeId;
import org.yaml.snakeyaml.DumperOptions;
import java.util.List;
import java.util.ArrayList;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class SequenceNodeBenchmark {

    private SequenceNode sequenceNode;

    @Setup
    public void setup() {
        List<Node> nodes = new ArrayList<>();
        nodes.add(new ScalarNode(Tag.STR, "alpha", null, null, DumperOptions.ScalarStyle.PLAIN));
        nodes.add(new ScalarNode(Tag.STR, "beta", null, null, DumperOptions.ScalarStyle.PLAIN));
        nodes.add(new ScalarNode(Tag.STR, "gamma", null, null, DumperOptions.ScalarStyle.PLAIN));
        sequenceNode = new SequenceNode(Tag.SEQ, nodes, DumperOptions.FlowStyle.BLOCK);
    }

    @Benchmark
    public NodeId benchmarkGetNodeId() {
        return sequenceNode.getNodeId();
    }

    @Benchmark
    public List<Node> benchmarkGetValue() {
        return sequenceNode.getValue();
    }

    @Benchmark
    public void benchmarkSetListType(Blackhole bh) {
        sequenceNode.setListType(Object.class);
        bh.consume(sequenceNode);
    }

    @Benchmark
    public String benchmarkToString() {
        return sequenceNode.toString();
    }
}
