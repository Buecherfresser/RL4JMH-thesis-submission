package bench.generated.c008;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import jodd.bean.exception.NullPropertyBeanException;
import jodd.bean.BeanProperty;
import sun.misc.Unsafe;
import java.lang.reflect.Field;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class NullPropertyBeanExceptionBenchmark {

    private BeanProperty beanProperty;
    private String shortMessage;
    private String longMessage;
    private static final Unsafe UNSAFE;

    static {
        try {
            Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            UNSAFE = (Unsafe) f.get(null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Setup(Level.Trial)
    public void setup() throws Exception {
        beanProperty = (BeanProperty) UNSAFE.allocateInstance(BeanProperty.class);
        shortMessage = "msg";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 20; i++) {
            sb.append("longMessagePart");
        }
        longMessage = sb.toString();
    }

    @Benchmark
    public NullPropertyBeanException constructShortMessage() {
        return new NullPropertyBeanException(shortMessage, beanProperty);
    }

    @Benchmark
    public NullPropertyBeanException constructLongMessage() {
        return new NullPropertyBeanException(longMessage, beanProperty);
    }

    @Benchmark
    public void constructAndConsume(Blackhole bh) {
        NullPropertyBeanException ex = new NullPropertyBeanException(shortMessage, beanProperty);
        bh.consume(ex);
    }
}
