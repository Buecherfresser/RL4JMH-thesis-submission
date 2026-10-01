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

    private String message;
    private Throwable cause;
    private BeanException preCreated;

    @Setup(Level.Trial)
    public void setUp() {
        // Build a non‑constant message
        StringBuilder sb = new StringBuilder();
        sb.append("Benchmark message ");
        sb.append(System.nanoTime());
        this.message = sb.toString();

        // Build a cause exception
        this.cause = new RuntimeException("Underlying cause");

        // Pre‑create an exception for read‑only method benchmarks
        this.preCreated = new BeanException(message, cause);
    }

    @Benchmark
    public BeanException constructMessageOnly() {
        return new BeanException(message);
    }

    @Benchmark
    public BeanException constructMessageAndCause() {
        return new BeanException(message, cause);
    }

    @Benchmark
    public String getMessage(Blackhole bh) {
        String msg = preCreated.getMessage();
        bh.consume(msg);
        return msg;
    }

    @Benchmark
    public Throwable getCause(Blackhole bh) {
        Throwable c = preCreated.getCause();
        bh.consume(c);
        return c;
    }
}
