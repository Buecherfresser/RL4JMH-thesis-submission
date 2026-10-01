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
    private Tag standardTag;
    private Tag customTag;

    @Setup(Level.Trial)
    public void setUp() {
        inspector = new UnTrustedTagInspector();
        standardTag = new Tag("tag:yaml.org,2002:str");
        customTag = new Tag("tag:example.com,2024:custom");
    }

    @Benchmark
    public boolean isGlobalTagAllowedStandard() {
        return inspector.isGlobalTagAllowed(standardTag);
    }

    @Benchmark
    public boolean isGlobalTagAllowedCustom() {
        return inspector.isGlobalTagAllowed(customTag);
    }

    @Benchmark
    public boolean isGlobalTagAllowedWithBlackhole(Blackhole bh) {
        boolean result = inspector.isGlobalTagAllowed(standardTag);
        bh.consume(result);
        return result;
    }
}
