package bench.generated.c000;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.archivers.ArchiveException;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ArchiveExceptionBenchmark {

    // Since ArchiveException is a simple class and its methods are non-mutating
    // or simple constructors, we don't need complex state setup.

    @Benchmark
    public void benchmarkConstructor(Blackhole bh) {
        // Test the no-argument constructor
        ArchiveException ex = new ArchiveException();
        bh.consume(ex);
    }

    @Benchmark
    public void benchmarkMessageConstructor(Blackhole bh) {
        // Test the constructor with a message
        ArchiveException ex = new ArchiveException("Test message");
        bh.consume(ex);
    }

    @Benchmark
    public void benchmarkCauseConstructor(Blackhole bh) {
        // Test the constructor with a Throwable cause
        try {
            ArchiveException ex = new ArchiveException(new RuntimeException("Cause"));
            bh.consume(ex);
        } catch (Exception e) {
            // Ignore exceptions during benchmark execution
        }
    }
}
