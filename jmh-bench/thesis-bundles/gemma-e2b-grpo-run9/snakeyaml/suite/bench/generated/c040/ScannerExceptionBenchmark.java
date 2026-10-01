package bench.generated.c040;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.scanner.ScannerException;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ScannerExceptionBenchmark {

    // Since ScannerException is a simple exception class with no mutable state
    // or complex setup requirements, we don't need complex @State fields.
    // We rely on the JVM/JMH to handle the instantiation cost.

    /**
     * Benchmarks the construction of a ScannerException using the no-argument constructor.
     * This measures the overhead of object instantiation and superclass initialization.
     *
     * @param bh Blackhole to consume the result and prevent dead code elimination.
     */
    @Benchmark
    public void createException(Blackhole bh) {
        try {
            // Instantiating the exception object
            new ScannerException("context", null, "problem", null);
        } catch (Exception e) {
            // Catching is generally discouraged in benchmarks unless measuring error handling overhead,
            // but necessary here if the constructor throws checked exceptions (which it doesn't seem to).
        }
        bh.consume(null);
    }

    /**
     * Benchmarks the construction of a ScannerException using the full constructor.
     * This measures the overhead of object instantiation plus parameter passing.
     *
     * @param bh Blackhole to consume the result and prevent dead code elimination.
     */
    @Benchmark
    public void createExceptionWithArgs(Blackhole bh) {
        try {
            // Instantiating the exception object with arguments
            new ScannerException("context", null, "problem", null, null);
        } catch (Exception e) {
            // Ignore
        }
        bh.consume(null);
    }
}
