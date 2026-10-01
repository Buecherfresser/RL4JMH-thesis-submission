package bench.generated.c038;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.*;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class RepresenterBenchmark {

    // Simple bean with a few properties
    public static class MyBean {
        private String name;
        private int age;
        private List<String> tags;

        public MyBean(String name, int age, List<String> tags) {
            this.name = name;
            this.age = age;
            this.tags = tags;
        }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public int getAge() { return age; }
        public void setAge(int age) { this.age = age; }
        public List<String> getTags() { return tags; }
        public void setTags(List<String> tags) { this.tags = tags; }
    }

    // Bean containing a list of other beans (tests generic collection handling)
    public static class MyBeanWithList {
        private List<MyBean> items;

        public MyBeanWithList(List<MyBean> items) { this.items = items; }
        public List<MyBean> getItems() { return items; }
        public void setItems(List<MyBean> items) { this.items = items; }
    }

    // Bean containing a map with generic value type
    public static class MyBeanWithMap {
        private Map<String, MyBean> mapping;

        public MyBeanWithMap(Map<String, MyBean> mapping) { this.mapping = mapping; }
        public Map<String, MyBean> getMapping() { return mapping; }
        public void setMapping(Map<String, MyBean> mapping) { this.mapping = mapping; }
    }

    public enum MyEnum { ONE, TWO, THREE }

    // State fields
    private Representer representer;
    private MyBean simpleBean;
    private List<String> stringList;
    private Map<String, Integer> stringIntMap;
    private MyEnum enumValue;
    private MyBeanWithList beanWithList;
    private MyBeanWithMap beanWithMap;
    private Set<String> stringSet;
    private String[] stringArray;

    // For type description test
    private Representer representerWithTypeDesc;
    private MyBean beanForTypeDesc;

    @Setup(Level.Trial)
    public void setup() {
        DumperOptions options = new DumperOptions();
        representer = new Representer(options);

        // Prepare simple bean
        List<String> tags = new ArrayList<>();
        tags.add("alpha");
        tags.add("beta");
        simpleBean = new MyBean("test", 42, tags);

        // Prepare list of strings
        stringList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            stringList.add("item" + i);
        }

        // Prepare map
        stringIntMap = new LinkedHashMap<>();
        for (int i = 0; i < 10; i++) {
            stringIntMap.put("key" + i, i);
        }

        // Enum
        enumValue = MyEnum.TWO;

        // Bean with list of beans
        List<MyBean> items = new ArrayList<>();
        items.add(new MyBean("a", 1, Collections.singletonList("x")));
        items.add(new MyBean("b", 2, Collections.singletonList("y")));
        beanWithList = new MyBeanWithList(items);

        // Bean with map of beans
        Map<String, MyBean> mapping = new LinkedHashMap<>();
        mapping.put("first", new MyBean("first", 10, Collections.emptyList()));
        mapping.put("second", new MyBean("second", 20, Collections.emptyList()));
        beanWithMap = new MyBeanWithMap(mapping);

        // Set and array
        stringSet = new LinkedHashSet<>(Arrays.asList("one", "two", "three"));
        stringArray = new String[]{"a", "b", "c"};

        // Representer with type description
        representerWithTypeDesc = new Representer(new DumperOptions());
        TypeDescription td = new TypeDescription(MyBean.class, new Tag("!mybean"));
        representerWithTypeDesc.addTypeDescription(td);
        beanForTypeDesc = new MyBean("typed", 7, Collections.singletonList("z"));
    }

    @Benchmark
    public Node representSimpleBean() {
        return representer.represent(simpleBean);
    }

    @Benchmark
    public Node representStringList() {
        return representer.represent(stringList);
    }

    @Benchmark
    public Node representStringIntMap() {
        return representer.represent(stringIntMap);
    }

    @Benchmark
    public Node representEnum() {
        return representer.represent(enumValue);
    }

    @Benchmark
    public Node representBeanWithList() {
        return representer.represent(beanWithList);
    }

    @Benchmark
    public Node representBeanWithMap() {
        return representer.represent(beanWithMap);
    }

    @Benchmark
    public Node representStringSet() {
        return representer.represent(stringSet);
    }

    @Benchmark
    public Node representStringArray() {
        return representer.represent(stringArray);
    }

    @Benchmark
    public Node representBeanWithTypeDescription() {
        return representerWithTypeDesc.represent(beanForTypeDesc);
    }

    // Also test representing a null value (should produce a null node)
    @Benchmark
    public Node representNull() {
        return representer.represent(null);
    }
}
