package bench.generated.c004;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import jodd.bean.BeanVisitor;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class BeanVisitorBenchmark {

    @State(Scope.Benchmark)
    public static class BenchState {
        Object bean;
        Map<String, Object> map;
        BeanVisitor visitorDefault;
        BeanVisitor visitorDeclared;
        BeanVisitor visitorIncludeFields;
        BeanVisitor visitorIgnoreNulls;
        BeanVisitor visitorIgnoreEmptyString;
        BeanVisitor visitorMap;

        @Setup(Level.Trial)
        public void setup() {
            bean = createBean();
            map = createMap();
            visitorDefault = new BeanVisitor(bean);
            visitorDeclared = new BeanVisitor(bean).declared(true);
            visitorIncludeFields = new BeanVisitor(bean).includeFields(true);
            visitorIgnoreNulls = new BeanVisitor(bean).ignoreNulls(true);
            visitorIgnoreEmptyString = new BeanVisitor(bean).ignoreEmptyString(true);
            visitorMap = new BeanVisitor(map);
        }

        private Object createBean() {
            TestBean b = new TestBean();
            b.setName("John");
            b.setAge(30);
            b.setAddress("123 Main St");
            b.setEmail(""); // empty string
            b.setPhone(null); // null
            b.publicField = "extra"; // field without getter
            return b;
        }

        private Map<String, Object> createMap() {
            Map<String, Object> m = new HashMap<>();
            m.put("key1", "value1");
            m.put("key2", 42);
            m.put("key3", null);
            m.put("key4", "");
            return m;
        }
    }

    public static class TestBean {
        private String name;
        private int age;
        private String address;
        private String email;
        private String phone;
        public String publicField;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public int getAge() { return age; }
        public void setAge(int age) { this.age = age; }
        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
    }

    @Benchmark
    public void visitDefault(Blackhole bh, BenchState state) {
        state.visitorDefault.visit((name, value) -> bh.consume(value));
    }

    @Benchmark
    public void visitDeclared(Blackhole bh, BenchState state) {
        state.visitorDeclared.visit((name, value) -> bh.consume(value));
    }

    @Benchmark
    public void visitIncludeFields(Blackhole bh, BenchState state) {
        state.visitorIncludeFields.visit((name, value) -> bh.consume(value));
    }

    @Benchmark
    public void visitIgnoreNulls(Blackhole bh, BenchState state) {
        state.visitorIgnoreNulls.visit((name, value) -> bh.consume(value));
    }

    @Benchmark
    public void visitIgnoreEmptyString(Blackhole bh, BenchState state) {
        state.visitorIgnoreEmptyString.visit((name, value) -> bh.consume(value));
    }

    @Benchmark
    public void visitMap(Blackhole bh, BenchState state) {
        state.visitorMap.visit((name, value) -> bh.consume(value));
    }
}
