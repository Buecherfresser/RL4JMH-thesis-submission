package bench.generated.c023;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.inspector.UnTrustedTagInspector;

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
        // Create a representative Tag object for testing
        this.testTag = new Tag("test_tag_name");
    }

    @Benchmark
    public void testIsGlobalTagAllowed(Blackhole bh) {
        boolean result = inspector.isGlobalTagAllowed(testTag);
        bh.consume(result);
    }
}
