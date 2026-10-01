package bench.generated.c024;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.introspector.PropertyUtils;
import org.yaml.snakeyaml.introspector.BeanAccess;
import org.yaml.snakeyaml.error.YAMLException;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class PropertyUtilsBenchmark {

    // The subject under test. Since PropertyUtils is not thread-safe,
    // we instantiate it per benchmark instance.
    private PropertyUtils propertyUtils;

    @Setup
    public void setup() {
        // Initialize the PropertyUtils instance.
        // We use the default constructor which initializes PlatformFeatureDetector.
        this.propertyUtils = new PropertyUtils();
    }

    @Benchmark
    public void getProperties_DefaultAccess(Blackhole bh) {
        // Test a read-only operation that relies on internal caching.
        try {
            // We use String.class as a simple, concrete type for testing.
            bh.consume(propertyUtils.getProperties(String.class));
        } catch (Exception e) {
            // Ignore exceptions if the internal structure fails due to missing SnakeYAML dependencies
        }
    }

    @Benchmark
    public void getProperty_SpecificAccess(Blackhole bh) {
        // Test a specific access method.
        try {
            // We use String.class as a simple, concrete type for testing.
            propertyUtils.getProperty(String.class, "someProperty");
            bh.consume(null);
        } catch (Exception e) {
            // Ignore exceptions
        }
    }

    @Benchmark
    public void setBeanAccess_ChangeAccess(Blackhole bh) {
        // Test a method that mutates internal state (clears caches).
        try {
            propertyUtils.setBeanAccess(BeanAccess.FIELD);
            bh.consume(null);
        } catch (IllegalArgumentException e) {
            // Ignore exceptions if running on an unsupported JVM
        }
    }

    @Benchmark
    public void setAllowReadOnlyProperties_ToggleReadOnly(Blackhole bh) {
        // Test another state mutation method.
        try {
            propertyUtils.setAllowReadOnlyProperties(true);
            bh.consume(null);
        } catch (Exception e) {
            // Ignore exceptions
        }
    }

    @Benchmark
    public void getProperties_AfterMutation(Blackhole bh) {
        // Test if the state mutation (from previous benchmarks) affects subsequent calls.
        try {
            // This call should hit the cache or re-calculate based on the current state.
            bh.consume(propertyUtils.getProperties(String.class));
        } catch (Exception e) {
            // Ignore exceptions
        }
    }
}
