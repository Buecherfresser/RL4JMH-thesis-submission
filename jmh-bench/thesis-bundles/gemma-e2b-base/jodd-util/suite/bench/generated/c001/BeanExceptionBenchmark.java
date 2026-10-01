package bench.generated.c001;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import jodd.bean.BeanException;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class BeanExceptionBenchmark {

    // Input data prepared in @Setup
    private String exceptionMessage;

    @Setup
    public void setup() {
        // Prepare a representative string payload for the exception message.
        // This ensures the input is not a compile-time constant.
        this.exceptionMessage = "Test message for BeanException benchmark run.";
    }

    @Benchmark
    public void createBeanException(Blackhole bh) {
        // Call the constructor, which is the primary operation of this class.
        BeanException exception = new BeanException(exceptionMessage);
        bh.consume(exception);
    }
}
