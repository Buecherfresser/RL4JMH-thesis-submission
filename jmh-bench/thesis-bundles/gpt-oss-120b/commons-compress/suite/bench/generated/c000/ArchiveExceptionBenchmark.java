package bench.generated.c000;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.archivers.ArchiveException;
import java.util.function.Supplier;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ArchiveExceptionBenchmark {

    private String message;
    private Throwable cause;
    private Exception deprecatedCause;
    private Object nonNullObj;
    private Supplier<String> messageSupplier;
    private Object nullObj; // always null

    @Setup
    public void setup() {
        this.message = new String("benchmark message");
        this.cause = new RuntimeException("benchmark cause");
        this.deprecatedCause = new Exception("deprecated cause");
        this.nonNullObj = new Object();
        this.messageSupplier = () -> message;
        this.nullObj = null;
    }

    @Benchmark
    public ArchiveException constructDefault() {
        return new ArchiveException();
    }

    @Benchmark
    public ArchiveException constructMessage() {
        return new ArchiveException(message);
    }

    @Benchmark
    public ArchiveException constructMessageCause() {
        return new ArchiveException(message, cause);
    }

    @Benchmark
    public ArchiveException constructCause() {
        return new ArchiveException(cause);
    }

    @Benchmark
    public ArchiveException constructDeprecated() {
        return new ArchiveException(message, deprecatedCause);
    }

    @Benchmark
    public Object requireNonNullSuccess() throws ArchiveException {
        return ArchiveException.requireNonNull(nonNullObj, messageSupplier);
    }

    @Benchmark
    public void requireNonNullFailure(Blackhole bh) {
        try {
            ArchiveException.requireNonNull(nullObj, messageSupplier);
        } catch (ArchiveException e) {
            bh.consume(e);
        }
    }
}
