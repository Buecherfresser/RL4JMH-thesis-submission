package bench.generated.c025;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.utils.IOUtils;
import java.util.concurrent.TimeUnit;

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
        
        // Use a standard TarArchiveOutputStream to create a valid TAR file structure
        try (org.apache.commons.compress.archivers.tar.TarArchiveOutputStream tarOut = 
             new org.apache.commons.compress.archivers.tar.TarArchiveOutputStream(baos)) {

            // Entry 1: Simple file (Size 100 bytes)
            org.apache.commons.compress.archivers.tar.TarArchiveEntry entry1 = 
                new org.apache.commons.compress.archivers.tar.TarArchiveEntry("file1.txt");
            entry1.setSize(100); 
            tarOut.putArchiveEntry(entry1);
            baos.write(new byte[100]); // Write content
            tarOut.closeArchiveEntry();

            // Entry 2: Directory (empty, Size 0 bytes)
            org.apache.commons.compress.archivers.tar.TarArchiveEntry entry2 = 
                new org.apache.commons.compress.archivers.tar.TarArchiveEntry("subdir/");
            entry2.setSize(0);
            tarOut.putArchiveEntry(entry2);
            tarOut.closeArchiveEntry();

            // Entry 3: Another file (Size 50 bytes)
            org.apache.commons.compress.archivers.tar.TarArchiveEntry entry3 = 
                new org.apache.commons.compress.archivers.tar.TarArchiveEntry("file2.bin");
            entry3.setSize(50); 
            tarOut.putArchiveEntry(entry3);
            baos.write(new byte[50]); // Write content
            tarOut.closeArchiveEntry();
        }
        
        tarArchiveData = baos.toByteArray();
        inputStream = new ByteArrayInputStream(tarArchiveData);
    }

    /**
     * Benchmark 1: Basic read operation (iterating through entries).
     */
    @Benchmark
    public void benchmarkIterateEntries(Blackhole bh) throws IOException {
        try (TarArchiveInputStream tarIn = new TarArchiveInputStream(inputStream)) {
            // Iterate through all entries
            while (tarIn.getNextEntry() != null) {
                // Consume the entry to ensure work is done
                bh.consume(tarIn.getCurrentEntry());
            }
        }
    }

    /**
     * Benchmark 2: Reading data from the first entry.
     */
    @Benchmark
    public void benchmarkReadFirstEntryData(Blackhole bh) throws IOException {
        try (TarArchiveInputStream tarIn = new TarArchiveInputStream(inputStream)) {
            ArchiveEntry entry = tarIn.getNextEntry();
            if (entry != null) {
                // Read a portion of the data
                int bytesRead = tarIn.read(new byte[10], 0, 10);
                bh.consume(bytesRead);
            }
        }
    }

    /**
     * Benchmark 3: Reading data from the second entry (directory).
     */
    @Benchmark
    public void benchmarkReadSecondEntryData(Blackhole bh) throws IOException {
        try (TarArchiveInputStream tarIn = new TarArchiveInputStream(inputStream)) {
            ArchiveEntry entry1 = tarIn.getNextEntry();
            if (entry1 != null) {
                // Consume the first entry
                tarIn.read(new byte[10], 0, 10);
            }
            ArchiveEntry entry2 = tarIn.getNextEntry();
            if (entry2 != null) {
                // Read data from the second entry (expecting 0 bytes)
                int bytesRead = tarIn.read(new byte[10], 0, 10);
                bh.consume(bytesRead);
            }
        }
    }

    /**
     * Benchmark 4: Reading data from the third entry.
     */
    @Benchmark
    public void benchmarkReadThirdEntryData(Blackhole bh) throws IOException {
        try (TarArchiveInputStream tarIn = new TarArchiveInputStream(inputStream)) {
            ArchiveEntry entry1 = tarIn.getNextEntry();
            if (entry1 != null) {
                // Consume the first entry
                tarIn.read(new byte[10], 0, 10);
            }
            ArchiveEntry entry2 = tarIn.getNextEntry();
            if (entry2 != null) {
                // Consume the second entry
                tarIn.read(new byte[10], 0, 10);
            }
            ArchiveEntry entry3 = tarIn.getNextEntry();
            if (entry3 != null) {
                // Read data from the third entry
                int bytesRead = tarIn.read(new byte[10], 0, 10);
                bh.consume(bytesRead);
            }
        }
    }
    
    /**
     * Benchmark 5: Testing the `skip` method on a non-sparse entry.
     */
    @Benchmark
    public void benchmarkSkipNonSparseEntry(Blackhole bh) throws IOException {
        try (TarArchiveInputStream tarIn = new TarArchiveInputStream(inputStream)) {
            ArchiveEntry entry = tarIn.getNextEntry();
            if (entry != null) {
                // Skip half the expected size of the entry (100 bytes / 2 = 50 bytes)
                long skipped = tarIn.skip(50);
                bh.consume(skipped);
            }
        }
    }

    /**
     * Benchmark 6: Testing the `skip` method when skipping past the end of the current entry.
     */
    @Benchmark
    public void benchmarkSkipPastEntryEnd(Blackhole bh) throws IOException {
        try (TarArchiveInputStream tarIn = new TarArchiveInputStream(inputStream)) {
            ArchiveEntry entry = tarIn.getNextEntry();
            if (entry != null) {
                // Skip more than the total size of the entry (100 bytes)
                long skipped = tarIn.skip(200);
                bh.consume(skipped);
            }
        }
    }
}
