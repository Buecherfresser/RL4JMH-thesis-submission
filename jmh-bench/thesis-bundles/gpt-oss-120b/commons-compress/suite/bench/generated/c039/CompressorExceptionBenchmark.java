package bench.generated.c039;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.compressors.CompressorException;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class CompressorExceptionBenchmark {

    private String message;
    private Throwable cause;
    private CompressorException preCreated;
    private CompressorException preCreatedWithCause;

    @Setup(Level.Trial)
    public void setUp() {
        // Build a non‑trivial message to avoid compile‑time constants
        StringBuilder sb = new StringBuilder();
        sb.append("Benchmarking CompressorException with payload size ");
        sb.append(1024);
        message = sb.toString();

        // Use a generic exception as cause
        cause = new Exception("Root cause for benchmark");

        // Pre‑create exceptions for read‑only method benchmarks
        preCreated = new CompressorException(message);
        preCreatedWithCause = new CompressorException(message, cause);
    }

    @Benchmark
    public CompressorException constructWithMessage() {
        return new CompressorException(message);
    }

    @Benchmark
    public CompressorException constructWithMessageAndCause() {
        return new CompressorException(message, cause);
    }

    @Benchmark
    public String getMessage() {
        return preCreated.getMessage();
    }

    @Benchmark
    public Throwable getCause() {
        return preCreatedWithCause.getCause();
    }
}
