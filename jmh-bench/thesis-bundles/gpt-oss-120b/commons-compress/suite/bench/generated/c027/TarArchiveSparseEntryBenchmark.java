package bench.generated.c027;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.archivers.tar.TarArchiveSparseEntry;
import java.util.List;
import java.io.IOException;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class TarArchiveSparseEntryBenchmark {

    private byte[] headerBuf;
    private TarArchiveSparseEntry entry;

    @Setup(Level.Trial)
    public void setUp() throws IOException {
        // Allocate a minimal header buffer sufficient for the constructor parsing.
        // Length = SPARSELEN_GNU_SPARSE + ISEXTENDEDLEN_GNU_SPARSE as defined in TarConstants.
        // Both constants are 512 and 1 respectively in the library, but we compute size safely.
        int size = org.apache.commons.compress.archivers.tar.TarConstants.SPARSELEN_GNU_SPARSE
                + org.apache.commons.compress.archivers.tar.TarConstants.ISEXTENDEDLEN_GNU_SPARSE;
        headerBuf = new byte[size];
        // All zeros => no sparse entries, isExtended = false.
        entry = new TarArchiveSparseEntry(headerBuf);
    }

    @Benchmark
    public List<?> benchmarkGetSparseHeaders() {
        return entry.getSparseHeaders();
    }

    @Benchmark
    public boolean benchmarkIsExtended() {
        return entry.isExtended();
    }

    @Benchmark
    public TarArchiveSparseEntry benchmarkConstructor() throws IOException {
        return new TarArchiveSparseEntry(headerBuf);
    }
}
