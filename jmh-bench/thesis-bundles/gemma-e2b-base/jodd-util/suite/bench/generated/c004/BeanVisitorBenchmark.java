package bench.generated.c004;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.concurrent.TimeUnit;

import jodd.bean.BeanVisitor;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class BeanVisitorBenchmark {

    // --- Test Data Setup ---
    private Object samplePojo;
    private Map<String, Object> sampleMap;
    private BiConsumer<String, Object> dummyConsumer;

    // --- Benchmark State ---
    private BeanVisitor visitor;

    @Setup
    public void setup() {
        // 1. Setup Sample POJO
        // A simple class structure to test property resolution
        class TestBean {
            public String name = "TestName";
            public int id = 100;
            public String description = "A test description";
            public String nullField = null;
            public String emptyField = "";
            public String getterName() { return name; }
            public int getId() { return id; }
            public String getDescription() { return description; }
        }
        samplePojo = new TestBean();

        // 2. Setup Sample Map
        Map<String, Object> map = new HashMap<>();
        map.put("key1", "value1");
        map.put("key2", 123);
        map.put("key3", null);
        map.put("key4", "");
        sampleMap = map;

        // 3. Setup Dummy Consumer (to consume results from visit)
        dummyConsumer = (name, value) -> {
            // Consume the result to prevent dead code elimination
            if (name != null) {
                // Simple operation to ensure the value is used
                if (value instanceof String) {
                    // Simulate some work if needed, but keep it minimal
                }
            }
        };

        // 4. Setup Visitor instance (using POJO source)
        visitor = new BeanVisitor(samplePojo);
    }

    @Benchmark
    public void visit_DefaultConfig(Blackhole bh) {
        // Test default configuration (declared=false, ignoreNulls=false, ignoreEmptyString=false, includeFields=false)
        visitor.visit(dummyConsumer);
        bh.consume(null); // Consume the result of the visit operation
    }

    @Benchmark
    public void visit_DeclaredConfig(Blackhole bh) {
        // Test configuration: declared=true
        BeanVisitor declaredVisitor = new BeanVisitor(samplePojo).declared(true);
        declaredVisitor.visit(dummyConsumer);
        bh.consume(null);
    }

    @Benchmark
    public void visit_IgnoreNullsConfig(Blackhole bh) {
        // Test configuration: ignoreNulls=true
        BeanVisitor nullIgnoreVisitor = new BeanVisitor(samplePojo).ignoreNulls(true);
        nullIgnoreVisitor.visit(dummyConsumer);
        bh.consume(null);
    }

    @Benchmark
    public void visit_IgnoreEmptyStringConfig(Blackhole bh) {
        // Test configuration: ignoreEmptyString=true
        BeanVisitor emptyStringIgnoreVisitor = new BeanVisitor(samplePojo).ignoreEmptyString(true);
        emptyStringIgnoreVisitor.visit(dummyConsumer);
        bh.consume(null);
    }

    @Benchmark
    public void visit_MapSource(Blackhole bh) {
        // Test visiting a Map source (tests isSourceMap logic)
        BeanVisitor mapVisitor = new BeanVisitor(sampleMap);
        mapVisitor.visit(dummyConsumer);
        bh.consume(null);
    }
}
