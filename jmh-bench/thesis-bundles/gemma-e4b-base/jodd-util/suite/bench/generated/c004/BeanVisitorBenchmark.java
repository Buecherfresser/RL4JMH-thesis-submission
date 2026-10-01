package bench.generated.c004;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import jodd.bean.BeanVisitor;

/**
 * JMH benchmark for testing the performance of jodd.bean.BeanVisitor
 * when processing POJOs and Maps.
 */
@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class BeanVisitorBenchmark {

    // Helper class representing the input POJO
    private static class SamplePojo {
        public String publicName = "Public";
        private String privateName = "Private";
        public Integer publicAge = 30;
        private Integer privateAge = 25;
        public List<String> publicList = new ArrayList<>();
        private Map<String, String> privateMap = new HashMap<>();
        public String address = "123 Main St";
        private String internalId = "ID123";
        public String emptyString = "";
        private String nullValue = null;

        public SamplePojo() {
            publicList.add("A");
            publicList.add("B");
            privateMap.put("key1", "value1");
            privateMap.put("key2", "value2");
        }
    }

    private SamplePojo pojoSource;
    private Map<String, Object> mapSource;
    private BiConsumer<String, Object> consumer;

    @Setup(Level.Trial)
    public void setup() {
        // 1. Setup POJO input
        pojoSource = new SamplePojo();

        // 2. Setup Map input
        mapSource = new HashMap<>();
        mapSource.put("name", "MapName");
        mapSource.put("age", 40);
        mapSource.put("address", "MapAddress");
        mapSource.put("list", List.of("X", "Y"));
        mapSource.put("metadata", Map.of("k", "v"));

        // 3. Setup consumer (minimal work)
        consumer = (name, value) -> {};
    }

    // --- POJO Benchmarks ---

    /**
     * Tests default visitor behavior (public properties only, no filtering).
     */
    @Benchmark
    public void visitPojoDefault(Blackhole bh) {
        // Default configuration: declared=false, includeFields=false, ignoreNulls=false, ignoreEmptyString=false
        BeanVisitor visitor = new BeanVisitor(pojoSource);
        visitor.visit(consumer);
        bh.consume(visitor);
    }

    /**
     * Tests visitor configured to look at declared properties only.
     */
    @Benchmark
    public void visitPojoDeclared(Blackhole bh) {
        BeanVisitor visitor = new BeanVisitor(pojoSource);
        visitor.declared(true);
        visitor.visit(consumer);
        bh.consume(visitor);
    }

    /**
     * Tests visitor configured to include fields (non-getter properties).
     */
    @Benchmark
    public void visitPojoIncludeFields(Blackhole bh) {
        BeanVisitor visitor = new BeanVisitor(pojoSource);
        visitor.includeFields(true);
        visitor.visit(consumer);
        bh.consume(visitor);
    }

    /**
     * Tests visitor configured to ignore null values.
     */
    @Benchmark
    public void visitPojoIgnoreNulls(Blackhole bh) {
        BeanVisitor visitor = new BeanVisitor(pojoSource);
        visitor.ignoreNulls(true);
        visitor.visit(consumer);
        bh.consume(visitor);
    }

    /**
     * Tests visitor configured to ignore empty strings.
     */
    @Benchmark
    public void visitPojoIgnoreEmptyString(Blackhole bh) {
        BeanVisitor visitor = new BeanVisitor(pojoSource);
        visitor.ignoreEmptyString(true);
        visitor.visit(consumer);
        bh.consume(visitor);
    }

    /**
     * Tests visitor configured with all filtering enabled (nulls and empty strings).
     */
    @Benchmark
    public void visitPojoAllFilters(Blackhole bh) {
        BeanVisitor visitor = new BeanVisitor(pojoSource);
        visitor.ignoreNulls(true);
        visitor.ignoreEmptyString(true);
        visitor.visit(consumer);
        bh.consume(visitor);
    }

    // --- Map Benchmarks ---

    /**
     * Tests visitor behavior when the source is a Map (keys are properties).
     */
    @Benchmark
    public void visitMapDefault(Blackhole bh) {
        // Map input bypasses reflection/descriptor lookup, relying on Map keys.
        BeanVisitor visitor = new BeanVisitor(mapSource);
        visitor.visit(consumer);
        bh.consume(visitor);
    }

    /**
     * Tests map visitor behavior with null filtering enabled.
     */
    @Benchmark
    public void visitMapIgnoreNulls(Blackhole bh) {
        // Note: Since map keys are strings, null filtering primarily affects values retrieved
        // if the visitor were to access them, but we test the configuration path.
        BeanVisitor visitor = new BeanVisitor(mapSource);
        visitor.ignoreNulls(true);
        visitor.visit(consumer);
        bh.consume(visitor);
    }
}
