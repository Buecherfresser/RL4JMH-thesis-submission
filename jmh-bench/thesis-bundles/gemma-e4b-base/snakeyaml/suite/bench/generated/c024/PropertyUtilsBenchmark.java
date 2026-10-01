package bench.generated.c024;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.Set;
import org.yaml.snakeyaml.introspector.PropertyUtils;
import org.yaml.snakeyaml.introspector.BeanAccess;
import org.yaml.snakeyaml.introspector.Property;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class PropertyUtilsBenchmark {

    private PropertyUtils propertyUtils;
    private Class<?> testPojoClass;
    private String testPropertyName;

    // Simple POJO for introspection testing
    public static class TestPojo {
        private String field1;
        public int field2;
        protected String protectedField;
        private static final int CONSTANT = 10;
        public String getField1() { return field1; }
        public void setField1(String field1) { this.field1 = field1; }
    }

    @Setup(Level.Trial)
    public void setup() {
        // Initialize SUT. We use the default constructor which relies on PlatformFeatureDetector.
        propertyUtils = new PropertyUtils();
        testPojoClass = TestPojo.class;
        testPropertyName = "field1";
    }

    /**
     * Benchmarks retrieving the set of properties using BeanAccess.FIELD.
     * This forces reflection traversal and caching.
     */
    @Benchmark
    public Set<Property> benchmarkGetPropertiesFieldAccess(Blackhole bh) {
        Set<Property> properties = propertyUtils.getProperties(testPojoClass, BeanAccess.FIELD);
        bh.consume(properties);
        return properties;
    }

    /**
     * Benchmarks retrieving the set of properties using the default BeanAccess.
     * This relies on the internal default setting (usually FIELD or PROPERTY).
     */
    @Benchmark
    public Set<Property> benchmarkGetPropertiesDefaultAccess(Blackhole bh) {
        Set<Property> properties = propertyUtils.getProperties(testPojoClass);
        bh.consume(properties);
        return properties;
    }

    /**
     * Benchmarks retrieving a specific property using BeanAccess.FIELD.
     * This tests the lookup mechanism after the properties map is cached.
     */
    @Benchmark
    public Property benchmarkGetPropertyFieldAccess(Blackhole bh) {
        Property property = propertyUtils.getProperty(testPojoClass, testPropertyName, BeanAccess.FIELD);
        bh.consume(property);
        return property;
    }

    /**
     * Benchmarks retrieving a specific property using the default BeanAccess.
     */
    @Benchmark
    public Property benchmarkGetPropertyDefaultAccess(Blackhole bh) {
        Property property = propertyUtils.getProperty(testPojoClass, testPropertyName);
        bh.consume(property);
        return property;
    }

    /**
     * Benchmarks the state mutation of BeanAccess, which forces cache clearing.
     * This measures the overhead of invalidating the internal state.
     */
    @Benchmark
    public void benchmarkSetBeanAccess(Blackhole bh) {
        // Change access mode and observe cache clearing/reinitialization overhead
        propertyUtils.setBeanAccess(BeanAccess.PROPERTY);
        bh.consume(true);
    }

    /**
     * Benchmarks the state mutation of allowReadOnlyProperties, which forces cache clearing.
     */
    @Benchmark
    public void benchmarkSetAllowReadOnlyProperties(Blackhole bh) {
        // Toggle the flag and observe cache clearing overhead
        propertyUtils.setAllowReadOnlyProperties(!propertyUtils.isAllowReadOnlyProperties());
        bh.consume(true);
    }

    /**
     * Benchmarks the state mutation of skipMissingProperties, which forces cache clearing.
     */
    @Benchmark
    public void benchmarkSetSkipMissingProperties(Blackhole bh) {
        // Toggle the flag and observe cache clearing overhead
        propertyUtils.setSkipMissingProperties(!propertyUtils.isSkipMissingProperties());
        bh.consume(true);
    }
}
