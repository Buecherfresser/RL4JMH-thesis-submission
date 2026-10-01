package bench.generated.c027;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.compress.archivers.tar.TarArchiveSparseEntry;
import org.apache.commons.compress.archivers.tar.TarConstants;
import org.apache.commons.compress.archivers.tar.TarUtils;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class TarArchiveSparseEntryBenchmark {

    private TarArchiveSparseEntry entry;
    private byte[] headerBuffer;

    @Setup(Level.Trial)
    public void setup() throws IOException {
        // Create a representative header buffer.
        // Since we don't have the full implementation of TarUtils, we create a buffer
        // large enough to satisfy the constructor's expectations for sparse headers and extension flag.
        // We assume TarConstants.SPARSE_HEADERS_IN_EXTENSION_HEADER and SPARSELEN_GNU_SPARSE are defined.
        
        // Mocking a buffer that contains enough data for at least one sparse struct and an extended flag.
        // A typical tar header is 512 bytes. We need enough space for the sparse structs + extension flag.
        
        // We use a fixed size buffer for consistency.
        headerBuffer = new byte[1024]; 
        
        // In a real scenario, this buffer would be read from a tar file.
        // We rely on TarUtils.readSparseStructs and TarUtils.parseBoolean to handle the mock data correctly.
        
        entry = new TarArchiveSparseEntry(headerBuffer);
    }

    @Benchmark
    public void testGetSparseHeaders(Blackhole bh) {
        List<org.apache.commons.compress.archivers.tar.TarArchiveStructSparse> headers = entry.getSparseHeaders();
        bh.consume(headers);
    }

    @Benchmark
    public void testIsExtended(Blackhole bh) {
        boolean isExtended = entry.isExtended();
        bh.consume(isExtended);
    }
}
