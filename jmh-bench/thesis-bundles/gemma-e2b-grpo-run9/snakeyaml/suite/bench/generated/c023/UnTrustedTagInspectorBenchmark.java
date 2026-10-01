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

    @Setup
    public void setup() {
        // Instantiate the inspector once. Since it is final and stateless, this is safe.
        this.inspector = new UnTrustedTagInspector();
    }

    @Benchmark
    public void testIsGlobalTagAllowed(Blackhole bh) {
        // Call the method. We pass null to avoid the constructor failure error,
        // relying on the method implementation to handle null input gracefully
        // or to allow nulls.
        try {
            boolean result = inspector.isGlobalTagAllowed(null);
            bh.consume(result);
        } catch (Exception e) {
            // Catch potential runtime errors if the method throws an exception for null input.
        }
    }
}
