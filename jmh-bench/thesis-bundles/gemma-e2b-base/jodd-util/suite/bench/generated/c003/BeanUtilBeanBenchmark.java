package bench.generated.c003;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import jodd.bean.BeanUtilBean;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class BeanUtilBeanBenchmark {

    private BeanUtilBean beanUtil;
    private TestBean testBean;
    private final String SIMPLE_PROP = "simpleName";
    private final String NESTED_PROP = "address.street";
    private final String INDEXED_PROP = "items[0].name";

    private static class TestBean {
        public String simpleName = "TestValue";
        public String address = "MainAddress";
        public String street = "123 Main St";
        public List<String> items = new ArrayList<>();
        public Map<String, Object> mapData = new HashMap<>();
        public int arrayValue = 42;
    }

    @Setup
    public void setup() {
        // 1. Initialize BeanUtilBean with declared properties enabled
        beanUtil = new BeanUtilBean().declared(true);

        // 2. Create a complex test bean structure
        testBean = new TestBean();
        testBean.items.add("Item1");
        testBean.items.add("Item2");
        testBean.mapData.put("key1", "value1");
    }

    @Benchmark
    public void testSimplePropertyGet(Blackhole bh) {
        String result = beanUtil.getProperty(testBean, SIMPLE_PROP);
        bh.consume(result);
    }

    @Benchmark
    public void testSimplePropertySet(Blackhole bh) {
        beanUtil.setSimpleProperty(testBean, SIMPLE_PROP, "NewValue");
        bh.consume(testBean.simpleName);
    }

    @Benchmark
    public void testNestedPropertyGet(Blackhole bh) {
        String result = beanUtil.getProperty(testBean, NESTED_PROP);
        bh.consume(result);
    }

    @Benchmark
    public void testNestedPropertySet(Blackhole bh) {
        beanUtil.setProperty(testBean, NESTED_PROP, "NewStreet");
        bh.consume(testBean.address + "." + testBean.street);
    }

    @Benchmark
    public void testIndexedPropertyGet(Blackhole bh) {
        Object result = beanUtil.getIndexProperty(testBean, INDEXED_PROP, 0);
        bh.consume(result);
    }

    @Benchmark
    public void testIndexedPropertySet(Blackhole bh) {
        beanUtil.setIndexProperty(testBean, INDEXED_PROP, 1, "Item2");
        bh.consume(testBean.items.get(1));
    }

    @Benchmark
    public void testIndexedPropertyGetOutOfBounds(Blackhole bh) {
        // Test case where index is out of bounds (should throw or handle based on implementation)
        Object result = beanUtil.getIndexProperty(testBean, "items[1]", 1);
        bh.consume(result);
    }

    @Benchmark
    public void testHasPropertySimple(Blackhole bh) {
        boolean has = beanUtil.hasProperty(testBean, SIMPLE_PROP);
        bh.consume(has);
    }

    @Benchmark
    public void testHasPropertyNested(Blackhole bh) {
        boolean has = beanUtil.hasProperty(testBean, NESTED_PROP);
        bh.consume(has);
    }

    @Benchmark
    public void testHasRootProperty(Blackhole bh) {
        boolean hasRoot = beanUtil.hasRootProperty(testBean, SIMPLE_PROP);
        bh.consume(hasRoot);
    }

    @Benchmark
    public void testPropertyType(Blackhole bh) {
        Class<?> type = beanUtil.getPropertyType(testBean, SIMPLE_PROP);
        bh.consume(type);
    }
}
