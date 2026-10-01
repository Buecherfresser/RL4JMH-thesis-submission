package bench.generated.c001;

import org.openjdk.jmh.annotations.*;
import java.util.concurrent.TimeUnit;
import jodd.bean.BeanException;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class BeanExceptionBenchmark {

    private String message;
    private Throwable cause;

    @Setup(Level.Trial)
    public void setup() {
        message = "some error message";
        cause = new RuntimeException("root cause");
    }

    @Benchmark
    public BeanException createWithMessage() {
        return new BeanException(message);
    }

    @Benchmark
    public BeanException createWithMessageAndCause() {
        return new BeanException(message, cause);
    }
}
