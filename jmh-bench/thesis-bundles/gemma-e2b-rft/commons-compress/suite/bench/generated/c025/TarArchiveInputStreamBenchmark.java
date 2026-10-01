package bench.generated.c025;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.utils.IOUtils;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class TarArchiveInputStreamBenchmark {

    private ByteArrayOutputStream baos;
    private byte[] tarArchiveData;
    private InputStream inputStream;

    // Fixed payload setup
    @Setup
    public void setup() throws IOException {
        // 1. Create a representative TAR archive payload in memory.
        baos = new ByteArrayOutputStream();
        try (org.apache.commons.compress.archivers.tar.TarArchiveOutputStream tarOut =
             new org.apache.commons.compress.archivers.tar.TarArchiveOutputStream(baos)) {

            // Entry 1: Simple file
            org.apache.commons.compress.archivers.tar.TarArchiveEntry entry1 =
                    new org.apache.commons.compress.archivers.tar.TarArchiveEntry("file1.txt");
            entry1.setSize(100);
            tarOut.putArchiveEntry(entry1);
            baos.write("Content of file 1".getBytes());
            tarOut.closeArchiveEntry();

            // Entry 2: Directory (empty)
            org.apache.commons.compress.archivers.tar.TarArchiveEntry entry2 =
                    new org.apache.commons.compress.archivers.tar.TarArchiveEntry("subdir/");
            entry2.setSize(0);
            tarOut.putArchiveEntry(entry2);
            tarOut.closeArchiveEntry();

            // Entry 3: Another file
            org.apache.commons.compress.archivers.tar.TarArchiveEntry entry3 =
                    new org.apache.commons.compress.archivers.tar.TarArchiveEntry("file2.bin");
            entry3.setSize(50);
            tarOut.putArchiveEntry(entry3);
            baos.write(new byte[50]); // 50 bytes of data
            tarOut.closeArchiveEntry();

            tarArchiveData = baos.toByteArray();
        }
        // The input stream will be created from the fixed payload for benchmarking
        inputStream = new ByteArrayInputStream(tarArchiveData);
    }

    /**
     * Benchmark 1: Basic read operation (iterating through entries).
     * Measures the time taken to iterate through all entries and read their content.
     */
    @Benchmark
    public void benchmarkReadAllEntries(Blackhole bh) throws IOException {
        try (TarArchiveInputStream tarIn = new TarArchiveInputStream(inputStream)) {
            ArchiveEntry entry;
            while ((entry = tarIn.getNextEntry()) != null) {
                // Read the content of the entry
                byte[] buffer = new byte[1024];
                int bytesRead = tarIn.read(buffer, 0, 1024);
                if (bytesRead != -1) {
                    bh.consume(Arrays.copyOf(buffer, bytesRead));
                }
            }
        }
    }

    /**
     * Benchmark 2: Reading a specific entry by name (simulating seeking).
     * Measures the time taken to find and read a specific entry.
     */
    @Benchmark
    public void benchmarkReadSpecificEntry(Blackhole bh) throws IOException {
        try (TarArchiveInputStream tarIn = new TarArchiveInputStream(inputStream)) {
            // We expect the first entry to be "file1.txt"
            ArchiveEntry entry = tarIn.getNextEntry();
            if (entry != null) {
                byte[] buffer = new byte[1024];
                int bytesRead = tarIn.read(buffer, 0, 1024);
                if (bytesRead != -1) {
                    bh.consume(Arrays.copyOf(buffer, bytesRead));
                }
            }
        }
    }

    /**
     * Benchmark 3: Checking if the stream can read the entry data (canReadEntryData).
     * Measures the overhead of checking the entry type.
     */
    @Benchmark
    public void benchmarkCanReadEntryData(Blackhole bh) throws IOException {
        try (TarArchiveInputStream tarIn = new TarArchiveInputStream(inputStream)) {
            ArchiveEntry entry = tarIn.getNextEntry();
            if (entry != null) {
                boolean result = tarIn.canReadEntryData(entry);
                bh.consume(result);
            }
        }
    }
}
