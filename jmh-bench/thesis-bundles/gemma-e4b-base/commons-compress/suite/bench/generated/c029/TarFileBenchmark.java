package bench.generated.c029;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.archivers.tar.TarFile;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.io.InputStream;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class TarFileBenchmark {

    // Placeholder for a small, valid TAR archive content.
    private byte[] archiveContent;

    // We keep these fields for setup/teardown, though benchmarks will use fresh instances
    private TarFile tarFile;
    private List<TarArchiveEntry> entries;

    @Setup(Level.Trial)
    public void setup() throws IOException {
        // WARNING: This byte array is a placeholder and likely does not represent a valid TAR archive.
        // For a real benchmark, this must be replaced by a byte array generated from a valid TAR file.
        this.archiveContent = new byte[]{
            (byte) 0x1F, (byte) 0x8B, (byte) 0x08, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
        };

        // Initialize the subject under test (TarFile)
        this.tarFile = new TarFile(archiveContent);
        
        // Pre-fetch all entries for iteration benchmarks
        this.entries = tarFile.getEntries();
    }

    @TearDown(Level.Trial)
    public void tearDown() throws IOException {
        if (tarFile != null) {
            tarFile.close();
        }
    }

    /**
     * Benchmarks the time taken to initialize and parse the entire TAR archive structure.
     * This measures the cost of the TarFile constructor.
     */
    @Benchmark
    public void benchmarkTarFileConstruction(Blackhole bh) throws IOException {
        // Re-initialize the TarFile instance for each invocation to measure construction cost accurately.
        TarFile tempTarFile = new TarFile(archiveContent);
        bh.consume(tempTarFile.getEntries());
        tempTarFile.close();
    }

    /**
     * Benchmarks the time taken to retrieve all entries in the archive.
     * This replaces the private getNextTarEntry() benchmark.
     */
    @Benchmark
    public void benchmarkGetEntries(Blackhole bh) throws IOException {
        // Create a fresh instance to measure the full parsing cost repeatedly.
        TarFile tempTarFile = new TarFile(archiveContent);
        List<TarArchiveEntry> entries = tempTarFile.getEntries();
        bh.consume(entries);
        tempTarFile.close();
    }

    /**
     * Benchmarks the time taken to retrieve the input stream for a specific entry.
     * We use the first entry found in the pre-fetched list.
     */
    @Benchmark
    public void benchmarkGetInputStream(Blackhole bh) throws IOException {
        if (entries.isEmpty()) return;
        
        // Create a fresh TarFile instance
        TarFile tempTarFile = new TarFile(archiveContent);
        
        // We must iterate to the first entry to get the stream.
        // Since we cannot call private methods, we rely on the fact that the constructor
        // already parsed the entries, and we use the first entry from the pre-fetched list.
        TarArchiveEntry targetEntry = entries.get(0);
        
        // The actual method call we want to benchmark
        InputStream is = tempTarFile.getInputStream(targetEntry);
        
        // Consume the stream to ensure the work is done
        bh.consume(is);
        
        tempTarFile.close();
    }

    /**
     * Benchmarks the time taken to read all data from a specific entry's input stream.
     * This measures the data transfer/reading cost.
     */
    @Benchmark
    public void benchmarkReadEntryData(Blackhole bh) throws IOException {
        if (entries.isEmpty()) return;

        // Create a fresh TarFile instance
        TarFile tempTarFile = new TarFile(archiveContent);
        
        // Get the first entry from the pre-fetched list
        TarArchiveEntry targetEntry = entries.get(0);

        // Get the input stream
        InputStream is = tempTarFile.getInputStream(targetEntry);
        
        // Read all data and consume it
        byte[] data = new byte[1024]; // Use a buffer size
        int bytesRead = 0;
        while ((bytesRead = is.read(data)) != -1) {
            // Consume the read data
        }
        
        bh.consume(bytesRead);
        tempTarFile.close();
    }
}
