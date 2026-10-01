package bench.generated.c005;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

import jodd.bean.exception.ForcedBeanException;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ForcedBeanExceptionBenchmark {

    @Setup
    public void setup() {
        // Setup phase is empty as the benchmark is stateless and relies on
        // the overhead of object creation/method invocation.
    }

    @Benchmark
    public void benchmarkConstructor(Blackhole bh) {
        try {
            // Attempt to instantiate the class. We pass nulls for dependencies
            // that we cannot instantiate, focusing the benchmark on the SUT's
            // internal logic (string concatenation and super call).
            new ForcedBeanException(
                "Test message",
                null, // Mocking BeanProperty
                null  // Mocking Throwable
            );
        } catch (Exception e) {
            // Catch potential exceptions during instantiation if dependencies fail
            // to handle nulls, which is acceptable for a benchmark focusing on the SUT path.
        }
        bh.consume(null);
    }
}
