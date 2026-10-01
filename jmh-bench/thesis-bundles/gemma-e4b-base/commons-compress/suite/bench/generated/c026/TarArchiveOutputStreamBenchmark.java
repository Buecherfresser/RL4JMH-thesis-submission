package bench.generated.c026;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class TarArchiveOutputStreamBenchmark {

    private TarArchiveOutputStream tarStream;
    private ByteArrayOutputStream baos;
    private TarArchiveEntry entry;
    private byte[] dataPayload;
    private final int PAYLOAD_SIZE = 4096;

    @Setup(Level.Trial)
    public void setup() throws IOException {
        // Use ByteArrayOutputStream as the underlying stream
        baos = new ByteArrayOutputStream();
        
        // Initialize TarArchiveOutputStream. Using default block size and ASCII charset.
        // We must use a constructor that takes an OutputStream.
        tarStream = new TarArchiveOutputStream(baos, 512, "UTF-8");

        // Create a representative entry
        entry = new TarArchiveEntry("test_file.txt");
        entry.setSize(PAYLOAD_SIZE);
        
        // Create a representative data payload
        dataPayload = new byte[PAYLOAD_SIZE];
        for (int i = 0; i < PAYLOAD_SIZE; i++) {
            dataPayload[i] = (byte) (i % 256);
        }
    }

    @TearDown(Level.Trial)
    public void tearDown() throws IOException {
        if (tarStream != null) {
            // Ensure the stream is closed properly after each trial
            tarStream.close();
        }
    }

    /**
     * Benchmarks the overhead of writing the entry header (putArchiveEntry).
     * Note: This method leaves the stream in a state where it expects data to be written next.
     */
    @Benchmark
    public void benchmarkPutArchiveEntry(Blackhole bh) throws IOException {
        // Reset state for clean measurement if necessary, but since we are measuring the call itself,
        // we rely on the setup state.
        
        // We must ensure the stream is ready for a new entry.
        // Since we are measuring putArchiveEntry, we assume the stream is ready.
        tarStream.putArchiveEntry(entry);
        
        // Consume the result (though putArchiveEntry is void, we consume the side effect if possible, 
        // but here we just ensure the call happens and the state change is registered).
        bh.consume(tarStream.getBytesWritten());
    }

    /**
     * Benchmarks the overhead of writing the content data for the current entry (write).
     * This assumes putArchiveEntry has already been called.
     */
    @Benchmark
    public void benchmarkWriteEntryContent(Blackhole bh) throws IOException {
        // Write the payload data
        tarStream.write(dataPayload, 0, PAYLOAD_SIZE);
        
        // Consume the result (bytes written count)
        bh.consume(tarStream.getBytesWritten());
    }

    /**
     * Benchmarks the overhead of closing the current entry (closeArchiveEntry).
     * This assumes data has been written and the stream is in the correct state.
     */
    @Benchmark
    public void benchmarkCloseArchiveEntry(Blackhole bh) throws IOException {
        // Close the entry, which flushes buffers and updates metadata
        tarStream.closeArchiveEntry();
        
        // Consume the result (bytes written count)
        bh.consume(tarStream.getBytesWritten());
    }

    /**
     * Benchmarks the overhead of finalizing the entire archive (finish).
     * This assumes all entries have been written and closed.
     */
    @Benchmark
    public void benchmarkFinishArchive(Blackhole bh) throws IOException {
        // Simulate a full archive process first (Put -> Write -> Close)
        tarStream.putArchiveEntry(entry);
        tarStream.write(dataPayload, 0, PAYLOAD_SIZE);
        tarStream.closeArchiveEntry();
        
        // Now measure the finalization step
        tarStream.finish();
        
        // Consume the result (total bytes written)
        bh.consume(tarStream.getBytesWritten());
    }
}
