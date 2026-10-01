package bench.generated.c029;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.nodes.NodeId;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ScalarNodeBenchmark {

    private ScalarNode scalarNode;
    private Tag sampleTag;
    private Mark startMark;
    private Mark endMark;
    private String sampleValue;

    @Setup(Level.Trial)
    public void setup() {
        // Initialize dummy/sample inputs required for ScalarNode construction
        
        // Tag requires a String or Class constructor
        sampleTag = new Tag("!!scalar");
        
        // Mark requires complex constructors. Using dummy data for the first signature:
        // Mark(String, int, int, int, char[], int)
        startMark = new Mark("start", 0, 0, 0, new char[0], 0);
        endMark = new Mark("end", 0, 0, 0, new char[0], 0);
        
        sampleValue = "Test scalar value for benchmarking.";

        // Create the ScalarNode instance
        scalarNode = new ScalarNode(sampleTag, false, sampleValue, startMark, endMark, DumperOptions.ScalarStyle.PLAIN);
    }

    @Benchmark
    public void testGetValue(Blackhole bh) {
        String value = scalarNode.getValue();
        bh.consume(value);
    }

    @Benchmark
    public void testGetScalarStyle(Blackhole bh) {
        DumperOptions.ScalarStyle style = scalarNode.getScalarStyle();
        bh.consume(style);
    }

    @Benchmark
    public void testGetNodeId(Blackhole bh) {
        NodeId nodeId = scalarNode.getNodeId();
        bh.consume(nodeId);
    }

    @Benchmark
    public void testIsPlain(Blackhole bh) {
        boolean isPlain = scalarNode.isPlain();
        bh.consume(isPlain);
    }

    @Benchmark
    public void testToString(Blackhole bh) {
        String result = scalarNode.toString();
        bh.consume(result);
    }
}
