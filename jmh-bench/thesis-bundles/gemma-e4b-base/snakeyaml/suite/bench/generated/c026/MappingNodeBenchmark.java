package bench.generated.c026;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.yaml.snakeyaml.nodes.*;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.DumperOptions;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class MappingNodeBenchmark {

    private MappingNode mappingNode;
    private List<NodeTuple> sampleValueList;
    private Tag sampleTag;
    private DumperOptions.FlowStyle sampleFlowStyle;

    // Helper method to create a minimal Node instance satisfying the required constructor
    private Node createMinimalNode(Tag tag, Mark mark) {
        // Node requires (Tag, Mark, Mark) based on original structure
        return new Node(tag, mark, mark) {
            // Override methods used in the benchmark/toString
            @Override
            public NodeId getNodeId() { return NodeId.scalar; }
            @Override
            public String toString() { return "MockNode"; }
            // Mock setType if needed by the benchmark methods
            public void setType(Class<?> type) { /* Mock implementation */ }
        };
    }

    // Helper method to create a minimal Mark instance
    private Mark createMinimalMark() {
        // Fix: Use a valid constructor for Mark, e.g., (String, int, int, int, char[], int)
        return new Mark("dummy", 0, 0, 0, new char[0], 0);
    }

    // Helper method to create a minimal NodeTuple
    private NodeTuple createMinimalNodeTuple(Node key, Node value) {
        return new NodeTuple(key, value);
    }

    @Setup(Level.Trial)
    public void setup() {
        // 1. Setup common dependencies
        sampleTag = new Tag("!map");
        sampleFlowStyle = DumperOptions.FlowStyle.BLOCK;
        Mark minimalMark = createMinimalMark();

        // 2. Build complex input data (e.g., 10 entries)
        sampleValueList = new ArrayList<>(10);
        for (int i = 0; i < 10; i++) {
            // Create nodes using the required constructor signature
            Node key = createMinimalNode(sampleTag, minimalMark);
            Node value = createMinimalNode(sampleTag, minimalMark);
            sampleValueList.add(createMinimalNodeTuple(key, value));
        }

        // 3. Initialize the SUT using the constructor that accepts null marks
        // MappingNode(Tag tag, List<NodeTuple> value, DumperOptions.FlowStyle flowStyle)
        mappingNode = new MappingNode(sampleTag, sampleValueList, sampleFlowStyle);
    }

    @Benchmark
    public void testGetValue(Blackhole bh) {
        bh.consume(mappingNode.getValue());
    }

    @Benchmark
    public void testIsMerged(Blackhole bh) {
        bh.consume(mappingNode.isMerged());
    }

    @Benchmark
    public void testSetMerged(Blackhole bh) {
        mappingNode.setMerged(true);
        bh.consume(mappingNode.isMerged());
    }

    @Benchmark
    public void testToString(Blackhole bh) {
        bh.consume(mappingNode.toString());
    }

    @Benchmark
    public void testSetValue(Blackhole bh) {
        // Create a fresh list to simulate a replacement operation
        List<NodeTuple> newValues = new ArrayList<>(sampleValueList.size());
        for (NodeTuple tuple : sampleValueList) {
            // Simple reference copy suffices for benchmarking the setter
            newValues.add(tuple);
        }
        mappingNode.setValue(newValues);
        bh.consume(mappingNode.getValue());
    }

    @Benchmark
    public void testSetOnlyKeyType(Blackhole bh) {
        // This method iterates over the internal list and modifies nodes.
        mappingNode.setOnlyKeyType(String.class);
        bh.consume(mappingNode);
    }

    @Benchmark
    public void testSetTypes(Blackhole bh) {
        // This method iterates over the internal list and modifies both key and value nodes.
        mappingNode.setTypes(String.class, Integer.class);
        bh.consume(mappingNode);
    }
}
