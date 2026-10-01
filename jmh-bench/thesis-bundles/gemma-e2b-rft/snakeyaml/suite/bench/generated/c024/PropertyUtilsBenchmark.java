package bench.generated.c024;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.introspector.BeanAccess;
import org.yaml.snakeyaml.introspector.PropertyUtils;
import org.yaml.snakeyaml.error.YAMLException;

/**
 * Benchmark for PropertyUtils introspection methods.
 */
@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class PropertyUtilsBenchmark {

    // Define the missing TestPojo class required by the benchmark
    public static class TestPojo {
        public String publicField;
        public int intField;
        public String nameField;
        public boolean readOnlyField;

        public TestPojo(String publicField, int intField, String nameField, boolean readOnlyField) {
            this.publicField = publicField;
            this.intField = intField;
            this.nameField = nameField;
            this.readOnlyField = readOnlyField;
        }
    }

    private PropertyUtils propertyUtils;
    private TestPojo testPojo;
    private final Class<TestPojo> pojoClass = TestPojo.class;

    @Setup
    public void setup() {
        // Initialize PropertyUtils.
        this.propertyUtils = new PropertyUtils();

        // Create a fixed input object for testing
        this.testPojo = new TestPojo("PublicValue", 123, "TestName", false);
    }

    // --- Benchmarks for getProperties(Class) ---

    @Benchmark
    public void getProperties_DefaultAccess(Blackhole bh) {
        // Tests the default BeanAccess
        Set<Property> properties = propertyUtils.getProperties(pojoClass);
        bh.consume(properties);
    }

    // --- Benchmarks for getProperties(Class, BeanAccess) ---

    @Benchmark
    public void getProperties_FieldAccess(Blackhole bh) {
        // Explicitly test BeanAccess.FIELD
        Set<Property> properties = propertyUtils.getProperties(pojoClass, BeanAccess.FIELD);
        bh.consume(properties);
    }

    @Benchmark
    public void getProperties_DefaultAccess_Explicit(Blackhole bh) {
        // Explicitly test default access
        Set<Property> properties = propertyUtils.getProperties(pojoClass, BeanAccess.DEFAULT);
        bh.consume(properties);
    }

    // --- Benchmarks for getProperty(Class, String) ---

    @Benchmark
    public void getProperty_DefaultAccess(Blackhole bh) {
        // Tests retrieval of a property using default access
        Property prop = propertyUtils.getProperty(pojoClass, "publicField");
        bh.consume(prop);
    }

    @Benchmark
    public void getProperty_FieldAccess(Blackhole bh) {
        // Tests retrieval of a property using FIELD access
        Property prop = propertyUtils.getProperty(pojoClass, "privateField", BeanAccess.FIELD);
        bh.consume(prop);
    }

    @Benchmark
    public void getProperty_MissingProperty_Skipped(Blackhole bh) {
        // Test case where a property does not exist, and skipMissingProperties is set to true
        propertyUtils.setSkipMissingProperties(true);
        Property prop = propertyUtils.getProperty(pojoClass, "nonExistentProperty", BeanAccess.DEFAULT);
        bh.consume(prop);
    }

    // --- Benchmarks for Configuration/State Modification ---

    @Benchmark
    public void setBeanAccess_SwitchToField(Blackhole bh) {
        // This tests the state change and cache clearing mechanism
        propertyUtils.setBeanAccess(BeanAccess.FIELD);
        bh.consume(propertyUtils);
    }

    @Benchmark
    public void setAllowReadOnlyProperties_Enable(Blackhole bh) {
        // This tests the state change and cache clearing mechanism
        propertyUtils.setAllowReadOnlyProperties(true);
        bh.consume(propertyUtils);
    }

    @Benchmark
    public void setSkipMissingProperties_Enable(Blackhole bh) {
        // This tests the state change and cache clearing mechanism
        propertyUtils.setSkipMissingProperties(true);
        bh.consume(propertyUtils);
    }
}
