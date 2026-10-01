package bench.generated.c012;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.lang.RuntimeException;
import org.apache.commons.compress.archivers.dump.DumpArchiveException;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
public class DumpArchiveExceptionBenchmark {

    // State fields to hold pre-built components for benchmarking
    private String testMessage;
    private RuntimeException testCause;

    @Setup
    public void setup() {
        // Prepare fixed payloads once in @Setup
        this.testMessage = "Test message for dump exception";
        this.testCause = new RuntimeException("Simulated IO failure");
    }

    /**
     * Benchmark for the no-argument constructor.
     */
    @Benchmark
    public void testNoArgConstructor(Blackhole bh) {
        DumpArchiveException exception = new DumpArchiveException();
        bh.consume(exception);
    }

    /**
     * Benchmark for the constructor taking a message.
     */
    @Benchmark
    public void testMessageConstructor(Blackhole bh) {
        DumpArchiveException exception = new DumpArchiveException(testMessage);
        bh.consume(exception);
    }

    /**
     * Benchmark for the constructor taking a message and a cause.
     */
    @Benchmark
    public void testMessageAndCauseConstructor(Blackhole bh) {
        DumpArchiveException exception = new DumpArchiveException(testMessage, testCause);
        bh.consume(exception);
    }

    /**
     * Benchmark for the constructor taking only a cause.
     */
    @Benchmark
    public void testCauseConstructor(Blackhole bh) {
        DumpArchiveException exception = new DumpArchiveException(testCause);
        bh.consume(exception);
    }
}
