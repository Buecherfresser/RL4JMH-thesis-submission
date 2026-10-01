package bench.generated.c027;

import org.apache.commons.compress.archivers.tar.TarArchiveSparseEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveStructSparse;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class TarArchiveSparseEntryBenchmark {

    private TarArchiveSparseEntry sparseEntry;

    @Setup
    public void setup() throws IOException {
        // --- Setup Input Data ---
        // Create a dummy header buffer. 1024 bytes is sufficient for testing parsing logic.
        int bufferSize = 1024;
        byte[] headerBuf = new byte[bufferSize];
        // Fill with some dummy data
        for (int i = 0; i < bufferSize; i++) {
            headerBuf[i] = (byte) (i % 256);
        }
        // Initialize the state object once
        this.sparseEntry = new TarArchiveSparseEntry(headerBuf);
    }

    @Benchmark
    public void testIsExtended(Blackhole bh) {
        // Test the isExtended method on the pre-built state object.
        boolean result = sparseEntry.isExtended();
        bh.consume(result);
    }

    @Benchmark
    public List<TarArchiveStructSparse> testGetSparseHeaders(Blackhole bh) {
        // Test the getSparseHeaders method on the pre-built state object.
        List<TarArchiveStructSparse> headers = sparseEntry.getSparseHeaders();
        bh.consume(headers);
        return headers;
    }
}
