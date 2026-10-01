package bench.generated.c036;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.utils.IOUtils;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ZipArchiveInputStreamBenchmark {

    private byte[] zipPayload;

    /**
     * Generates a small, multi-entry ZIP payload in memory.
     * This payload is used as the input for all benchmarks.
     */
    @Setup(Level.Trial)
    public void setup() throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (ZipArchiveOutputStream zos = new ZipArchiveOutputStream(bos)) {
            // Entry 1: Small, uncompressed
            ZipArchiveEntry entry1 = new ZipArchiveEntry("file1.txt");
            entry1.setSize(10);
            zos.putArchiveEntry(entry1);
            zos.write("abcdefghij".getBytes());
            zos.closeArchiveEntry();

            // Entry 2: Larger, uncompressed
            ZipArchiveEntry entry2 = new ZipArchiveEntry("file2.dat");
            byte[] data2 = new byte[1024];
            Arrays.fill(data2, (byte) 0xAA);
            entry2.setSize(data2.length);
            zos.putArchiveEntry(entry2);
            zos.write(data2);
            zos.closeArchiveEntry();

            // Entry 3: Empty entry
            ZipArchiveEntry entry3 = new ZipArchiveEntry("empty.log");
            entry3.setSize(0);
            zos.putArchiveEntry(entry3);
            zos.closeArchiveEntry();
        }
        this.zipPayload = bos.toByteArray();
    }

    /**
     * Benchmarks the process of retrieving the next entry from the stream.
     * This tests header parsing and state transition.
     */
    @Benchmark
    public ZipArchiveEntry benchmarkGetNextEntry(Blackhole bh) throws IOException {
        // Create a fresh stream for each invocation to ensure state isolation
        try (InputStream is = new ByteArrayInputStream(zipPayload);
             ZipArchiveInputStream zais = new ZipArchiveInputStream(is)) {

            ZipArchiveEntry entry = zais.getNextEntry();
            bh.consume(entry);
            return entry;
        }
    }

    /**
     * Benchmarks reading a single byte from the current entry.
     * This tests the underlying stream reading logic (deflated/stored/etc.).
     */
    @Benchmark
    public int benchmarkReadByte(Blackhole bh) throws IOException {
        // Create a fresh stream for each invocation
        try (InputStream is = new ByteArrayInputStream(zipPayload);
             ZipArchiveInputStream zais = new ZipArchiveInputStream(is)) {

            // Must get the first entry first
            ZipArchiveEntry entry = zais.getNextEntry();
            if (entry == null) return -1;

            // Read one byte
            int b = zais.read();
            bh.consume(b);
            return b;
        }
    }

    /**
     * Benchmarks reading a chunk of data from the current entry.
     * This tests bulk data transfer efficiency.
     */
    @Benchmark
    public int benchmarkReadChunk(Blackhole bh) throws IOException {
        final int bufferSize = 512;
        // Create a fresh stream for each invocation
        try (InputStream is = new ByteArrayInputStream(zipPayload);
             ZipArchiveInputStream zais = new ZipArchiveInputStream(is)) {

            // Must get the first entry first
            ZipArchiveEntry entry = zais.getNextEntry();
            if (entry == null) return -1;

            byte[] buffer = new byte[bufferSize];
            // Read a chunk
            int bytesRead = zais.read(buffer, 0, bufferSize);
            bh.consume(bytesRead);
            return bytesRead;
        }
    }

    /**
     * Benchmarks skipping a large number of bytes within the current entry.
     */
    @Benchmark
    public long benchmarkSkipBytes(Blackhole bh) throws IOException {
        final long skipAmount = 100;
        // Create a fresh stream for each invocation
        try (InputStream is = new ByteArrayInputStream(zipPayload);
             ZipArchiveInputStream zais = new ZipArchiveInputStream(is)) {

            // Must get the first entry first
            ZipArchiveEntry entry = zais.getNextEntry();
            if (entry == null) return 0;

            // Skip bytes
            long skipped = zais.skip(skipAmount);
            bh.consume(skipped);
            return skipped;
        }
    }

    /**
     * Benchmarks checking if the current entry data can be read.
     */
    @Benchmark
    public boolean benchmarkCanReadEntryData(Blackhole bh) throws IOException {
        // Create a fresh stream for each invocation
        try (InputStream is = new ByteArrayInputStream(zipPayload);
             ZipArchiveInputStream zais = new ZipArchiveInputStream(is)) {

            // Must get the first entry first
            ZipArchiveEntry entry = zais.getNextEntry();
            if (entry == null) return false;

            // Check capability
            boolean canRead = zais.canReadEntryData(entry);
            bh.consume(canRead);
            return canRead;
        }
    }
}
