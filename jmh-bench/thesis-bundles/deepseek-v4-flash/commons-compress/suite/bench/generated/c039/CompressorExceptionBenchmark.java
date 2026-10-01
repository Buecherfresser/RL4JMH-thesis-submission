package bench.generated.c039;

import org.apache.commons.compress.compressors.CompressorException;
import org.openjdk.jmh.annotations.*;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class CompressorExceptionBenchmark {

    private String message;
    private Throwable cause;
    private CompressorException exception;

    @Setup(Level.Trial)
    public void setUp() {
        this.message = "simulated compressor failure " + System.nanoTime();
        this.cause = new IllegalStateException("cause of " + this.message);
        this.exception = new CompressorException(this.message, this.cause);
    }

    @Benchmark
    public CompressorException constructWithMessage() {
        return new CompressorException(this.message);
    }

    @Benchmark
    public CompressorException constructWithMessageAndCause() {
        return new CompressorException(this.message, this.cause);
    }

    @Benchmark
    public String readMessage() {
        return this.exception.getMessage();
    }

    @Benchmark
    public Throwable readCause() {
        return this.exception.getCause();
    }

    @Benchmark
    public String readToString() {
        return this.exception.toString();
    }
}
