package bench.generated.c012;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.archivers.dump.DumpArchiveException;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class DumpArchiveExceptionBenchmark {

    private String message;
    private Throwable cause;
    private DumpArchiveException preCreated;

    @Setup(Level.Trial)
    public void setUp() {
        message = "Test message for DumpArchiveException";
        cause = new RuntimeException("Root cause");
        preCreated = new DumpArchiveException(message, cause);
    }

    @Benchmark
    public DumpArchiveException benchmarkDefaultConstructor() {
        return new DumpArchiveException();
    }

    @Benchmark
    public DumpArchiveException benchmarkMessageConstructor() {
        return new DumpArchiveException(message);
    }

    @Benchmark
    public DumpArchiveException benchmarkMessageCauseConstructor() {
        return new DumpArchiveException(message, cause);
    }

    @Benchmark
    public DumpArchiveException benchmarkCauseConstructor() {
        return new DumpArchiveException(cause);
    }

    @Benchmark
    public String benchmarkGetMessage() {
        return preCreated.getMessage();
    }

    @Benchmark
    public Throwable benchmarkGetCause() {
        return preCreated.getCause();
    }
}
