package bench.generated.c009;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import jodd.bean.exception.PropertyNotFoundBeanException;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class PropertyNotFoundBeanExceptionBenchmark {

    private String message;

    @Setup(Level.Trial)
    public void setUp() {
        message = "Property not found";
    }

    @Benchmark
    public PropertyNotFoundBeanException constructException() {
        return new PropertyNotFoundBeanException(message, null);
    }

    @Benchmark
    public void getMessage(Blackhole bh) {
        PropertyNotFoundBeanException ex = new PropertyNotFoundBeanException(message, null);
        bh.consume(ex.getMessage());
    }
}
