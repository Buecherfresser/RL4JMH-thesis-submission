package bench.generated.c027;

import org.apache.commons.compress.archivers.tar.TarArchiveSparseEntry;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

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
    public void setUp() {
        // All-zero header: 21 sparse structs (each 24 bytes) + 1 byte for isExtended flag
        headerBuf = new byte[TarArchiveSparseEntry.SPARSELEN_GNU_SPARSE + 1];
        try {
            entry = new TarArchiveSparseEntry(headerBuf);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Benchmark
    public TarArchiveSparseEntry construct() throws IOException {
        return new TarArchiveSparseEntry(headerBuf);
    }

    @Benchmark
    public java.util.List<org.apache.commons.compress.archivers.tar.TarArchiveStructSparse> getSparseHeaders() {
        return entry.getSparseHeaders();
    }

    @Benchmark
    public boolean isExtended() {
        return entry.isExtended();
    }
}
