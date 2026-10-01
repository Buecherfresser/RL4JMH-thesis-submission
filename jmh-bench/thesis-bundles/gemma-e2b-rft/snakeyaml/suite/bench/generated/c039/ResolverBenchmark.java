package bench.generated.c039;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.nodes.NodeId;
import org.yaml.snakeyaml.nodes.Tag;

import org.yaml.snakeyaml.resolver.Resolver;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ResolverBenchmark {

    private Resolver resolver;

    // Test inputs for scalar resolution (implicit=true)
    private String intValue;
    private String floatValue;
    private String nullValue;
    private String timestampValue;
    private String nonMatchingValue;

    // Test inputs for sequence resolution
    private String sequenceValue;

    @Setup
    public void setup() {
        resolver = new Resolver();
        
        // 1. Populate implicit resolvers (mimicking Resolver constructor logic)
        resolver.addImplicitResolver(Tag.BOOL, Resolver.BOOL, "yYnNtTfFoO", 10);
        resolver.addImplicitResolver(Tag.INT, Resolver.INT, "-+0123456789");
        resolver.addImplicitResolver(Tag.FLOAT, Resolver.FLOAT, "-+0123456789.");
        resolver.addImplicitResolver(Tag.MERGE, Resolver.MERGE, "<", 10);
        resolver.addImplicitResolver(Tag.NULL, Resolver.NULL, "~nN\0", 10);
        resolver.addImplicitResolver(Tag.NULL, Resolver.EMPTY, null, 10);
        resolver.addImplicitResolver(Tag.TIMESTAMP, Resolver.TIMESTAMP, "0123456789", 50);

        // 2. Prepare test data
        this.intValue = "12345";
        this.floatValue = "3.14159";
        this.nullValue = "~";
        this.timestampValue = "2023-10-27T10:00:00Z";
        this.nonMatchingValue = "invalid_string";
        this.sequenceValue = "a, b, c";
    }

    @Benchmark
    public void resolveScalarImplicitMatch(Blackhole bh) {
        Tag resolvedTag = resolver.resolve(NodeId.scalar, intValue, true);
        bh.consume(resolvedTag);
    }

    @Benchmark
    public void resolveScalarImplicitFloatMatch(Blackhole bh) {
        Tag resolvedTag = resolver.resolve(NodeId.scalar, floatValue, true);
        bh.consume(resolvedTag);
    }

    @Benchmark
    public void resolveScalarImplicitNullMatch(Blackhole bh) {
        Tag resolvedTag = resolver.resolve(NodeId.scalar, nullValue, true);
        bh.consume(resolvedTag);
    }

    @Benchmark
    public void resolveScalarImplicitTimestampMatch(Blackhole bh) {
        Tag resolvedTag = resolver.resolve(NodeId.scalar, timestampValue, true);
        bh.consume(resolvedTag);
    }

    @Benchmark
    public void resolveScalarImplicitNoMatch(Blackhole bh) {
        Tag resolvedTag = resolver.resolve(NodeId.scalar, nonMatchingValue, true);
        bh.consume(resolvedTag);
    }

    @Benchmark
    public void resolveScalarExplicit(Blackhole bh) {
        // When implicit is false, it should return Tag.STR regardless of value
        Tag resolvedTag = resolver.resolve(NodeId.scalar, intValue, false);
        bh.consume(resolvedTag);
    }

    @Benchmark
    public void resolveSequence(Blackhole bh) {
        // Test sequence resolution path
        Tag resolvedTag = resolver.resolve(NodeId.sequence, sequenceValue, false);
        bh.consume(resolvedTag);
    }
}
