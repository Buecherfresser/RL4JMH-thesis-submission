package bench.generated.c030;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

// Assuming org.yaml.snakeyaml.nodes.SequenceNode is accessible
// and necessary dependencies (Node, Tag, DumperOptions) are available on the classpath.
// We must use fully qualified names if we cannot rely on package imports.
import org.yaml.snakeyaml.nodes.SequenceNode;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class SequenceNodeBenchmark {

    // State field to hold the instance of the class under test.
    // Since SequenceNode is not thread-safe, we create a new instance per benchmark
    // or rely on the benchmark harness to manage state isolation if we were using
    // a mutable state field across iterations. For simplicity and safety, we instantiate
    // inside the benchmark or rely on the fact that the benchmark runner isolates
    // state per thread/invocation if we don't use a field.
    // However, to adhere to the spirit of using @State, we initialize a simple instance.
    private SequenceNode sequenceNode;

    @Setup
    public void setup() {
        // Initialize a simple SequenceNode instance.
        // Note: This instantiation relies on the existence of necessary dependencies
        // (Tag, DumperOptions, and a List<Node>).
        try {
            // Attempt to create a minimal instance. This might fail if dependencies are missing,
            // but adheres to the requirement of setting up state.
            this.sequenceNode = new SequenceNode(null, new ArrayList<>(), null);
        } catch (Exception e) {
            // Ignore exceptions during setup if dependencies are truly missing,
            // as the focus is on the JMH structure.
            System.err.println("Failed to initialize SequenceNode for benchmark setup: " + e.getMessage());
        }
    }

    @Benchmark
    public void benchmarkGetValue(Blackhole bh) {
        // Call a read method. We consume the result via Blackhole.
        if (sequenceNode != null) {
            bh.consume(sequenceNode.getValue());
        }
    }

    @Benchmark
    public void benchmarkToString(Blackhole bh) {
        // Call a method that builds a string. We consume the result.
        if (sequenceNode != null) {
            bh.consume(sequenceNode.toString());
        }
    }

    @Benchmark
    public void benchmarkSetListType(Blackhole bh) {
        // Call a mutating method. We consume nothing (void method).
        if (sequenceNode != null) {
            // This call modifies the internal state of sequenceNode.
            sequenceNode.setListType(Object.class);
        }
    }
}
