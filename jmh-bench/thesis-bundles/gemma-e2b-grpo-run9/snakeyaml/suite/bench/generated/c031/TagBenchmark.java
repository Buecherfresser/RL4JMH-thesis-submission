package bench.generated.c031;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.nodes.Tag;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class TagBenchmark {

    // Since Tag is mostly static constants and stateless instance methods,
    // we don't need complex @Setup state fields, but we need a way to instantiate
    // the class if we test instance methods.

    @Benchmark
    public void testGetValue(Blackhole bh) {
        try {
            // Test a standard static tag instance (if possible, or rely on static access)
            // Since getValue() is an instance method, we must create an instance.
            Tag tag = Tag.YAML;
            bh.consume(tag.getValue());
        } catch (Exception e) {
            // Ignore exceptions for benchmarking stability if they occur during setup/call
        }
    }

    @Benchmark
    public void testIsSecondary(Blackhole bh) {
        try {
            Tag tag = Tag.YAML;
            bh.consume(tag.isSecondary());
        } catch (Exception e) {
            // Ignore exceptions
        }
    }

    @Benchmark
    public void testIsCompatible(Blackhole bh) {
        try {
            // Test compatibility check against a known compatible class (e.g., Double.class)
            Tag tag = new Tag(Double.class);
            bh.consume(tag.isCompatible(Double.class));
        } catch (Exception e) {
            // Ignore exceptions
        }
    }

    @Benchmark
    public void testMatches(Blackhole bh) {
        try {
            // Test matching against a class that should match the global tag prefix
            Tag tag = new Tag(Object.class);
            bh.consume(tag.matches(Object.class));
        } catch (Exception e) {
            // Ignore exceptions
        }
    }

    @Benchmark
    public void testConstructorWithString(Blackhole bh) {
        try {
            // Test constructor that takes a String
            new Tag("test:tag");
            bh.consume(null);
        } catch (Exception e) {
            // Ignore exceptions
        }
    }

    @Benchmark
    public void testConstructorWithClass(Blackhole bh) {
        try {
            // Test constructor that takes a Class
            new Tag(String.class);
            bh.consume(null);
        } catch (Exception e) {
            // Ignore exceptions
        }
    }
}
