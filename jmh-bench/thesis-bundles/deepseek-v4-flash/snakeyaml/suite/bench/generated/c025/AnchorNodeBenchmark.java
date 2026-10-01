package bench.generated.c025;

import org.openjdk.jmh.annotations.*;
import java.util.concurrent.TimeUnit;
import java.io.StringReader;
import org.yaml.snakeyaml.Yaml;
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

    private Node realNode;
    private AnchorNode anchorNode;

    @Setup(Level.Trial)
    public void setup() {
        Yaml yaml = new Yaml();
        realNode = yaml.compose(new StringReader("hello"));
        anchorNode = new AnchorNode(realNode);
    }

    @Benchmark
    public AnchorNode constructAnchorNode() {
        return new AnchorNode(realNode);
    }

    @Benchmark
    public NodeId getNodeId() {
        return anchorNode.getNodeId();
    }

    @Benchmark
    public Node getRealNode() {
        return anchorNode.getRealNode();
    }
}
