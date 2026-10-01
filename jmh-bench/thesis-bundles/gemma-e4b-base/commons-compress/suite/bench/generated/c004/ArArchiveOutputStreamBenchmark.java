package bench.generated.c004;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.archivers.ar.ArArchiveOutputStream;
import org.apache.commons.compress.archivers.ar.ArArchiveEntry;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ArArchiveOutputStreamBenchmark {

    private ArArchiveEntry entry;
    private byte[] dataChunk;
    private File dummyFile;

    @Setup(Level.Trial)
    public void setup() throws IOException {
        // Create a dummy file object for ArArchiveEntry construction
        dummyFile = new File("dummy.ar");

        // Create a representative entry. We rely on default values for metadata
        // since setters are not publicly available on ArArchiveEntry.
        entry = new ArArchiveEntry(dummyFile, "test_file.txt");

        // Create a representative data chunk
        dataChunk = new byte[1024];
        for (int i = 0; i < 1024; i++) {
            dataChunk[i] = (byte) (i % 256);
        }
    }

    /**
     * Benchmarks the process of writing a single entry header and closing it.
     * This tests putArchiveEntry and closeArchiveEntry.
     */
    @Benchmark
    public void benchmarkWriteEntryHeader(Blackhole bh) throws IOException {
        // Must create a fresh instance for each invocation due to state mutation
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ArArchiveOutputStream arArchiveOutputStream = new ArArchiveOutputStream(baos);

        try {
            // 1. Put entry header
            arArchiveOutputStream.putArchiveEntry(entry);
            // 2. Close entry (since no data is written)
            arArchiveOutputStream.closeArchiveEntry();
        } finally {
            arArchiveOutputStream.close();
        }
        bh.consume(baos.toByteArray());
    }

    /**
     * Benchmarks the process of writing a data chunk after an entry has been started.
     * This tests the core write method.
     */
    @Benchmark
    public void benchmarkWriteDataChunk(Blackhole bh) throws IOException {
        // Must create a fresh instance for each invocation due to state mutation
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ArArchiveOutputStream arArchiveOutputStream = new ArArchiveOutputStream(baos);

        try {
            // 1. Put entry header (to set up the context)
            arArchiveOutputStream.putArchiveEntry(entry);
            // 2. Write data chunk
            arArchiveOutputStream.write(dataChunk, 0, dataChunk.length);
            // 3. Close entry
            arArchiveOutputStream.closeArchiveEntry();
        } finally {
            arArchiveOutputStream.close();
        }
        bh.consume(baos.toByteArray());
    }

    /**
     * Benchmarks the full cycle: Entry Header -> Data Write -> Close Entry.
     * This is the most representative operation.
     */
    @Benchmark
    public void benchmarkFullEntryCycle(Blackhole bh) throws IOException {
        // Must create a fresh instance for each invocation due to state mutation
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ArArchiveOutputStream arArchiveOutputStream = new ArArchiveOutputStream(baos);

        try {
            // 1. Put entry header
            arArchiveOutputStream.putArchiveEntry(entry);
            // 2. Write data chunk
            arArchiveOutputStream.write(dataChunk, 0, dataChunk.length);
            // 3. Close entry
            arArchiveOutputStream.closeArchiveEntry();
        } finally {
            arArchiveOutputStream.close();
        }
        bh.consume(baos.toByteArray());
    }

    /**
     * Benchmarks the finalization of the archive.
     * This tests the finish() method.
     */
    @Benchmark
    public void benchmarkFinishArchive(Blackhole bh) throws IOException {
        // Setup a state where an entry was written but not closed, simulating a partial archive
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ArArchiveOutputStream arArchiveOutputStream = new ArArchiveOutputStream(baos);

        try {
            // Put an entry header
            arArchiveOutputStream.putArchiveEntry(entry);
            // Do not close the entry
            // Call finish() which should throw an IOException if an entry is open
            arArchiveOutputStream.finish();
        } catch (IOException e) {
            // Expected behavior for testing finish() when entry is open
        } finally {
            arArchiveOutputStream.close();
        }
        bh.consume(true); // Consume the exception/result
    }
}
