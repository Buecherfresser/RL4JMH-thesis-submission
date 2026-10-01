package bench.generated.c023;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.inspector.UnTrustedTagInspector;
import org.yaml.snakeyaml.nodes.Tag;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class UnTrustedTagInspectorBenchmark {

    private UnTrustedTagInspector inspector;
    private Tag testTag;

    @Setup
    public void setup() {
        this.inspector = new UnTrustedTagInspector();
        // Create a representative Tag object for testing.
        // Assuming Tag can be constructed with a String argument based on compilation errors.
        this.testTag = new Tag("test_tag_string");
    }

    @Benchmark
    public void testIsGlobalTagAllowed(Blackhole bh) {
        // Call the method exactly once per invocation
        boolean result = inspector.isGlobalTagAllowed(testTag);
        // Consume the result to prevent dead code elimination
        bh.consume(result);
    }
}
