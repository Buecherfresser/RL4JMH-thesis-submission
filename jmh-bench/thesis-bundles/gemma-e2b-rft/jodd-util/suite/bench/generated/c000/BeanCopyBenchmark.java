package bench.generated.c000;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.concurrent.TimeUnit;

import jodd.bean.BeanCopy;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class BeanCopyBenchmark {

    // --- Test Data Structures ---

    private static class SimpleBean {
        public String name;
        public int id;
        public String description;
        public boolean active;

        public SimpleBean(String name, int id, String description, boolean active) {
            this.name = name;
            this.id = id;
            this.description = description;
            this.active = active;
        }
    }

    // --- State Fields ---
    private SimpleBean sourceBean;
    private SimpleBean destinationBean;
    private Map<String, Object> sourceMap;
    private SimpleBean destinationPojo;

    // --- Setup ---

    @Setup
    public void setup() {
        // Setup POJO data
        sourceBean = new SimpleBean("Alice", 101, "A test description", true);
        destinationPojo = new SimpleBean("Bob", 202, "Initial description", false);

        // Setup Map data
        sourceMap = new HashMap<>();
        sourceMap.put("name", "Alice");
        sourceMap.put("id", 101);
        sourceMap.put("description", "A test description");
        sourceMap.put("active", true);

        // Setup destination for POJO copy tests
        destinationBean = new SimpleBean("Target", 0, "", false);
    }

    // --- Benchmarks ---

    @Benchmark
    public void copy_default_pojo(Blackhole bh) {
        BeanCopy copy = BeanCopy.from(sourceBean).to(destinationBean);
        copy.copy();
        bh.consume(destinationBean);
    }

    @Benchmark
    public void copy_declared_pojo(Blackhole bh) {
        BeanCopy copy = BeanCopy.from(sourceBean).to(destinationBean).declared(true);
        copy.copy();
        bh.consume(destinationBean);
    }

    @Benchmark
    public void copy_forced_pojo(Blackhole bh) {
        BeanCopy copy = BeanCopy.from(sourceBean).to(destinationBean).forced(true);
        copy.copy();
        bh.consume(destinationBean);
    }

    @Benchmark
    public void copy_include_fields_pojo(Blackhole bh) {
        BeanCopy copy = BeanCopy.from(sourceBean).to(destinationBean).includeFields(true);
        copy.copy();
        bh.consume(destinationBean);
    }

    @Benchmark
    public void copy_map_to_pojo(Blackhole bh) {
        // Test copying from Map to POJO
        BeanCopy copy = BeanCopy.from(sourceMap).to(destinationPojo);
        copy.copy();
        bh.consume(destinationPojo);
    }

    @Benchmark
    public void copy_pojo_to_map(Blackhole bh) {
        // Test copying from POJO to Map
        BeanCopy copy = BeanCopy.from(sourceBean).to(sourceMap);
        copy.copy();
        bh.consume(sourceMap);
    }

    @Benchmark
    public void copy_with_filter_name(Blackhole bh) {
        // Filter: only copy properties where the name starts with 'A'
        Predicate<String> nameFilter = name -> name != null && name.startsWith("A");

        BeanCopy copy = BeanCopy.from(sourceBean).to(destinationBean).filter(nameFilter);
        copy.copy();
        bh.consume(destinationBean);
    }

    @Benchmark
    public void copy_with_filter_and_value(Blackhole bh) {
        // Filter 1: only copy properties where the name is 'id'
        Predicate<String> nameFilter = name -> name.equals("id");

        // Filter 2: only copy if the value is greater than 100
        BiPredicate<String, Object> valueFilter = (name, value) -> {
            if (name.equals("id")) {
                return (Integer) value > 100;
            }
            return true;
        };

        BeanCopy copy = BeanCopy.from(sourceBean).to(destinationBean)
                .filter(nameFilter)
                .filter(valueFilter);
        copy.copy();
        bh.consume(destinationBean);
    }
}
