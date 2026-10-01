package bench.generated.c012;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.archivers.dump.DumpArchiveException;
import java.io.IOException;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class DumpArchiveExceptionBenchmark {

    // State fields for inputs
    private String message1;
    private String message2;
    private IOException cause1;
    private Throwable cause2;

    @Setup
    public void setup() {
        // Build inputs once in @Setup
        message1 = "Test message for constructor 1";
        message2 = "Test message for constructor 2";
        
        // Create a dummy IOException for testing the cause constructor
        cause1 = new IOException("Simulated IO Error");
        cause2 = new RuntimeException("Simulated Runtime Error");
    }

    @Benchmark
    public void testConstructorNoArgs(Blackhole bh) {
        DumpArchiveException exception = new DumpArchiveException();
        bh.consume(exception);
    }

    @Benchmark
    public void testConstructorWithMessage(Blackhole bh) {
        DumpArchiveException exception = new DumpArchiveException(message1);
        bh.consume(exception);
    }

    @Benchmark
    public void testConstructorWithMessageAndCause(Blackhole bh) {
        DumpArchiveException exception = new DumpArchiveException(message2, cause1);
        bh.consume(exception);
    }

    @Benchmark
    public void testConstructorWithCauseOnly(Blackhole bh) {
        DumpArchiveException exception = new DumpArchiveException(cause2);
        bh.consume(exception);
    }
}
