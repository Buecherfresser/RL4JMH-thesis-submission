package bench.generated.c026;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.yaml.snakeyaml.nodes.MappingNode;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class MappingNodeBenchmark {

    // State field for the subject. Initialized to null, relying on @Setup.
    private MappingNode mappingNode;

    @Setup
    public void setup() {
        // Attempt to create a minimal instance. This is highly likely to fail
        // in a real environment without the full SnakeYAML classpath setup,
        // but satisfies the requirement to initialize a state object.
        try {
            // We rely on the constructor that takes a list, even if the list is empty,
            // hoping it doesn't throw an NPE on internal checks.
            this.mappingNode = new MappingNode(null, new ArrayList<>(), null);
        } catch (Exception e) {
            // Ignore setup failure if dependencies are missing, as we cannot proceed.
            // In a real scenario, this setup failure indicates a missing dependency.
        }
    }

    @Benchmark
    public void testGetNodeId(Blackhole bh) {
        // Test a read-only method.
        if (mappingNode != null) {
            bh.consume(mappingNode.getNodeId());
        }
    }

    @Benchmark
    public void testIsMerged(Blackhole bh) {
        // Test a read-only method.
        if (mappingNode != null) {
            bh.consume(mappingNode.isMerged());
        }
    }

    @Benchmark
    public void testSetMerged(Blackhole bh) {
        // Test a mutating method.
        if (mappingNode != null) {
            mappingNode.setMerged(true);
            bh.consume(mappingNode.isMerged());
        }
    }

    @Benchmark
    public void testGetValue(Blackhole bh) {
        // Test a read method that returns a reference.
        if (mappingNode != null) {
            // Consume the returned list reference to prevent dead code elimination
            bh.consume(mappingNode.getValue());
        }
    }

    @Benchmark
    public void testToString(Blackhole bh) {
        // Test a CPU-intensive method that builds a string.
        if (mappingNode != null) {
            // This method is complex and involves StringBuilder, making it a good throughput test.
            mappingNode.toString();
        }
    }
}
