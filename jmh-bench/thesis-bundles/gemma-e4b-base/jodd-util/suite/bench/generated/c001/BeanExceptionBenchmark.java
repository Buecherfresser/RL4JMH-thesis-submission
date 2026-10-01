package bench.generated.c001;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import jodd.bean.BeanException;
import java.lang.RuntimeException;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class BeanExceptionBenchmark {

    private String testMessage;
    private Throwable testCause;

    @Setup(Level.Trial)
    public void setup() {
        // Prepare inputs
        testMessage = "A test exception message for JMH benchmarking.";
        // Use a standard runtime exception as the cause
        testCause = new RuntimeException("Underlying system failure.");
    }

    /**
     * Benchmarks the construction of BeanException using only a message.
     * BeanException(final String message)
     */
    @Benchmark
    public void constructBeanExceptionWithMessage(Blackhole bh) {
        BeanException exception = new BeanException(testMessage);
        bh.consume(exception);
    }

    /**
     * Benchmarks the construction of BeanException using a message and a cause.
     * BeanException(final String message, final Throwable t)
     */
    @Benchmark
    public void constructBeanExceptionWithMessageAndCause(Blackhole bh) {
        BeanException exception = new BeanException(testMessage, testCause);
        bh.consume(exception);
    }
}
