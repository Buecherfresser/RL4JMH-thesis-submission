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
    private String message1;
    private String message2;
    private Throwable cause;

    @Setup
    public void setup() {
        // Prepare distinct input messages
        message1 = "Test message one";
        message2 = "Another test message for benchmarking";
        cause = new RuntimeException("Setup failure");
    }

    @Benchmark
    public void createExceptionWithMessage1(Blackhole bh) {
        BeanException exception = new BeanException(message1);
        bh.consume(exception);
    }

    @Benchmark
    public void createExceptionWithMessage2(Blackhole bh) {
        BeanException exception = new BeanException(message2);
        bh.consume(exception);
    }

    @Benchmark
    public void createExceptionWithCause(Blackhole bh) {
        BeanException exception = new BeanException(message1, cause);
        bh.consume(exception);
    }
}
