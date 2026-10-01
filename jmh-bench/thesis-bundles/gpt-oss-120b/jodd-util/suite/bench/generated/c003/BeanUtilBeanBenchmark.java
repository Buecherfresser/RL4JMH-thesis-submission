package bench.generated.c003;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import jodd.bean.BeanUtilBean;
import java.util.*;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class BeanUtilBeanBenchmark {

    private BeanUtilBean beanUtil;
    private Person bean;

    @Setup(Level.Trial)
    public void setup() {
        beanUtil = new BeanUtilBean();

        bean = new Person();
        bean.setName("Alice");
        bean.setAge(30);

        Address address = new Address();
        address.setStreet("Main St");
        address.setCity("Metropolis");
        bean.setAddress(address);

        bean.setNumbers(new int[]{10, 20, 30, 40});

        List<String> tags = new ArrayList<>(Arrays.asList("tag1", "tag2", "tag3"));
        bean.setTags(tags);

        Map<String, String> attributes = new HashMap<>();
        attributes.put("key", "value");
        bean.setAttributes(attributes);
    }

    // --------------------------------------------------------------------
    // getProperty benchmarks

    @Benchmark
    public Object getSimpleProperty() {
        return beanUtil.getProperty(bean, "name");
    }

    @Benchmark
    public Object getNestedProperty() {
        return beanUtil.getProperty(bean, "address.street");
    }

    @Benchmark
    public Object getArrayIndexProperty() {
        return beanUtil.getProperty(bean, "numbers[2]");
    }

    @Benchmark
    public Object getListIndexProperty() {
        return beanUtil.getProperty(bean, "tags[1]");
    }

    @Benchmark
    public Object getMapKeyProperty() {
        return beanUtil.getProperty(bean, "attributes[key]");
    }

    // --------------------------------------------------------------------
    // setProperty benchmarks

    @Benchmark
    public void setSimpleProperty(Blackhole bh) {
        beanUtil.setProperty(bean, "name", "Bob");
        bh.consume(bean.getName());
    }

    @Benchmark
    public void setNestedProperty(Blackhole bh) {
        beanUtil.setProperty(bean, "address.street", "Second St");
        bh.consume(bean.getAddress().getStreet());
    }

    @Benchmark
    public void setArrayIndexProperty(Blackhole bh) {
        beanUtil.setProperty(bean, "numbers[0]", 99);
        bh.consume(bean.getNumbers()[0]);
    }

    @Benchmark
    public void setListIndexProperty(Blackhole bh) {
        beanUtil.setProperty(bean, "tags[0]", "newTag");
        bh.consume(bean.getTags().get(0));
    }

    @Benchmark
    public void setMapKeyProperty(Blackhole bh) {
        beanUtil.setProperty(bean, "attributes[newKey]", "newValue");
        bh.consume(bean.getAttributes().get("newKey"));
    }

    // --------------------------------------------------------------------
    // hasProperty benchmarks

    @Benchmark
    public boolean hasProperty() {
        return beanUtil.hasProperty(bean, "address.street");
    }

    @Benchmark
    public boolean hasRootProperty() {
        return beanUtil.hasRootProperty(bean, "address.street");
    }

    // --------------------------------------------------------------------
    // getPropertyType benchmark

    @Benchmark
    public Class<?> getPropertyType() {
        return beanUtil.getPropertyType(bean, "numbers");
    }

    // --------------------------------------------------------------------
    // Helper POJOs

    public static class Person {
        private String name;
        private int age;
        private Address address;
        private int[] numbers;
        private List<String> tags;
        private Map<String, String> attributes;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public int getAge() { return age; }
        public void setAge(int age) { this.age = age; }

        public Address getAddress() { return address; }
        public void setAddress(Address address) { this.address = address; }

        public int[] getNumbers() { return numbers; }
        public void setNumbers(int[] numbers) { this.numbers = numbers; }

        public List<String> getTags() { return tags; }
        public void setTags(List<String> tags) { this.tags = tags; }

        public Map<String, String> getAttributes() { return attributes; }
        public void setAttributes(Map<String, String> attributes) { this.attributes = attributes; }
    }

    public static class Address {
        private String street;
        private String city;

        public String getStreet() { return street; }
        public void setStreet(String street) { this.street = street; }

        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }
    }
}
