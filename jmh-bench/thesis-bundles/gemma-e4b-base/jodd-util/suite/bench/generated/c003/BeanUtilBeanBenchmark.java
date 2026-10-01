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

    private BeanUtilBean beanUtilBean;

    // --- Input POJOs ---

    // Inner class for Address
    private static class Address {
        String street;
        String city;

        public Address(String street, String city) {
            this.street = street;
            this.city = city;
        }
    }

    // Inner class for Item
    private static class Item {
        String name;
        int quantity;

        public Item(String name, int quantity) {
            this.name = name;
            this.quantity = quantity;
        }
    }

    // Inner class for RootBean
    private static class RootBean {
        String simpleField;
        Address address;
        List<Item> items;
        Map<String, String> metadata;

        public RootBean(String simpleField, Address address, List<Item> items, Map<String, String> metadata) {
            this.simpleField = simpleField;
            this.address = address;
            this.items = items;
            this.metadata = metadata;
        }
    }

    // --- State Fields ---

    // Read-only input bean for property access tests
    private RootBean readOnlyRootBean;

    // Pool of mutable input beans for property setting tests
    private List<RootBean> mutableBeanPool;
    private int poolIndex = 0;

    @Setup(Level.Trial)
    public void setup() {
        // Initialize BeanUtilBean (using default configuration)
        beanUtilBean = new BeanUtilBean();

        // 1. Setup Read-Only Input Bean
        Address address = new Address("123 Main St", "Anytown");
        List<Item> items = new ArrayList<>();
        items.add(new Item("Laptop", 1));
        items.add(new Item("Mouse", 2));
        Map<String, String> metadata = new HashMap<>();
        metadata.put("color", "blue");
        metadata.put("size", "large");

        readOnlyRootBean = new RootBean("TestValue", address, items, metadata);

        // 2. Setup Mutable Bean Pool (to avoid state corruption across invocations)
        mutableBeanPool = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            // Renaming variables inside the loop to prevent potential redefinition errors
            Address poolAddress = new Address("Pool St " + i, "Pool City");
            List<Item> poolItems = new ArrayList<>();
            poolItems.add(new Item("Item " + i, i));
            Map<String, String> poolMetadata = new HashMap<>();
            poolMetadata.put("key", "value" + i);
            mutableBeanPool.add(new RootBean("PoolValue" + i, poolAddress, poolItems, poolMetadata));
        }
    }

    /**
     * Helper to get the next bean from the pool, cycling the index.
     */
    private RootBean getNextMutableBean() {
        RootBean bean = mutableBeanPool.get(poolIndex);
        poolIndex = (poolIndex + 1) % mutableBeanPool.size();
        return bean;
    }

    // =================================================================================
    // GET PROPERTY BENCHMARKS
    // =================================================================================

    @Benchmark
    public Object getSimpleProperty_String(Blackhole bh) {
        // Test simple field access
        Object result = beanUtilBean.getProperty(readOnlyRootBean, "simpleField");
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Object getNestedProperty_AddressStreet(Blackhole bh) {
        // Test nested field access (address.street)
        Object result = beanUtilBean.getProperty(readOnlyRootBean, "address.street");
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Object getIndexedProperty_Items2(Blackhole bh) {
        // Test indexed list access (items[1])
        Object result = beanUtilBean.getProperty(readOnlyRootBean, "items[1]");
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Object getIndexedProperty_MetadataKey(Blackhole bh) {
        // Test indexed map access (metadata['color'])
        Object result = beanUtilBean.getProperty(readOnlyRootBean, "metadata['color']");
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Object getDeeplyNestedProperty_Items1Name(Blackhole bh) {
        // Test deep nested indexed access (items[1].name)
        Object result = beanUtilBean.getProperty(readOnlyRootBean, "items[1].name");
        bh.consume(result);
        return result;
    }

    // =================================================================================
    // SET PROPERTY BENCHMARKS
    // =================================================================================

    @Benchmark
    public void setSimpleProperty_String(Blackhole bh) {
        // Test setting a simple field
        RootBean bean = getNextMutableBean();
        beanUtilBean.setProperty(bean, "simpleField", "NewValue");
        bh.consume(bean.simpleField);
    }

    @Benchmark
    public void setNestedProperty_AddressCity(Blackhole bh) {
        // Test setting a nested field
        RootBean bean = getNextMutableBean();
        beanUtilBean.setProperty(bean, "address.city", "NewCity");
        bh.consume(bean.address.city);
    }

    @Benchmark
    public void setIndexedProperty_Items0Quantity(Blackhole bh) {
        // Test setting an indexed list element (items[0].quantity)
        RootBean bean = getNextMutableBean();
        beanUtilBean.setProperty(bean, "items[0].quantity", 99);
        bh.consume(bean.items.get(0).quantity);
    }

    @Benchmark
    public void setIndexedProperty_MetadataKey(Blackhole bh) {
        // Test setting a map element (metadata['newKey'])
        RootBean bean = getNextMutableBean();
        beanUtilBean.setProperty(bean, "metadata['newKey']", "newValue");
        bh.consume(bean.metadata.get("newKey"));
    }

    // =================================================================================
    // HAS PROPERTY BENCHMARKS
    // =================================================================================

    @Benchmark
    public boolean hasSimpleProperty_Exists(Blackhole bh) {
        // Test simple field existence
        boolean result = beanUtilBean.hasProperty(readOnlyRootBean, "simpleField");
        bh.consume(result);
        return result;
    }

    @Benchmark
    public boolean hasNestedProperty_Exists(Blackhole bh) {
        // Test nested field existence
        boolean result = beanUtilBean.hasProperty(readOnlyRootBean, "address.street");
        bh.consume(result);
        return result;
    }

    @Benchmark
    public boolean hasIndexedProperty_Exists(Blackhole bh) {
        // Test indexed list existence
        boolean result = beanUtilBean.hasProperty(readOnlyRootBean, "items[5]");
        bh.consume(result);
        return result;
    }

    @Benchmark
    public boolean hasRootProperty_Exists(Blackhole bh) {
        // Test root property existence (checking only the top level)
        boolean result = beanUtilBean.hasRootProperty(readOnlyRootBean, "address");
        bh.consume(result);
        return result;
    }

    // =================================================================================
    // GET PROPERTY TYPE BENCHMARKS
    // =================================================================================

    @Benchmark
    public Class<?> getPropertyType_SimpleField(Blackhole bh) {
        // Test simple field type
        Class<?> result = beanUtilBean.getPropertyType(readOnlyRootBean, "simpleField");
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Class<?> getPropertyType_NestedField(Blackhole bh) {
        // Test nested field type
        Class<?> result = beanUtilBean.getPropertyType(readOnlyRootBean, "address.street");
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Class<?> getPropertyType_IndexedField(Blackhole bh) {
        // Test indexed field type
        Class<?> result = beanUtilBean.getPropertyType(readOnlyRootBean, "items[0]");
        bh.consume(result);
        return result;
    }
}
