package bench.generated.c024;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.introspector.PropertyUtils;
import org.yaml.snakeyaml.introspector.BeanAccess;
import org.yaml.snakeyaml.introspector.Property;
import java.util.Set;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class PropertyUtilsBenchmark {

    @State(Scope.Benchmark)
    public static class BenchmarkState {
        PropertyUtils utils;
        Class<?> beanClass;
        Class<?> fieldClass;
        String beanPropertyName;
        String fieldPropertyName;
        BeanAccess defaultAccess;
        BeanAccess fieldAccess;

        @Setup(Level.Trial)
        public void setUp() {
            utils = new PropertyUtils();
            beanClass = SampleBean.class;
            fieldClass = FieldBean.class;
            beanPropertyName = "name";
            fieldPropertyName = "title";
            defaultAccess = BeanAccess.DEFAULT;
            fieldAccess = BeanAccess.FIELD;
        }
    }

    // Simple JavaBean with getters/setters
    public static class SampleBean {
        private String name;
        private int age;

        public SampleBean() {}

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }
    }

    // Class with public fields (used for FIELD access)
    public static class FieldBean {
        public String title;
        public int count;
    }

    @Benchmark
    public Set<Property> getPropertiesDefault(BenchmarkState s) {
        return s.utils.getProperties(s.beanClass);
    }

    @Benchmark
    public Set<Property> getPropertiesWithFieldAccess(BenchmarkState s) {
        return s.utils.getProperties(s.fieldClass, s.fieldAccess);
    }

    @Benchmark
    public Property getPropertyDefault(BenchmarkState s) {
        return s.utils.getProperty(s.beanClass, s.beanPropertyName);
    }

    @Benchmark
    public Property getPropertyWithFieldAccess(BenchmarkState s) {
        return s.utils.getProperty(s.fieldClass, s.fieldPropertyName, s.fieldAccess);
    }

    @Benchmark
    public void setBeanAccessField(BenchmarkState s, Blackhole bh) {
        s.utils.setBeanAccess(BeanAccess.FIELD);
        bh.consume(s.utils);
    }

    @Benchmark
    public void setAllowReadOnlyPropertiesTrue(BenchmarkState s, Blackhole bh) {
        s.utils.setAllowReadOnlyProperties(true);
        bh.consume(s.utils);
    }

    @Benchmark
    public void setSkipMissingPropertiesTrue(BenchmarkState s, Blackhole bh) {
        s.utils.setSkipMissingProperties(true);
        bh.consume(s.utils);
    }

    @Benchmark
    public boolean isAllowReadOnlyProperties(BenchmarkState s) {
        return s.utils.isAllowReadOnlyProperties();
    }

    @Benchmark
    public boolean isSkipMissingProperties(BenchmarkState s) {
        return s.utils.isSkipMissingProperties();
    }
}
