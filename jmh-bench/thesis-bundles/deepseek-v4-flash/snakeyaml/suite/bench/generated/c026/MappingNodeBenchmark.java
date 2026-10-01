package bench.generated.c026;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.Tag;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class MappingNodeBenchmark {

    @State(Scope.Benchmark)
    public static class BenchmarkState {
        MappingNode node;
        List<NodeTuple> tuples;
        List<NodeTuple> newTuples;
        MappingNode nodeForSetTypes;
        MappingNode nodeForSetOnlyKeyType;
        MappingNode nodeForSetMerged;
        MappingNode nodeForIsMerged;

        @Setup(Level.Trial)
        public void setup() {
            tuples = createTuples(10);
            newTuples = createTuples(10);
            node = new MappingNode(Tag.MAP, tuples, DumperOptions.FlowStyle.BLOCK);
            nodeForSetTypes = new MappingNode(Tag.MAP, createTuples(10), DumperOptions.FlowStyle.BLOCK);
            nodeForSetOnlyKeyType = new MappingNode(Tag.MAP, createTuples(10), DumperOptions.FlowStyle.BLOCK);
            nodeForSetMerged = new MappingNode(Tag.MAP, createTuples(10), DumperOptions.FlowStyle.BLOCK);
            nodeForIsMerged = new MappingNode(Tag.MAP, createTuples(10), DumperOptions.FlowStyle.BLOCK);
        }

        private List<NodeTuple> createTuples(int size) {
            List<NodeTuple> list = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                ScalarNode key = new ScalarNode(Tag.STR, "key" + i, null, null, DumperOptions.ScalarStyle.PLAIN);
                ScalarNode value = new ScalarNode(Tag.STR, "value" + i, null, null, DumperOptions.ScalarStyle.PLAIN);
                list.add(new NodeTuple(key, value));
            }
            return list;
        }
    }

    @Benchmark
    public org.yaml.snakeyaml.nodes.NodeId getNodeId(BenchmarkState state) {
        return state.node.getNodeId();
    }

    @Benchmark
    public List<NodeTuple> getValue(BenchmarkState state) {
        return state.node.getValue();
    }

    @Benchmark
    public MappingNode setValue(BenchmarkState state) {
        state.node.setValue(state.newTuples);
        return state.node;
    }

    @Benchmark
    public MappingNode setOnlyKeyType(BenchmarkState state) {
        state.nodeForSetOnlyKeyType.setOnlyKeyType(String.class);
        return state.nodeForSetOnlyKeyType;
    }

    @Benchmark
    public MappingNode setTypes(BenchmarkState state) {
        state.nodeForSetTypes.setTypes(String.class, String.class);
        return state.nodeForSetTypes;
    }

    @Benchmark
    public String toString(BenchmarkState state) {
        return state.node.toString();
    }

    @Benchmark
    public MappingNode setMerged(BenchmarkState state) {
        state.nodeForSetMerged.setMerged(true);
        return state.nodeForSetMerged;
    }

    @Benchmark
    public boolean isMerged(BenchmarkState state) {
        return state.nodeForIsMerged.isMerged();
    }

    @Benchmark
    public MappingNode createNode(BenchmarkState state) {
        return new MappingNode(Tag.MAP, state.newTuples, DumperOptions.FlowStyle.BLOCK);
    }
}
