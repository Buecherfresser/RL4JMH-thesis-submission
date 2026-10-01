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

    @Setup(Level.Trial)
    public void setup() {
        inspector = new UnTrustedTagInspector();
        // Fix: Tag requires a constructor argument (String or Class), not a no-arg constructor.
        // We use a representative string tag.
        testTag = new Tag("!!testTag"); 
    }

    @Benchmark
    public boolean testIsGlobalTagAllowed() {
        boolean result = inspector.isGlobalTagAllowed(testTag);
        // Consume the result to prevent dead code elimination
        return result;
    }
}
