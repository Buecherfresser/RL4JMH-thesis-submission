package bench.generated.c039;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorException;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class CompressorExceptionBenchmark {

    private String errorMessage;
    private Throwable causeThrowable;

    @Setup(Level.Trial)
    public void setup() {
        // Input 1: Standard error message
        errorMessage = "Simulated compression failure during stream processing.";

        // Input 2: A dummy cause throwable
        causeThrowable = new RuntimeException("Underlying IO error occurred.");
    }

    /**
     * Benchmarks the construction of CompressorException using only a message.
     * This tests the simplest constructor path.
     */
    @Benchmark
    public void constructExceptionWithMessage(Blackhole bh) {
        CompressorException exception = new CompressorException(errorMessage);
        bh.consume(exception);
    }

    /**
     * Benchmarks the construction of CompressorException using a message and a cause.
     * This tests the constructor path that involves handling a Throwable object.
     */
    @Benchmark
    public void constructExceptionWithMessageAndCause(Blackhole bh) {
        CompressorException exception = new CompressorException(errorMessage, causeThrowable);
        bh.consume(exception);
    }

    /**
     * Benchmarks accessing the message property after construction.
     * We construct the object once in setup and reuse it for the benchmark loop.
     */
    private CompressorException exceptionInstance;

    @Setup(Level.Trial)
    public void setupAccess() {
        // Initialize the instance once for access benchmarks
        exceptionInstance = new CompressorException(errorMessage);
    }

    @Benchmark
    public void getMessageFromException(Blackhole bh) {
        String message = exceptionInstance.getMessage();
        bh.consume(message);
    }

    @Benchmark
    public void getCauseFromException(Blackhole bh) {
        // Note: Since the instance was created without a cause in setupAccess, getCause() should return null.
        Throwable cause = exceptionInstance.getCause();
        bh.consume(cause);
    }
}
