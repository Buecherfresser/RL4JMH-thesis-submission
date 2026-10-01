package bench.generated.c027;

import org.apache.commons.compress.archivers.tar.TarArchiveSparseEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveStructSparse;
import org.apache.commons.compress.archivers.tar.TarConstants;
import org.apache.commons.compress.archivers.tar.TarUtils;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class TarArchiveSparseEntryBenchmark {

    // State for input generation
    private List<byte[]> sparseHeaderInputs;
    private final Random random = new Random();

    // Constants derived from the source structure (assuming these are defined in TarConstants)
    private static final int SPARSELEN_GNU_SPARSE = 12 * 2; // 12 chars offset + 12 chars numbytes
    private static final int SPARSE_HEADERS_IN_EXTENSION_HEADER = 21; // Assuming this constant exists

    @Setup
    public void setup() throws IOException {
        // Build a pool of representative header byte arrays.
        // Since we cannot fully replicate the complex TAR header structure here,
        // we create byte arrays of a fixed, representative size.
        sparseHeaderInputs = new ArrayList<>();
        int inputSize = 1024; // Representative size for a header block

        for (int i = 0; i < 10; i++) {
            byte[] headerBuf = new byte[inputSize];
            random.nextBytes(headerBuf);
            sparseHeaderInputs.add(headerBuf);
        }
    }

    @Benchmark
    public void testSparseEntryConstruction(Blackhole bh) throws IOException {
        // Use a pre-built input from the setup pool
        byte[] headerBuf = sparseHeaderInputs.get(random.nextInt(sparseHeaderInputs.size()));

        // Call the constructor, which performs the parsing logic
        TarArchiveSparseEntry entry = new TarArchiveSparseEntry(headerBuf);

        // Consume the result to prevent dead code elimination
        bh.consume(entry);
    }
}
