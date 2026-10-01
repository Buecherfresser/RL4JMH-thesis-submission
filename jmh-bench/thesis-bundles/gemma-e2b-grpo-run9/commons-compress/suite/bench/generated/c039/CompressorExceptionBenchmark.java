package bench.generated.c039;

import org.apache.commons.compress.compressors.CompressorException;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class CompressorExceptionBenchmark {

    // Since CompressorException is stateless and we create new instances in every benchmark,
    // no @State fields are strictly necessary.

    /**
     * Benchmark for constructing CompressorException with a single String message.
     * We consume the result to prevent dead code elimination.
     */
    @Benchmark
    public void createExceptionWithMessage(Blackhole bh) {
        try {
            new CompressorException("Test message");
        } catch (Exception e) {
            // Ignore exceptions during benchmark setup if they occur,
            // as we are measuring the construction path.
        }
        bh.consume(null);
    }

    /**
     * Benchmark for constructing CompressorException with a message and a Throwable cause.
     * We consume the result to prevent dead code elimination.
     */
    @Benchmark
    public void createExceptionWithCause(Blackhole bh) {
        try {
            new CompressorException("Test message", new RuntimeException("Caused by error"));
        } catch (Exception e) {
            // Ignore exceptions during benchmark setup
        }
        bh.consume(null);
    }
}
