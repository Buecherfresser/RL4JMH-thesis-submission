package bench.generated.c039;

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
import org.yaml.snakeyaml.resolver.Resolver;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.nodes.NodeId;
import java.util.regex.Pattern;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ResolverBenchmark {

    private Resolver resolver;

    private String boolScalar;
    private String intScalar;
    private String floatScalar;
    private String nullScalar;
    private String timestampScalar;
    private String mergeScalar;
    private String yamlScalar;
    private String unknownScalar;

    @Setup
    public void setup() {
        resolver = new Resolver();
        boolScalar = "true";
        intScalar = "12345";
        floatScalar = "3.14";
        nullScalar = "~";
        timestampScalar = "2020-01-01";
        mergeScalar = "<<";
        yamlScalar = "!";
        unknownScalar = "someString";
    }

    @Benchmark
    public Tag resolveBool() {
        return resolver.resolve(NodeId.scalar, boolScalar, true);
    }

    @Benchmark
    public Tag resolveInt() {
        return resolver.resolve(NodeId.scalar, intScalar, true);
    }

    @Benchmark
    public Tag resolveFloat() {
        return resolver.resolve(NodeId.scalar, floatScalar, true);
    }

    @Benchmark
    public Tag resolveNull() {
        return resolver.resolve(NodeId.scalar, nullScalar, true);
    }

    @Benchmark
    public Tag resolveTimestamp() {
        return resolver.resolve(NodeId.scalar, timestampScalar, true);
    }

    @Benchmark
    public Tag resolveMerge() {
        return resolver.resolve(NodeId.scalar, mergeScalar, true);
    }

    @Benchmark
    public Tag resolveYaml() {
        return resolver.resolve(NodeId.scalar, yamlScalar, true);
    }

    @Benchmark
    public Tag resolveDefaultString() {
        return resolver.resolve(NodeId.scalar, unknownScalar, true);
    }

    @Benchmark
    public void addImplicitResolver(Blackhole bh) {
        Resolver r = new Resolver();
        Tag customTag = new Tag("!custom");
        Pattern pattern = Pattern.compile("^custom$");
        r.addImplicitResolver(customTag, pattern, "c", 10);
        bh.consume(r);
    }
}
