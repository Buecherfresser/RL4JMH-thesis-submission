package bench.generated.c030;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.ArrayList;
import java.util.List;
import org.yaml.snakeyaml.nodes.SequenceNode;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.nodes.NodeId;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.error.Mark;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class SequenceNodeBenchmark {

    @State(Scope.Benchmark)
    public static class BenchmarkState {
        SequenceNode node;
        List<Node> nodes;
        Tag tag;
        Mark startMark;
        Mark endMark;
        DumperOptions.FlowStyle flowStyle;
        Class<?> listType;

        @Setup(Level.Trial)
        public void setup() {
            nodes = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                nodes.add(new ScalarNode(Tag.STR, "value" + i, null, null,
                        DumperOptions.ScalarStyle.PLAIN));
            }
            tag = Tag.SEQ;
            startMark = new Mark("test", 0, 0, 0, new int[]{}, 0);
            endMark = new Mark("test", 10, 0, 10, new int[]{}, 10);
            flowStyle = DumperOptions.FlowStyle.BLOCK;
            node = new SequenceNode(tag, nodes, flowStyle);
            listType = String.class;
        }
    }

    @Benchmark
    public SequenceNode constructFull(BenchmarkState state) {
        return new SequenceNode(state.tag, true, state.nodes, state.startMark,
                state.endMark, state.flowStyle);
    }

    @Benchmark
    public SequenceNode constructSimple(BenchmarkState state) {
        return new SequenceNode(state.tag, state.nodes, state.flowStyle);
    }

    @Benchmark
    public NodeId getNodeId(BenchmarkState state) {
        return state.node.getNodeId();
    }

    @Benchmark
    public List<Node> getValue(BenchmarkState state) {
        return state.node.getValue();
    }

    @Benchmark
    public void setListType(BenchmarkState state, Blackhole bh) {
        state.node.setListType(state.listType);
        bh.consume(state.node);
    }

    @Benchmark
    public String toString(BenchmarkState state) {
        return state.node.toString();
    }

    @Benchmark
    public Tag getTag(BenchmarkState state) {
        return state.node.getTag();
    }
}
