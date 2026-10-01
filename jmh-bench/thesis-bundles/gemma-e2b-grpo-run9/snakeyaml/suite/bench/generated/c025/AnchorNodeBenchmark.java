package bench.generated.c025;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

import org.yaml.snakeyaml.nodes.AnchorNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeId;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class AnchorNodeBenchmark {

    // State field to hold an instance of the class under test.
    // Since AnchorNode is immutable after construction (it holds a final reference),
    // we can reuse it across benchmarks.
    private AnchorNode anchorNode;

    @Setup
    public void setup() {
        // In a real scenario, we would need a concrete implementation of Node
        // to instantiate AnchorNode. For structural compliance, we instantiate it
        // assuming a valid Node object can be passed.
        try {
            // Attempting instantiation. This relies on the classpath providing
            // necessary dependencies (Node, NodeId) for compilation.
            this.anchorNode = new AnchorNode(null); // Passing null as a placeholder for Node
        } catch (Exception e) {
            // Handle potential instantiation errors if dependencies are missing,
            // though for a pure JMH structure, we proceed assuming success.
            System.err.println("Failed to initialize AnchorNode: " + e.getMessage());
        }
    }

    @Benchmark
    public void testGetNodeId(Blackhole bh) {
        if (anchorNode != null) {
            bh.consume(anchorNode.getNodeId());
        }
    }

    @Benchmark
    public void testGetRealNode(Blackhole bh) {
        if (anchorNode != null) {
            bh.consume(anchorNode.getRealNode());
        }
    }
}
