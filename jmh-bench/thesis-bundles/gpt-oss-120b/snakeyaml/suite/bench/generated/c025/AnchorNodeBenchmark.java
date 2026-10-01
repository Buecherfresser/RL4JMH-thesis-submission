package bench.generated.c025;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.nodes.AnchorNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.ScalarNode;
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
public class AnchorNodeBenchmark {

    private Node realNode;
    private AnchorNode anchorNode;
    private Mark dummyMark;

    @Setup
    public void setup() {
        dummyMark = new Mark("benchmark", 0, 0, 0, new char[0], 0);
        realNode = new ScalarNode(Tag.STR, "benchmark-value", dummyMark, dummyMark,
                DumperOptions.ScalarStyle.PLAIN);
        anchorNode = new AnchorNode(realNode);
    }

    @Benchmark
    public AnchorNode constructAnchorNode() {
        return new AnchorNode(realNode);
    }

    @Benchmark
    public NodeId benchmarkGetNodeId() {
        return anchorNode.getNodeId();
    }

    @Benchmark
    public Node benchmarkGetRealNode() {
        return anchorNode.getRealNode();
    }
}
