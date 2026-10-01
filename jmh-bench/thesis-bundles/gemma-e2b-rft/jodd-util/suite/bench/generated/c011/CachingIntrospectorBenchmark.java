package bench.generated.c011;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

// Assuming the SUT classes (CachingIntrospector, ClassDescriptor, TypeCache) are available on the classpath
// and accessible via their package structure.
import jodd.introspector.CachingIntrospector;
import jodd.introspector.ClassDescriptor;
import jodd.util.TypeCache;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 5, time = 2)
@Measurement(iterations = 5, time = 2)
public class CachingIntrospectorBenchmark {

    // --- State Fields ---

    // Configuration 1: Default settings (scanAccessible=true, enhancedProperties=true, includeFieldsAsProperties=true, prefix=null)
    private CachingIntrospector introspectorDefault;

    // Configuration 2: Restricted settings (scanAccessible=false, enhancedProperties=false, includeFieldsAsProperties=false, prefix=["a"])
    private CachingIntrospector introspectorRestricted;

    // Class to test lookup against (Read-only input)
    private Class<?> testClass1;
    private Class<?> testClass2;

    // --- Setup ---

    @Setup
    public void setup() {
        // 1. Initialize Introspector instances
        introspectorDefault = new CachingIntrospector();
        introspectorRestricted = new CachingIntrospector(false, false, false, new String[]{"a"});

        // 2. Prepare input classes
        testClass1 = String.class;
        testClass2 = Integer.class;
    }

    // --- Benchmarks ---

    /**
     * Benchmark for lookup when the class descriptor is not yet cached (Cache Miss).
     * Tests the path where the introspector must create a new descriptor.
     */
    @Benchmark
    public void lookup_CacheMiss_DefaultConfig(Blackhole bh) {
        ClassDescriptor descriptor = introspectorDefault.lookup(testClass1);
        bh.consume(descriptor);
    }

    /**
     * Benchmark for lookup when the class descriptor is already cached (Cache Hit).
     * Tests the path where the introspector retrieves the existing descriptor.
     */
    @Benchmark
    public void lookup_CacheHit_DefaultConfig(Blackhole bh) {
        // Ensure the descriptor is cached first (this setup is handled by the previous benchmark run,
        // but we call it again to ensure the cache is populated for this specific run context if needed,
        // though JMH usually runs benchmarks independently).
        // We rely on the fact that the previous benchmark populated the cache.
        ClassDescriptor descriptor = introspectorDefault.lookup(testClass1);
        bh.consume(descriptor);
    }

    /**
     * Benchmark for lookup using the restricted configuration.
     */
    @Benchmark
    public void lookup_CacheMiss_RestrictedConfig(Blackhole bh) {
        ClassDescriptor descriptor = introspectorRestricted.lookup(testClass2);
        bh.consume(descriptor);
    }

    /**
     * Benchmark for lookup using the restricted configuration when the descriptor is cached.
     */
    @Benchmark
    public void lookup_CacheHit_RestrictedConfig(Blackhole bh) {
        ClassDescriptor descriptor = introspectorRestricted.lookup(testClass2);
        bh.consume(descriptor);
    }

    /**
     * Benchmark for the reset operation, which clears the internal cache.
     */
    @Benchmark
    public void reset(Blackhole bh) {
        introspectorDefault.reset();
        // Consume nothing, as reset is a void operation.
    }
}
