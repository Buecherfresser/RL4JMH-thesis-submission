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
import jodd.bean.BeanUtil;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class BeanVisitorBenchmark {

    // --- Test Data Setup ---
    private Map<String, Object> testMap;
    private Object testPojo;

    @Setup
    public void setup() {
        // 1. Setup a complex POJO for property walking tests
        testPojo = new Object() {
            public String name = "TestBean";
            public int id = 100;
            public String description = "A detailed description.";
            public String nullField = null;
            public String emptyStringField = "";
            public Object nested = new Object();
        };

        // 2. Setup a Map for testing Map path
        testMap = new HashMap<>();
        testMap.put("key1", "value1");
        testMap.put("key2", 123);
        testMap.put("key3", null);
    }

    // --- Benchmark Methods ---

    /**
     * Benchmark 1: Basic visit with default settings (declared=false, ignoreNulls=false, ignoreEmptyString=false).
     */
    @Benchmark
    public void visit_DefaultConfig(Blackhole bh) {
        BeanVisitor visitor = new BeanVisitor(testPojo);
        visitor.visit((name, value) -> {
            bh.consume(name);
            bh.consume(value);
        });
    }

    /**
     * Benchmark 2: Visit configured to only include declared properties (declared=true).
     */
    @Benchmark
    public void visit_DeclaredProperties(Blackhole bh) {
        BeanVisitor visitor = new BeanVisitor(testPojo).declared(true);
        visitor.visit((name, value) -> {
            bh.consume(name);
            bh.consume(value);
        });
    }

    /**
     * Benchmark 3: Visit configured to ignore null values (ignoreNulls=true).
     */
    @Benchmark
    public void visit_IgnoreNulls(Blackhole bh) {
        BeanVisitor visitor = new BeanVisitor(testPojo).ignoreNulls(true);
        visitor.visit((name, value) -> {
            bh.consume(name);
            bh.consume(value);
        });
    }

    /**
     * Benchmark 4: Visit configured to ignore empty strings (ignoreEmptyString=true).
     */
    @Benchmark
    public void visit_IgnoreEmptyStrings(Blackhole bh) {
        BeanVisitor visitor = new BeanVisitor(testPojo).ignoreEmptyString(true);
        visitor.visit((name, value) -> {
            bh.consume(name);
            bh.consume(value);
        });
    }

    /**
     * Benchmark 5: Visit on a Map structure (testing isSourceMap path).
     */
    @Benchmark
    public void visit_MapSource(Blackhole bh) {
        BeanVisitor visitor = new BeanVisitor(testMap);
        visitor.visit((name, value) -> {
            bh.consume(name);
            bh.consume(value);
        });
    }
}
