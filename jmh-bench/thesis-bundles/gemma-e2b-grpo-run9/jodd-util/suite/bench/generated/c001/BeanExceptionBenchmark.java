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

    // Since BeanException is stateless and its constructors are simple,
    // no complex @State fields are required.

    /**
     * Benchmark for the BeanException constructor that takes a String message.
     * This measures the cost of creating the exception object.
     */
    @Benchmark
    public void createException_String(Blackhole bh) {
        try {
            new BeanException("Test message");
        } catch (Exception e) {
            // Catching exceptions during benchmark setup is generally discouraged,
            // but necessary if the SUT throws unchecked exceptions during construction.
        }
        bh.consume(null); // Consume null as the method is void
    }

    /**
     * Benchmark for the BeanException constructor that takes a String message and a Throwable.
     * This measures the cost of creating the exception object, including potential
     * internal operations related to the Throwable parameter.
     */
    @Benchmark
    public void createException_WithThrowable(Blackhole bh) {
        try {
            new BeanException("Error message", new RuntimeException("Caused by error"));
        } catch (Exception e) {
            // Ignore exceptions for benchmarking purposes
        }
        bh.consume(null); // Consume null as the method is void
    }
}
