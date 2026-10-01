package bench.generated.c009;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

import jodd.bean.exception.PropertyNotFoundBeanException;
import jodd.bean.BeanProperty;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class PropertyNotFoundBeanExceptionBenchmark {

    /**
     * Benchmarks the construction of PropertyNotFoundBeanException.
     * This measures the overhead of object creation and initialization.
     *
     * @param bh Blackhole to consume the result.
     */
    @Benchmark
    public void testConstructor(Blackhole bh) {
        try {
            // We instantiate the exception.
            new PropertyNotFoundBeanException("Test message", null);
        } catch (Exception e) {
            // Ignore exceptions for benchmarking purposes.
        }
        bh.consume(null);
    }
}
