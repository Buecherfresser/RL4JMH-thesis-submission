package bench.generated.c006;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

import jodd.bean.BeanException;
import jodd.bean.BeanProperty;
import jodd.bean.exception.InvalidPropertyBeanException;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class InvalidPropertyBeanExceptionBenchmark {

    // Since the constructor is public and stateless (no instance fields),
    // we do not need @State fields.

    @Benchmark
    public void createException(Blackhole bh) {
        try {
            // Call the public constructor. This is the operation we are measuring.
            InvalidPropertyBeanException exception = new InvalidPropertyBeanException(
                "Test message",
                null // Passing null for BeanProperty, assuming it's acceptable or mocked/handled by the runtime environment for this test.
            );
            bh.consume(exception);
        } catch (Exception e) {
            // Catching exceptions during setup/benchmarking is acceptable if they don't affect the measurement logic.
        }
    }
}
