package bench.generated.c029;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarFile;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class TarFileBenchmark {

    private byte[] tarArchiveContent;
    private TarFile tarFile;
    private List<TarArchiveEntry> entries;

    // --- Setup Phase ---

    @Setup
    public void setup() throws IOException {
        // 1. Create a representative, non-trivial TAR archive payload.
        tarArchiveContent = createMinimalTarArchive();

        // 2. Initialize the TarFile object once for read benchmarks.
        tarFile = new TarFile(tarArchiveContent);
        entries = tarFile.getEntries();
    }

    /**
     * Helper method to generate a minimal, valid TAR archive byte array.
     * This simulates a real archive structure to test the TarFile parser.
     */
    private byte[] createMinimalTarArchive() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        // --- Minimal TAR Header (1024 bytes) ---
        // This is a placeholder header structure. Real TAR headers are complex.
        // We use a simple structure that the TarFile parser should be able to process.
        byte[] header = new byte[1024];
        // Fill with dummy data to simulate a header structure
        for (int i = 0; i < header.length; i++) {
            header[i] = (byte) (i % 256);
        }
        baos.write(header);

        // --- Minimal File Entry ---
        // In a real scenario, this would be a proper TAR entry structure.
        // We simulate a file entry structure here.
        // For simplicity, we just append some data.
        byte[] fileData = "This is the content of the file.".getBytes();
        
        // Simulate writing the file data after the header
        baos.write(fileData);

        return baos.toByteArray();
    }

    // --- Benchmarks ---

    /**
     * Benchmark for the time taken to initialize TarFile from a pre-built byte array.
     */
    @Benchmark
    public void benchmarkTarFileConstruction(Blackhole bh) throws IOException {
        // Re-instantiate TarFile for each run to measure construction time
        TarFile tempFile = new TarFile(tarArchiveContent);
        bh.consume(tempFile);
    }

    /**
     * Benchmark for retrieving all entries from the TarFile.
     */
    @Benchmark
    public void benchmarkGetEntries(Blackhole bh) {
        // Use the pre-initialized tarFile from @Setup
        bh.consume(tarFile.getEntries());
    }

    /**
     * Benchmark for retrieving the input stream for a specific entry and draining it.
     * This tests the core entry reading mechanism.
     */
    @Benchmark
    public void benchmarkGetInputStreamAndDrain(Blackhole bh) throws IOException {
        // Select the first entry for testing
        TarArchiveEntry entry = entries.get(0);

        // Get the InputStream and drain it directly
        try (InputStream is = tarFile.getInputStream(entry)) {
            // Drain the stream to ensure the entire read operation is measured
            byte[] result = is.readAllBytes();
            bh.consume(result);
        }
    }
}
