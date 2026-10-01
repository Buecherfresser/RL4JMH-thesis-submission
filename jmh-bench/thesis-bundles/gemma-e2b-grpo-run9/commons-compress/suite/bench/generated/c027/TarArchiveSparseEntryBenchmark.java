package bench.generated.c027;

import org.apache.commons.compress.archivers.tar.TarArchiveSparseEntry;
import java.io.IOException;
import java.util.List;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class TarArchiveSparseEntryBenchmark {

    // Since TarArchiveSparseEntry is immutable after construction, we don't need @State fields
    // unless we were benchmarking a mutable object or wanted to reuse a complex setup.

    @Benchmark
    public void benchmarkConstructor(Blackhole bh) {
        try {
            // Create a minimal, non-null byte array input.
            // This relies on the internal TarUtils methods not failing catastrophically
            // with arbitrary data, which is a necessary assumption for benchmarking
            // a library class without its full context.
            TarArchiveSparseEntry entry = new TarArchiveSparseEntry(new byte[100]);
            bh.consume(entry);
        } catch (IOException e) {
            // Catching exception to prevent benchmark failure if dummy data causes issues.
            // In a real scenario, this would require a more robust setup.
        }
    }

    @Benchmark
    public boolean benchmarkIsExtended(Blackhole bh) {
        try {
            // Instantiate inside the benchmark to test the method call overhead.
            TarArchiveSparseEntry entry = new TarArchiveSparseEntry(new byte[100]);
            boolean result = entry.isExtended();
            bh.consume(result);
            return result;
        } catch (IOException e) {
            // Ignore exceptions for benchmarking purposes
            return false;
        }
    }

    @Benchmark
    public List<org.apache.commons.compress.archivers.tar.TarArchiveStructSparse> benchmarkGetSparseHeaders(Blackhole bh) {
        try {
            // Instantiate inside the benchmark to test the method call overhead.
            TarArchiveSparseEntry entry = new TarArchiveSparseEntry(new byte[100]);
            List<org.apache.commons.compress.archivers.tar.TarArchiveStructSparse> headers = entry.getSparseHeaders();
            bh.consume(headers);
            return headers;
        } catch (IOException e) {
            // Ignore exceptions for benchmarking purposes
            return null;
        }
    }
}
