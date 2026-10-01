package bench.generated.c038;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.infra.Blackhole;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.introspector.PropertyUtils;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class RepresenterBenchmark {

    private Representer representer;
    private ExposedRepresenter exposedRepresenter;
    private Yaml yaml;
    private SimpleBean simpleBean;
    private Property nameProperty;
    private PropertyUtils propertyUtils;

    @Setup
    public void setup() {
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        representer = new Representer(options);
        exposedRepresenter = new ExposedRepresenter(options);
        yaml = new Yaml(representer);
        simpleBean = new SimpleBean("benchmark", 42);
        propertyUtils = new PropertyUtils();
        nameProperty = propertyUtils.getProperty(SimpleBean.class, "name");
    }

    @Benchmark
    public String dumpSimpleBean() {
        return yaml.dump(simpleBean);
    }

    @Benchmark
    public void addTypeDescription(Blackhole bh) {
        TypeDescription td = new TypeDescription(SimpleBean.class);
        TypeDescription previous = representer.addTypeDescription(td);
        bh.consume(previous);
    }

    @Benchmark
    public void setPropertyUtils(Blackhole bh) {
        PropertyUtils pu = new PropertyUtils();
        representer.setPropertyUtils(pu);
        bh.consume(pu);
    }

    @Benchmark
    public MappingNode representJavaBean() {
        return exposedRepresenter.exposeRepresentJavaBean(simpleBean);
    }

    @Benchmark
    public NodeTuple representJavaBeanProperty(Blackhole bh) {
        NodeTuple tuple = exposedRepresenter.exposeRepresentJavaBeanProperty(
                simpleBean,
                nameProperty,
                simpleBean.getName(),
                null);
        bh.consume(tuple);
        return tuple;
    }

    // -------------------------------------------------------------------------
    // Helper classes
    // -------------------------------------------------------------------------

    public static class SimpleBean {
        private final String name;
        private final int age;

        public SimpleBean(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public int getAge() {
            return age;
        }
    }

    /**
     * Subclass that makes protected methods of {@link Representer} accessible for benchmarking.
     */
    private static class ExposedRepresenter extends Representer {

        public ExposedRepresenter(DumperOptions options) {
            super(options);
        }

        public MappingNode exposeRepresentJavaBean(Object bean) {
            Set<Property> props = getProperties(bean.getClass());
            return (MappingNode) representJavaBean(props, bean);
        }

        public NodeTuple exposeRepresentJavaBeanProperty(Object bean, Property property,
                                                          Object propertyValue, Tag customTag) {
            return representJavaBeanProperty(bean, property, propertyValue, customTag);
        }
    }
}
