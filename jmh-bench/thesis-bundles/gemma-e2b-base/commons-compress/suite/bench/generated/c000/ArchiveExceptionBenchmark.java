package bench.generated.c000;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.archivers.ArchiveException;
import java.lang.IllegalArgumentException;
import java.lang.NullPointerException;
import java.util.function.Supplier;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ArchiveExceptionBenchmark {

    // Inputs prepared in @Setup
    private String message;
    private Exception cause;
    private Throwable causeThrowable;

    @Setup
    public void setup() {
        // Setup fixed inputs for exception construction
        this.message = "Benchmark message for ArchiveException";
        this.cause = new NullPointerException("Simulated NPE");
        this.causeThrowable = new IllegalArgumentException("Simulated IAE");
    }

    @Benchmark
    public void testDefaultConstructor(Blackhole bh) throws ArchiveException {
        ArchiveException ex = new ArchiveException();
        bh.consume(ex);
    }

    @Benchmark
    public void testMessageConstructor(Blackhole bh) throws ArchiveException {
        ArchiveException ex = new ArchiveException(message);
        bh.consume(ex);
    }

    @Benchmark
    public void testMessageAndCauseConstructor(Blackhole bh) throws ArchiveException {
        ArchiveException ex = new ArchiveException(message, cause);
        bh.consume(ex);
    }

    @Benchmark
    public void testCauseConstructor(Blackhole bh) throws ArchiveException {
        ArchiveException ex = new ArchiveException(causeThrowable);
        bh.consume(ex);
    }

    @Benchmark
    public void testRequireNonNullStaticMethod(Blackhole bh) throws ArchiveException {
        // Test static method requiring a supplier
        Supplier<String> supplier = () -> "Required check failed";
        
        // We expect this to throw ArchiveException if the object is null, 
        // but since we pass a non-null object, it should succeed.
        ArchiveException result = ArchiveException.requireNonNull(null, supplier);
        bh.consume(result);
    }
}
