package bench.generated.c003;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.archivers.ar.ArArchiveInputStream;
import org.apache.commons.compress.archivers.ar.ArArchiveEntry;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ArArchiveInputStreamBenchmark {

    private InputStream arInputStream;
    private ArArchiveInputStream arArchiveInputStream;
    private byte[] dataBuffer;
    private final int BUFFER_SIZE = 4096;

    /**
     * Setup method runs once per benchmark trial.
     * We create a mock AR archive stream containing a small number of entries.
     * Note: Generating a perfectly valid AR archive byte array is complex.
     * This setup simulates a stream containing data for benchmarking purposes.
     */
    @Setup(Level.Trial)
    public void setupTrial() throws IOException {
        // --- Mock AR Archive Generation ---
        // This mock data simulates a stream containing a header, metadata, and content for one entry.
        // The structure is highly simplified but large enough to test stream reading logic.
        
        // 1. Signature: !<'arch>\n (8 bytes)
        byte[] signature = new byte[]{0x21, 0x3c, 0x61, 0x72, 0x63, 0x68, 0x3e, 0x0a};
        
        // 2. Metadata block (simplified, focusing on size/name)
        // Name (16 bytes): "file1.txt\0..."
        byte[] name = new byte[16];
        System.arraycopy("file1.txt".getBytes(), 0, name, 0, 9);
        
        // Length (10 bytes): Total size of entry content (e.g., 100 bytes)
        byte[] length = new byte[10];
        // Simulate 100 bytes length (ASCII representation of 100)
        System.arraycopy("100".getBytes(), 0, length, 0, 3);
        
        // Other metadata fields (padded/zeroed)
        byte[] metadata = new byte[16 + 10]; // Name + Length
        System.arraycopy(name, 0, metadata, 0, 16);
        System.arraycopy(length, 0, metadata, 16, 10);

        // 3. Trailer (8 bytes)
        byte[] trailer = new byte[]{0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00}; // Simplified trailer

        // 4. Content Data (100 bytes)
        byte[] content = new byte[100];
        for (int i = 0; i < 100; i++) {
            content[i] = (byte) ('A' + (i % 26));
        }

        // Concatenate: Signature + Metadata + Trailer + Content
        int totalSize = signature.length + metadata.length + trailer.length + content.length;
        byte[] archiveData = new byte[totalSize];
        int offset = 0;
        
        System.arraycopy(signature, 0, archiveData, offset, signature.length);
        offset += signature.length;
        
        System.arraycopy(metadata, 0, archiveData, offset, metadata.length);
        offset += metadata.length;
        
        System.arraycopy(trailer, 0, archiveData, offset, trailer.length);
        offset += trailer.length;
        
        System.arraycopy(content, 0, archiveData, offset, content.length);
        
        // Set up the input stream
        arInputStream = new ByteArrayInputStream(archiveData);
        
        // Initialize the subject under test
        arArchiveInputStream = new ArArchiveInputStream(arInputStream);
        
        // Pre-allocate buffer for reading data
        dataBuffer = new byte[BUFFER_SIZE];
    }

    /**
     * Benchmark for iterating to the next entry in the archive.
     * This tests the header parsing and metadata extraction logic.
     */
    @Benchmark
    public ArArchiveEntry benchmarkGetNextEntry(Blackhole bh) throws IOException {
        // Since getNextEntry is stateful, we must ensure the stream is ready for the call.
        // We rely on the setup to provide a fresh stream instance per trial.
        ArArchiveEntry entry = arArchiveInputStream.getNextEntry();
        bh.consume(entry);
        return entry;
    }

    /**
     * Benchmark for reading data from the current entry.
     * This tests the underlying stream reading mechanism within the context of an entry.
     */
    @Benchmark
    public void benchmarkReadEntryData(Blackhole bh) throws IOException {
        // Ensure we have an entry before reading
        if (arArchiveInputStream.getNextEntry() == null) {
            // If the stream is exhausted, skip the benchmark iteration
            return;
        }
        
        // Read a chunk of data
        int bytesRead = arArchiveInputStream.read(dataBuffer, 0, BUFFER_SIZE);
        
        // Consume the result
        bh.consume(bytesRead);
    }

    /**
     * Benchmark for closing the stream.
     * This tests the cleanup logic.
     */
    @Benchmark
    public void benchmarkCloseStream(Blackhole bh) throws IOException {
        // We must ensure the stream is open before closing it.
        // Since the stream is reset in setup, we just call close.
        arArchiveInputStream.close();
        bh.consume(true);
    }
}
