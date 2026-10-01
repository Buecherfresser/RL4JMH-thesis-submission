package bench.generated.c030;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.nodes.CollectionNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.SequenceNode;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.error.Mark;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class SequenceNodeBenchmark {

    private SequenceNode sequenceNode;
    private List<Node> inputNodes;
    private Class<? extends Object> listType;

    @Setup(Level.Trial)
    public void setup() {
        // 1. Setup dependencies
        Tag sequenceTag = new Tag("!!sequence");
        DumperOptions.FlowStyle flowStyle = DumperOptions.FlowStyle.BLOCK;
        
        // Fix Mark initialization: Mark requires multiple arguments
        Mark startMark = new Mark("dummy", 0, 0, 0, new char[0], 0);
        Mark endMark = new Mark("dummy", 0, 0, 0, new char[0], 0);

        // 2. Create input nodes (e.g., 10 scalar nodes)
        inputNodes = new ArrayList<>(10);
        for (int i = 0; i < 10; i++) {
            Tag scalarTag = new Tag("!!scalar");
            // FIX: ScalarNode requires Tag, Value, StartMark, EndMark, and ScalarStyle
            inputNodes.add(new ScalarNode(scalarTag, "value" + i, startMark, endMark, DumperOptions.ScalarStyle.PLAIN));
        }

        // 3. Create the SequenceNode subject
        // Using the constructor that accepts all parameters
        sequenceNode = new SequenceNode(sequenceTag, true, inputNodes, startMark, endMark, flowStyle);

        // 4. Setup type for mutation benchmark
        listType = String.class;
    }

    /**
     * Benchmarks the retrieval of the internal list of nodes.
     * This is a read-only operation.
     */
    @Benchmark
    public List<Node> testGetValue() {
        return sequenceNode.getValue();
    }

    /**
     * Benchmarks the generation of the string representation of the sequence.
     * This is a read-only operation.
     */
    @Benchmark
    public String testToString() {
        return sequenceNode.toString();
    }

    /**
     * Benchmarks the mutation of the type of all contained nodes.
     * This is a mutating operation.
     */
    @Benchmark
    public void testSetListType(Blackhole bh) {
        sequenceNode.setListType(listType);
        bh.consume(sequenceNode);
    }
}
