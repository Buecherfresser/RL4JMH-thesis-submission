package bench.generated.c012;

import org.apache.commons.compress.archivers.dump.DumpArchiveException;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class DumpArchiveExceptionBenchmark {

    private String sampleMessage;
    private Throwable sampleCause;

    @Setup(Level.Trial)
    public void setup() {
        // Prepare inputs for constructors
        sampleMessage = "Test archive error occurred.";
        // Use a simple runtime exception as a cause
        sampleCause = new RuntimeException("Underlying IO failure.");
    }

    @Benchmark
    public void constructNoArgsException(Blackhole bh) {
        DumpArchiveException e = new DumpArchiveException();
        bh.consume(e);
    }

    @Benchmark
    public void constructWithMessageException(Blackhole bh) {
        DumpArchiveException e = new DumpArchiveException(sampleMessage);
        bh.consume(e);
    }

    @Benchmark
    public void constructWithMessageAndCauseException(Blackhole bh) {
        DumpArchiveException e = new DumpArchiveException(sampleMessage, sampleCause);
        bh.consume(e);
    }

    @Benchmark
    public void constructWithCauseException(Blackhole bh) {
        DumpArchiveException e = new DumpArchiveException(sampleCause);
        bh.consume(e);
    }
}
