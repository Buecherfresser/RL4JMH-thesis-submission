package bench.generated.c000;

import org.apache.commons.compress.archivers.ArchiveException;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ArchiveExceptionBenchmark {

    private Supplier<String> messageSupplier;
    private Throwable cause;
    private String message;

    @Setup(Level.Trial)
    public void setup() {
        // Setup common inputs
        messageSupplier = () -> "Test message";
        message = "Test message";
        cause = new RuntimeException("Test cause");
    }

    // --- Constructor Benchmarks ---

    @Benchmark
    public void benchmarkDefaultConstructor(Blackhole bh) {
        ArchiveException e = new ArchiveException();
        bh.consume(e);
    }

    @Benchmark
    public void benchmarkWithMessage(Blackhole bh) {
        ArchiveException e = new ArchiveException(message);
        bh.consume(e);
    }

    @Benchmark
    public void benchmarkWithCause(Blackhole bh) {
        ArchiveException e = new ArchiveException(cause);
        bh.consume(e);
    }

    // --- Static Utility Method Benchmarks (requireNonNull) ---

    @Benchmark
    public void benchmarkRequireNonNull_Success(Blackhole bh) {
        // Test case where the object is not null
        String nonNullObject = "Valid";
        try {
            ArchiveException.requireNonNull(nonNullObject, messageSupplier);
        } catch (ArchiveException e) {
            // Should not happen in success case
            bh.consume(e);
        }
    }

    @Benchmark
    public void benchmarkRequireNonNull_Failure(Blackhole bh) {
        // Test case where the object is null (this will throw ArchiveException)
        try {
            ArchiveException.requireNonNull(null, messageSupplier);
        } catch (ArchiveException e) {
            // JMH measures the time taken to execute the code path, including the throw
            bh.consume(e);
        }
    }
}
