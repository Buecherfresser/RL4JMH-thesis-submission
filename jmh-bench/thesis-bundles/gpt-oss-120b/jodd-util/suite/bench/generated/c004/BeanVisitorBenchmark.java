package bench.generated.c004;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import jodd.bean.BeanVisitor;
import java.util.Map;
import java.util.HashMap;
import java.util.function.BiConsumer;

@State(org.openjdk.jmh.annotations.Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class BeanVisitorBenchmark {

    // Simple POJO used as source bean
    public static class SimpleBean {
        private String name;
        private int age;
        private String emptyString;
        private String nullString;
        public int publicFieldNoGetter; // field without getter

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public int getAge() { return age; }
        public void setAge(int age) { this.age = age; }

        public String getEmptyString() { return emptyString; }
        public void setEmptyString(String emptyString) { this.emptyString = emptyString; }

        public String getNullString() { return nullString; }
        public void setNullString(String nullString) { this.nullString = nullString; }
    }

    private BeanVisitor visitorDefault;
    private BeanVisitor visitorDeclared;
    private BeanVisitor visitorIncludeFields;
    private BeanVisitor visitorIgnoreNulls;
    private BeanVisitor visitorIgnoreEmpty;
    private BeanVisitor visitorMap;

    private SimpleBean beanInstance;
    private Map<String, Object> mapInstance;

    @Setup
    public void setup() {
        // Prepare POJO
        beanInstance = new SimpleBean();
        beanInstance.setName("John Doe");
        beanInstance.setAge(42);
        beanInstance.setEmptyString("");
        beanInstance.setNullString(null);
        beanInstance.publicFieldNoGetter = 12345;

        // Prepare Map source
        mapInstance = new HashMap<>();
        mapInstance.put("name", "Jane Doe");
        mapInstance.put("age", 30);
        mapInstance.put("empty", "");
        mapInstance.put("nullValue", null);
        mapInstance.put("extra", "extraValue");

        // Visitors with different configurations
        visitorDefault = new BeanVisitor(beanInstance);
        visitorDeclared = new BeanVisitor(beanInstance).declared(true);
        visitorIncludeFields = new BeanVisitor(beanInstance).includeFields(true);
        visitorIgnoreNulls = new BeanVisitor(beanInstance).ignoreNulls(true);
        visitorIgnoreEmpty = new BeanVisitor(beanInstance).ignoreEmptyString(true);
        visitorMap = new BeanVisitor(mapInstance);
    }

    @Benchmark
    public void visitDefault(Blackhole bh) {
        visitorDefault.visit((name, value) -> {
            bh.consume(name);
            bh.consume(value);
        });
    }

    @Benchmark
    public void visitDeclared(Blackhole bh) {
        visitorDeclared.visit((name, value) -> {
            bh.consume(name);
            bh.consume(value);
        });
    }

    @Benchmark
    public void visitIncludeFields(Blackhole bh) {
        visitorIncludeFields.visit((name, value) -> {
            bh.consume(name);
            bh.consume(value);
        });
    }

    @Benchmark
    public void visitIgnoreNulls(Blackhole bh) {
        visitorIgnoreNulls.visit((name, value) -> {
            bh.consume(name);
            bh.consume(value);
        });
    }

    @Benchmark
    public void visitIgnoreEmptyString(Blackhole bh) {
        visitorIgnoreEmpty.visit((name, value) -> {
            bh.consume(name);
            bh.consume(value);
        });
    }

    @Benchmark
    public void visitMap(Blackhole bh) {
        visitorMap.visit((name, value) -> {
            bh.consume(name);
            bh.consume(value);
        });
    }
}
