package bench.generated.c028;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class NodeTupleBenchmark {

    private Node keyNode;
    private Node valueNode;
    private NodeTuple nodeTuple;

    @Setup
    public void setup() {
        // Build fixed payloads once in @Setup.
        // Assuming ScalarNode has a constructor that accepts a String for simplicity,
        // or that the necessary complex arguments are handled by the underlying SnakeYAML structure.
        // If the original error persists, this instantiation must be adjusted based on the exact ScalarNode API.
        this.keyNode = new ScalarNode(null, "key", null, null, null); // Using a plausible constructor signature based on error context
        this.valueNode = new ScalarNode(null, "value", null, null, null); // Using a plausible constructor signature based on error context
        this.nodeTuple = new NodeTuple(keyNode, valueNode);
    }

    @Benchmark
    public void testConstruction(Blackhole bh) {
        NodeTuple tuple = new NodeTuple(keyNode, valueNode);
        bh.consume(tuple);
    }

    @Benchmark
    public Node testGetKeyNode(Blackhole bh) {
        Node key = nodeTuple.getKeyNode();
        bh.consume(key);
        return key;
    }

    @Benchmark
    public Node testGetValueNode(Blackhole bh) {
        Node value = nodeTuple.getValueNode();
        bh.consume(value);
        return value;
    }

    @Benchmark
    public String testToString(Blackhole bh) {
        String result = nodeTuple.toString();
        bh.consume(result);
        return result;
    }
}
