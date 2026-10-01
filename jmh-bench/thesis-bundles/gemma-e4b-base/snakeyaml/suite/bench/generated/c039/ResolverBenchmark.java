package bench.generated.c039;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.resolver.Resolver;
import org.yaml.snakeyaml.nodes.NodeId;
import org.yaml.snakeyaml.nodes.Tag;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ResolverBenchmark {

    private Resolver resolver;

    // Inputs for testing implicit scalar resolution
    private String boolTrueValue;
    private String intValue;
    private String floatValue;
    private String nullValue;
    private String unknownScalarValue;

    // Inputs for testing non-scalar kinds
    private NodeId scalarKind;
    private NodeId sequenceKind;

    @Setup(Level.Trial)
    public void setup() {
        // Initialize the Resolver, which sets up internal implicit resolvers
        resolver = new Resolver();

        // Prepare input strings
        boolTrueValue = "True";
        intValue = "12345";
        floatValue = "3.14159";
        nullValue = "null";
        unknownScalarValue = "some_random_string";

        // Prepare NodeId inputs
        scalarKind = NodeId.scalar;
        sequenceKind = NodeId.sequence;
    }

    @Benchmark
    public Tag testResolve_Scalar_Implicit_Boolean(Blackhole bh) {
        Tag result = resolver.resolve(scalarKind, boolTrueValue, true);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Tag testResolve_Scalar_Implicit_Integer(Blackhole bh) {
        Tag result = resolver.resolve(scalarKind, intValue, true);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Tag testResolve_Scalar_Implicit_Float(Blackhole bh) {
        Tag result = resolver.resolve(scalarKind, floatValue, true);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Tag testResolve_Scalar_Implicit_Null(Blackhole bh) {
        Tag result = resolver.resolve(scalarKind, nullValue, true);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Tag testResolve_Scalar_Implicit_Unknown(Blackhole bh) {
        // Should fall through and return Tag.STR
        Tag result = resolver.resolve(scalarKind, unknownScalarValue, true);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Tag testResolve_Scalar_Explicit(Blackhole bh) {
        // Explicit resolution should always return Tag.STR regardless of content
        Tag result = resolver.resolve(scalarKind, boolTrueValue, false);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Tag testResolve_SequenceKind(Blackhole bh) {
        Tag result = resolver.resolve(sequenceKind, "any_value", true);
        bh.consume(result);
        return result;
    }
}
