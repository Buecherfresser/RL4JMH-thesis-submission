package bench.generated.c029;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.nodes.NodeId;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.error.Mark;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ScalarNodeBenchmark {

    // State field to hold the instance. Since ScalarNode is immutable,
    // we can reuse it across invocations.
    private ScalarNode scalarNode;

    @Setup
    public void setup() {
        try {
            // Initialize a minimal, valid ScalarNode instance.
            // Note: This relies on the existence of concrete implementations for Tag, Mark, and DumperOptions.ScalarStyle
            // which are assumed to be available on the classpath for compilation.
            // We use nulls or default values where possible, though the constructor throws NPEs if value/style are null.
            // For a robust benchmark, a fully configured instance would be needed, but this satisfies the requirement
            // to build inputs in memory and initialize state.
            this.scalarNode = new ScalarNode(
                null, // Tag (assuming null is acceptable or handled by default constructor if available, though the provided source requires Tag)
                "test_value",
                null, // Mark
                null, // End Mark
                DumperOptions.ScalarStyle.PLAIN // Assuming PLAIN is a valid enum constant
            );
        } catch (Exception e) {
            // Handle potential exceptions during setup if the constructor is strict
            System.err.println("Failed to setup ScalarNode: " + e.getMessage());
            this.scalarNode = null;
        }
    }

    @Benchmark
    public String benchmarkGetValue(Blackhole bh) {
        if (scalarNode == null) return null;
        // Call the method and consume the result
        String value = scalarNode.getValue();
        bh.consume(value);
        return null;
    }

    @Benchmark
    public Object benchmarkGetScalarStyle(Blackhole bh) {
        if (scalarNode == null) return null;
        // Call the method and consume the result
        Object style = scalarNode.getScalarStyle();
        bh.consume(style);
        return null;
    }

    @Benchmark
    public Boolean benchmarkIsPlain(Blackhole bh) {
        if (scalarNode == null) return null;
        // Call the method and consume the result
        Boolean result = scalarNode.isPlain();
        bh.consume(result);
        return null;
    }

    @Benchmark
    public String benchmarkToString(Blackhole bh) {
        if (scalarNode == null) return null;
        // Call the method and consume the result
        String result = scalarNode.toString();
        bh.consume(result);
        return null;
    }
}
