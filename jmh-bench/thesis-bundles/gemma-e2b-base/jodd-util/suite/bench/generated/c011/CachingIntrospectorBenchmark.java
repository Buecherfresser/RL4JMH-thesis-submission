package bench.generated.c011;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.Arrays;
import java.util.List;

import jodd.introspector.CachingIntrospector;
import jodd.introspector.ClassDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class CachingIntrospectorBenchmark {

    // State fields for the subject under test and inputs
    private CachingIntrospector defaultIntrospector;
    private CachingIntrospector strictIntrospector;
    private Class<?> testClass;
    private Class<?> anotherClass;

    // Descriptors to be generated/looked up
    private ClassDescriptor descriptor1;
    private ClassDescriptor descriptor2;

    @Setup
    public void setup() {
        // 1. Prepare input classes
        testClass = String.class;
        anotherClass = Integer.class;

        // 2. Initialize Introspector instances with different configurations
        defaultIntrospector = new CachingIntrospector(); // scanAccessible=true, enhancedProperties=true, includeFieldsAsProperties=true, propertyFieldPrefix=null
        strictIntrospector = new CachingIntrospector(true, false, false, new String[]{"_internal"});

        // 3. Pre-calculate descriptors for testing lookup performance
        // We use the default introspector to populate the cache for the first lookup
        descriptor1 = defaultIntrospector.lookup(testClass);
        descriptor2 = defaultIntrospector.lookup(anotherClass);
    }

    @Benchmark
    public void lookupDefaultIntrospector(Blackhole bh) {
        // Test lookup on a class that should be cached (or newly created)
        ClassDescriptor result = defaultIntrospector.lookup(testClass);
        bh.consume(result);
    }

    @Benchmark
    public void lookupStrictIntrospector(Blackhole bh) {
        // Test lookup on a class using a strict configuration
        ClassDescriptor result = strictIntrospector.lookup(testClass);
        bh.consume(result);
    }

    @Benchmark
    public void lookupAnotherClass(Blackhole bh) {
        // Test lookup on a different class
        ClassDescriptor result = defaultIntrospector.lookup(anotherClass);
        bh.consume(result);
    }

    @Benchmark
    public void resetIntrospector(Blackhole bh) {
        // Test the reset functionality
        defaultIntrospector.reset();
        bh.consume(null); // Reset returns void
    }
}
