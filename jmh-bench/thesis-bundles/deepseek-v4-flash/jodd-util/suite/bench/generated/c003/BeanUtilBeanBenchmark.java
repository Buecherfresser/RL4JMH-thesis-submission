package bench.generated.c003;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.*;

import jodd.bean.BeanUtilBean;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class BeanUtilBeanBenchmark {

    // ----------------------------------------------------------------------
    // Test bean classes
    // ----------------------------------------------------------------------

    public static class Address {
        private String street;
        private String city;

        public String getStreet() { return street; }
        public void setStreet(String street) { this.street = street; }
        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }
    }

    public static class SampleBean {
        private String name;
        private int age;
        private Address address;
        private List<String> items;
        private Map<String, String> map;
        private int[] numbers;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public int getAge() { return age; }
        public void setAge(int age) { this.age = age; }
        public Address getAddress() { return address; }
        public void setAddress(Address address) { this.address = address; }
        public List<String> getItems() { return items; }
        public void setItems(List<String> items) { this.items = items; }
        public Map<String, String> getMap() { return map; }
        public void setMap(Map<String, String> map) { this.map = map; }
        public int[] getNumbers() { return numbers; }
        public void setNumbers(int[] numbers) { this.numbers = numbers; }
    }

    // ----------------------------------------------------------------------
    // Benchmark state
    // ----------------------------------------------------------------------

    private BeanUtilBean beanUtil;
    private SampleBean sampleBean;
    private SampleBean[] writeBeans;
    private int writeIndex;

    @Setup(Level.Trial)
    public void setup() {
        beanUtil = new BeanUtilBean();
        beanUtil.declared(false).forced(false).silent(false);

        // Create a fully populated sample bean for read-only operations
        sampleBean = new SampleBean();
        sampleBean.setName("John");
        sampleBean.setAge(30);
        Address addr = new Address();
        addr.setStreet("Main St");
        addr.setCity("Springfield");
        sampleBean.setAddress(addr);
        sampleBean.setItems(new ArrayList<>(Arrays.asList("a", "b", "c")));
        sampleBean.setMap(new HashMap<>());
        sampleBean.getMap().put("key1", "value1");
        sampleBean.getMap().put("key2", "value2");
        sampleBean.setNumbers(new int[]{1, 2, 3, 4, 5});

        // Create a pool of beans for write operations (to avoid reusing the same bean)
        writeBeans = new SampleBean[1000];
        for (int i = 0; i < writeBeans.length; i++) {
            SampleBean b = new SampleBean();
            b.setName("name" + i);
            b.setAge(i);
            Address a = new Address();
            a.setStreet("street" + i);
            a.setCity("city" + i);
            b.setAddress(a);
            b.setItems(new ArrayList<>(Arrays.asList("x", "y", "z")));
            b.setMap(new HashMap<>());
            b.getMap().put("k", "v");
            b.setNumbers(new int[]{i, i+1, i+2});
            writeBeans[i] = b;
        }
        writeIndex = 0;
    }

    private SampleBean nextWriteBean() {
        SampleBean bean = writeBeans[writeIndex];
        writeIndex = (writeIndex + 1) % writeBeans.length;
        return bean;
    }

    // ----------------------------------------------------------------------
    // Benchmarks
    // ----------------------------------------------------------------------

    // Simple getter
    @Benchmark
    public String benchGetSimpleProperty() {
        return beanUtil.getSimpleProperty(sampleBean, "name");
    }

    // Simple setter (mutates a fresh bean)
    @Benchmark
    public void benchSetSimpleProperty(Blackhole bh) {
        SampleBean bean = nextWriteBean();
        beanUtil.setSimpleProperty(bean, "name", "newName");
        bh.consume(bean);
    }

    // Indexed getter (list)
    @Benchmark
    public String benchGetIndexPropertyList() {
        return beanUtil.getIndexProperty(sampleBean, "items", 1);
    }

    // Indexed getter (array)
    @Benchmark
    public int benchGetIndexPropertyArray() {
        return beanUtil.getIndexProperty(sampleBean, "numbers", 2);
    }

    // Indexed setter (list)
    @Benchmark
    public void benchSetIndexPropertyList(Blackhole bh) {
        SampleBean bean = nextWriteBean();
        beanUtil.setIndexProperty(bean, "items", 1, "newItem");
        bh.consume(bean);
    }

    // Nested getter
    @Benchmark
    public String benchGetPropertyNested() {
        return beanUtil.getProperty(sampleBean, "address.street");
    }

    // Nested setter
    @Benchmark
    public void benchSetPropertyNested(Blackhole bh) {
        SampleBean bean = nextWriteBean();
        beanUtil.setProperty(bean, "address.street", "newStreet");
        bh.consume(bean);
    }

    // hasProperty
    @Benchmark
    public boolean benchHasProperty() {
        return beanUtil.hasProperty(sampleBean, "address.street");
    }

    // hasRootProperty
    @Benchmark
    public boolean benchHasRootProperty() {
        return beanUtil.hasRootProperty(sampleBean, "address");
    }

    // getPropertyType
    @Benchmark
    public Class<?> benchGetPropertyType() {
        return beanUtil.getPropertyType(sampleBean, "address.street");
    }

    // extractThisReference
    @Benchmark
    public String benchExtractThisReference() {
        return beanUtil.extractThisReference("address.street");
    }

    // hasSimpleProperty
    @Benchmark
    public boolean benchHasSimpleProperty() {
        return beanUtil.hasSimpleProperty(sampleBean, "name");
    }

    // getProperty with map key
    @Benchmark
    public String benchGetPropertyMap() {
        return beanUtil.getProperty(sampleBean, "map[key1]");
    }

    // setProperty with map key
    @Benchmark
    public void benchSetPropertyMap(Blackhole bh) {
        SampleBean bean = nextWriteBean();
        beanUtil.setProperty(bean, "map[k]", "newValue");
        bh.consume(bean);
    }
}
