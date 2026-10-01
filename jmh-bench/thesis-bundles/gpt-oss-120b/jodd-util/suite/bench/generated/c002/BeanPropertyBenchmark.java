package bench.generated.c002;

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
import java.util.concurrent.TimeUnit;
import jodd.bean.BeanProperty;
import jodd.bean.BeanUtil;
import jodd.bean.BeanUtilBean;
import jodd.introspector.Getter;
import jodd.introspector.Setter;
import java.util.HashMap;
import java.lang.reflect.Constructor;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class BeanPropertyBenchmark {

    private BeanProperty beanProperty;
    private BeanProperty mapProperty;
    private BeanProperty beanPropertySetName;
    private BeanProperty beanPropertyUpdateBean;

    private TestBean testBean;
    private HashMap<String, Integer> map;

    @Setup
    public void init() {
        try {
            testBean = new TestBean();
            map = new HashMap<>();
            map.put("key", 1);

            Constructor<BeanProperty> ctor = BeanProperty.class.getDeclaredConstructor(
                    BeanUtilBean.class, Object.class, String.class, boolean.class);
            ctor.setAccessible(true);

            beanProperty = ctor.newInstance(BeanUtil.pojo, testBean, "value", false);
            mapProperty = ctor.newInstance(BeanUtil.pojo, map, "size", false);
            beanPropertySetName = ctor.newInstance(BeanUtil.pojo, testBean, "value", false);
            beanPropertyUpdateBean = ctor.newInstance(BeanUtil.pojo, testBean, "value", false);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Benchmark
    public Getter benchmarkGetGetter() {
        return beanProperty.getGetter(true);
    }

    @Benchmark
    public Setter benchmarkGetSetter() {
        return beanProperty.getSetter(true);
    }

    @Benchmark
    public boolean benchmarkIsMap() {
        return mapProperty.isMap();
    }

    @Benchmark
    public String benchmarkSetName() {
        beanPropertySetName.setName("newProp");
        return beanPropertySetName.toString();
    }

    @Benchmark
    public Getter benchmarkUpdateBean() {
        beanPropertyUpdateBean.updateBean(new TestBean());
        return beanPropertyUpdateBean.getGetter(true);
    }

    @Benchmark
    public boolean benchmarkCurrentPropertyExistOnParent() {
        return beanProperty.currentPropertyExistOnParent(true);
    }

    @Benchmark
    public boolean benchmarkIsExistingParentNull() {
        return beanProperty.isExistingParentNull();
    }

    @Benchmark
    public String benchmarkToString() {
        return beanProperty.toString();
    }

    // Simple POJO used for testing BeanProperty
    private static class TestBean {
        private int value;

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }
    }
}
