package bench.generated.c012;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.archivers.dump.DumpArchiveException;
import org.apache.commons.compress.archivers.ArchiveException;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class DumpArchiveExceptionBenchmark {

    // Since DumpArchiveException is stateless and its constructors are simple,
    // we don't need complex @Setup state fields.

    @Benchmark
    public void benchmarkDefaultConstructor(Blackhole bh) {
        // Instantiating the exception object
        DumpArchiveException exception = new DumpArchiveException();
        bh.consume(exception);
    }

    @Benchmark
    public void benchmarkConstructorWithMessage(Blackhole bh) {
        // Instantiating the exception object with a message
        DumpArchiveException exception = new DumpArchiveException("Test message");
        bh.consume(exception);
    }

    @Benchmark
    public void benchmarkConstructorWithCause(Blackhole bh) {
        try {
            // Instantiating the exception object with a message and a cause
            DumpArchiveException exception = new DumpArchiveException("Test message", new RuntimeException("Caused by error"));
            bh.consume(exception);
        } catch (Exception e) {
            // Should not happen in a benchmark context
        }
    }
}
