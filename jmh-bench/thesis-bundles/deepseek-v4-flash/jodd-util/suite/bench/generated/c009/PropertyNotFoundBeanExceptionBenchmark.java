package bench.generated.c009;

import jodd.bean.BeanUtilBean;
import jodd.bean.exception.PropertyNotFoundBeanException;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class PropertyNotFoundBeanExceptionBenchmark {

    private BeanUtilBean beanUtilBean;
    private Object bean;

    @Setup(Level.Trial)
    public void setup() {
        beanUtilBean = new BeanUtilBean();
        bean = new SampleBean();
    }

    @Benchmark
    public void constructException(Blackhole bh) {
        try {
            beanUtilBean.getProperty(bean, "nonExistentProperty");
        } catch (PropertyNotFoundBeanException e) {
            bh.consume(e);
        }
    }

    public static class SampleBean {
        private String name = "example";
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }
}
