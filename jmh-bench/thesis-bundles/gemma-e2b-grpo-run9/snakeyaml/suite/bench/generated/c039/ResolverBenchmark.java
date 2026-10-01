package bench.generated.c039;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
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

    @Setup
    public void setup() {
        // Initialize the resolver. This calls addImplicitResolvers(), setting up the internal state.
        this.resolver = new Resolver();
    }

    @Benchmark
    public void resolveScalar_True(Blackhole bh) {
        // Test resolution for a scalar value, assuming implicit resolution is enabled (true).
        // We use an empty string to test the null resolver path if possible, or a simple value.
        try {
            bh.consume(resolver.resolve(NodeId.scalar, "", true));
        } catch (Exception e) {
            // Ignore exceptions for benchmarking purposes if they occur during setup/initialization
        }
    }

    @Benchmark
    public void resolveScalar_False(Blackhole bh) {
        // Test resolution for a scalar value, assuming implicit resolution is disabled (false).
        try {
            bh.consume(resolver.resolve(NodeId.scalar, "some_value", false));
        } catch (Exception e) {
            // Ignore exceptions
        }
    }

    @Benchmark
    public void resolveScalar_MatchBool(Blackhole bh) {
        // Test a value that should match the BOOL pattern (implicit=true)
        try {
            bh.consume(resolver.resolve(NodeId.scalar, "Yes", true));
        } catch (Exception e) {
            // Ignore exceptions
        }
    }

    @Benchmark
    public void resolveScalar_NoMatch(Blackhole bh) {
        // Test a value that should not match any implicit pattern
        try {
            bh.consume(resolver.resolve(NodeId.scalar, "invalid_value", true));
        } catch (Exception e) {
            // Ignore exceptions
        }
    }

    @Benchmark
    public void resolveScalar_Empty(Blackhole bh) {
        // Test an empty string, which might trigger the null resolver path
        try {
            bh.consume(resolver.resolve(NodeId.scalar, "", true));
        } catch (Exception e) {
            // Ignore exceptions
        }
    }
}
