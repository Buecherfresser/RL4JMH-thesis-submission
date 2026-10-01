package bench.generated.c025;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.utils.IOUtils;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class TarArchiveInputStreamBenchmark {

    private byte[] tarData;
    private TarArchiveInputStream tarInputStream;
    private final byte[] buffer = new byte[4096];

    /**
     * Setup method to generate a minimal, valid in-memory tar archive payload.
     * This payload contains one entry with some data.
     */
    @Setup(Level.Trial)
    public void setup() throws IOException {
        // 1. Create a minimal tar archive in memory
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        TarArchiveOutputStream tarOut = new TarArchiveOutputStream(bos);

        // 2. Create a dummy entry
        TarArchiveEntry entry = new TarArchiveEntry("test_file.txt");
        entry.setSize(1024); // 1KB of data
        tarOut.putArchiveEntry(entry);

        // 3. Write dummy data
        byte[] data = new byte[1024];
        tarOut.write(data);

        // 4. Close the entry and finish the archive
        tarOut.closeArchiveEntry();
        tarOut.finish();
        tarOut.close();

        this.tarData = bos.toByteArray();

        // 5. Initialize the stream instance (will be reset/reinitialized in Invocation setup)
        this.tarInputStream = new TarArchiveInputStream(new ByteArrayInputStream(tarData));
    }

    /**
     * Re-initializes the stream for each invocation to ensure a clean state (start of archive).
     * This is crucial for stateful readers like TarArchiveInputStream.
     */
    @Setup(Level.Invocation)
    public void setupInvocation() {
        // Re-wrap the byte array in a new stream and re-initialize the SUT
        ByteArrayInputStream bais = new ByteArrayInputStream(tarData);
        this.tarInputStream = new TarArchiveInputStream(bais);
    }

    /**
     * Benchmarks the primary iteration method: getting the next entry.
     * This tests the header parsing logic.
     */
    @Benchmark
    public TarArchiveEntry benchmarkGetNextEntry() throws IOException {
        // The result is returned to prevent dead code elimination
        return tarInputStream.getNextEntry();
    }

    /**
     * Benchmarks reading a chunk of data from the current entry.
     * This tests the core read logic.
     */
    @Benchmark
    public int benchmarkReadData() throws IOException {
        // Read a chunk of data and consume it via Blackhole
        int bytesRead = tarInputStream.read(buffer, 0, buffer.length);
        return bytesRead;
    }

    /**
     * Benchmarks skipping a large amount of data within the current entry.
     * This tests the skip logic, especially for non-sparse entries.
     */
    @Benchmark
    public long benchmarkSkipData() throws IOException {
        // Skip a significant portion of the entry data
        long skipped = tarInputStream.skip(500);
        return skipped;
    }

    /**
     * Benchmarks checking the available bytes remaining in the current entry.
     */
    @Benchmark
    public int benchmarkAvailable() throws IOException {
        // The result is returned
        return tarInputStream.available();
    }

    /**
     * Benchmarks accessing the metadata of the current entry.
     */
    @Benchmark
    public TarArchiveEntry benchmarkGetCurrentEntry() {
        // The result is returned
        return tarInputStream.getCurrentEntry();
    }
}
