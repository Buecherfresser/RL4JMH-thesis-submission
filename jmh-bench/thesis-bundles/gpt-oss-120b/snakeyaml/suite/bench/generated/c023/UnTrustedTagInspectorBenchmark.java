package bench.generated.c023;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.annotations.Scope;
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
    private Tag simpleTag;
    private Tag complexTag;

    @Setup
    public void setUp() {
        inspector = new UnTrustedTagInspector();
        // Tags are created once per trial; they are immutable and safe to reuse.
        simpleTag = new Tag("!simple");
        complexTag = new Tag("!com.example.ComplexTag");
    }

    @Benchmark
    public boolean isGlobalTagAllowedSimple() {
        return inspector.isGlobalTagAllowed(simpleTag);
    }

    @Benchmark
    public boolean isGlobalTagAllowedComplex() {
        return inspector.isGlobalTagAllowed(complexTag);
    }

    // Demonstrate usage with Blackhole to avoid dead‑code elimination if desired.
    @Benchmark
    public void isGlobalTagAllowedConsume(Blackhole bh) {
        bh.consume(inspector.isGlobalTagAllowed(simpleTag));
    }
}
