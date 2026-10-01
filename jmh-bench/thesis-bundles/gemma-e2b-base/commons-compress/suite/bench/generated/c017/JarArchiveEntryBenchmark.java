package bench.generated.c017;

import org.apache.commons.compress.archivers.jar.JarArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import java.util.jar.JarEntry;
import java.util.zip.ZipEntry;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipException;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class JarArchiveEntryBenchmark {

    // State fields for inputs
    private String jarEntryName;
    private JarEntry jarEntry;
    private ZipArchiveEntry zipArchiveEntry;
    private ZipEntry zipEntry;
    private JarArchiveEntry jarArchiveEntryByName;
    private JarArchiveEntry jarArchiveEntryFromJarEntry;
    private JarArchiveEntry jarArchiveEntryFromZipArchiveEntry;
    private JarArchiveEntry jarArchiveEntryFromZipEntry;

    @Setup
    public void setup() {
        // Setup input data
        this.jarEntryName = "test_file.txt";
        this.jarEntry = new JarEntry("test_file.txt");
        this.zipArchiveEntry = new ZipArchiveEntry("zip_file.zip");
        this.zipEntry = new ZipEntry("zip_file.zip");

        // Pre-calculate instances to avoid setup overhead in benchmarks
        try {
            this.jarArchiveEntryByName = new JarArchiveEntry(this.jarEntryName);
            this.jarArchiveEntryFromJarEntry = new JarArchiveEntry(this.jarEntry);
            this.jarArchiveEntryFromZipArchiveEntry = new JarArchiveEntry(this.zipArchiveEntry);
            this.jarArchiveEntryFromZipEntry = new JarArchiveEntry(this.zipEntry);
        } catch (ZipException e) {
            throw new RuntimeException("Setup failed due to ZipException", e);
        }
    }

    @Benchmark
    public void createFromStringName(Blackhole bh) {
        JarArchiveEntry entry = new JarArchiveEntry(jarEntryName);
        bh.consume(entry);
    }

    @Benchmark
    public void createFromJarEntry(Blackhole bh) {
        try {
            JarArchiveEntry entry = new JarArchiveEntry(jarEntry);
            bh.consume(entry);
        } catch (ZipException e) {
            throw new RuntimeException("Benchmark failed due to ZipException", e);
        }
    }

    @Benchmark
    public void createFromZipArchiveEntry(Blackhole bh) {
        try {
            JarArchiveEntry entry = new JarArchiveEntry(zipArchiveEntry);
            bh.consume(entry);
        } catch (ZipException e) {
            throw new RuntimeException("Benchmark failed due to ZipException", e);
        }
    }

    @Benchmark
    public void createFromZipEntry(Blackhole bh) {
        try {
            JarArchiveEntry entry = new JarArchiveEntry(zipEntry);
            bh.consume(entry);
        } catch (ZipException e) {
            throw new RuntimeException("Benchmark failed due to ZipException", e);
        }
    }
}
