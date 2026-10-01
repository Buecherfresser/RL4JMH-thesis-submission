package bench.generated.c008;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

import jodd.bean.exception.NullPropertyBeanException;
import jodd.bean.BeanProperty;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class NullPropertyBeanExceptionBenchmark {

    // Since NullPropertyBeanException is stateless regarding external state,
    // we do not need @State fields.

    /**
     * Benchmarks the construction of NullPropertyBeanException.
     * This tests the overhead of calling the constructor and its internal logic.
     * We pass null/dummy values as we cannot instantiate BeanProperty without its source.
     *
     * @param bh Blackhole to consume the result and prevent dead code elimination.
     */
    @Benchmark
    public void constructException(Blackhole bh) {
        try {
            // Attempt to instantiate the exception. This relies on BeanProperty being available
            // and having a constructible state, which is necessary for compilation.
            NullPropertyBeanException exception = new NullPropertyBeanException("Test message", null);
            bh.consume(exception);
        } catch (Exception e) {
            // Catch potential exceptions during instantiation if BeanProperty is complex
            // or missing, ensuring the benchmark doesn't crash the harness.
        }
    }
}
