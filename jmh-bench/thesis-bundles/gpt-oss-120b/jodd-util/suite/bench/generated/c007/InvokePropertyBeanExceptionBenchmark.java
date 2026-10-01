package bench.generated.c007;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import jodd.bean.exception.InvokePropertyBeanException;
import jodd.bean.BeanProperty;
import java.lang.reflect.Method;
import java.lang.reflect.Constructor;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class InvokePropertyBeanExceptionBenchmark {

    private String shortMessage;
    private String longMessage;
    private BeanProperty beanProperty;
    private Throwable cause;
    private Throwable nullCause;

    @Setup(Level.Trial)
    public void setup() throws Exception {
        shortMessage = "msg";
        longMessage = "This is a considerably longer message used to test the performance of exception message handling in the benchmark.";
        cause = new RuntimeException("root cause");
        nullCause = null;

        // Prepare a simple bean with getter and setter
        Method getter = TestBean.class.getMethod("getValue");
        Method setter = TestBean.class.getMethod("setValue", int.class);

        // Find BeanProperty constructor (Class type, String name, Method getter, Method setter)
        Class<?> beanPropClass = Class.forName("jodd.bean.BeanProperty");
        Constructor<?> ctor = beanPropClass.getConstructor(Class.class, String.class, Method.class, Method.class);
        beanProperty = (BeanProperty) ctor.newInstance(int.class, "value", getter, setter);
    }

    @Benchmark
    public InvokePropertyBeanException invokeShortMessage() {
        return new InvokePropertyBeanException(shortMessage, beanProperty, cause);
    }

    @Benchmark
    public InvokePropertyBeanException invokeLongMessage() {
        return new InvokePropertyBeanException(longMessage, beanProperty, cause);
    }

    @Benchmark
    public InvokePropertyBeanException invokeWithNullCause() {
        return new InvokePropertyBeanException(shortMessage, beanProperty, nullCause);
    }

    // Simple bean used to obtain getter/setter methods for BeanProperty construction
    public static class TestBean {
        private int value;

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }
    }
}
