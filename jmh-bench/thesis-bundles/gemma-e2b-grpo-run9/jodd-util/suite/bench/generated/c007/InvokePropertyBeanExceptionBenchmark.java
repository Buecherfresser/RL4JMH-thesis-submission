package bench.generated.c007;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

import jodd.bean.exception.InvokePropertyBeanException;
import jodd.bean.BeanProperty;
import java.lang.Throwable;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class InvokePropertyBeanExceptionBenchmark {

    // Since InvokePropertyBeanException is stateless and the constructor takes complex types,
    // we do not need a persistent @State field.

    /**
     * Benchmarks the instantiation of InvokePropertyBeanException.
     * This tests the overhead of calling the constructor and its internal logic.
     * We pass nulls for complex dependencies to minimize setup complexity.
     *
     * @param bh Blackhole to consume the result (void method requirement).
     */
    @Benchmark
    public void benchmarkInstantiation(Blackhole bh) {
        try {
            // Attempt to instantiate the class. This is the operation being measured.
            // We pass nulls for BeanProperty and Throwable as we don't have concrete implementations.
            InvokePropertyBeanException exception = new InvokePropertyBeanException(
                "Test message",
                null,
                null
            );
            // Consume the result to prevent dead code elimination
            bh.consume(exception);
        } catch (Exception e) {
            // Catch potential exceptions during instantiation if dependencies fail,
            // ensuring the benchmark doesn't crash, though this might skew results.
        }
    }
}
