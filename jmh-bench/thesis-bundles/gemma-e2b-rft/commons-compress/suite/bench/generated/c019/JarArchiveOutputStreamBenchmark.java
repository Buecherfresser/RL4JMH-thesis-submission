package bench.generated.c019;

import org.apache.commons.compress.archivers.jar.JarArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.JarMarker;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class JarArchiveOutputStreamBenchmark {

    private ByteArrayOutputStream outputStream;
    private JarArchiveOutputStream jarOutputStream;
    private List<ZipArchiveEntry> entries;

    @Setup
    public void setup() throws IOException {
        // Define the entries to be written
        this.entries = new ArrayList<>();
        
        // Entry 1: Larger content
        ZipArchiveEntry entry1 = new ZipArchiveEntry("file1.txt");
        entry1.setSize(1024); // Use a slightly larger size for better simulation
        this.entries.add(entry1);

        // Entry 2: Smaller content
        ZipArchiveEntry entry2 = new ZipArchiveEntry("file2.dat");
        entry2.setSize(10);
        this.entries.add(entry2);

        // Initialize the output stream wrapper
        this.outputStream = new ByteArrayOutputStream();
        this.jarOutputStream = new JarArchiveOutputStream(this.outputStream);
    }

    @Benchmark
    public void writeJarArchive(Blackhole bh) throws IOException {
        // Simulate writing the archive by iterating through entries and calling putArchiveEntry
        for (ZipArchiveEntry entry : entries) {
            // This call exercises the core logic of JarArchiveOutputStream,
            // specifically checking if JarMarker is added on the first call.
            jarOutputStream.putArchiveEntry(entry);
        }

        // Finish the archive structure
        jarOutputStream.finish();

        // Consume the result to prevent dead code elimination
        byte[] result = outputStream.toByteArray();
        bh.consume(result);
    }
}
