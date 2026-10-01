package bench.generated.c017;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.archivers.jar.JarArchiveEntry;
import java.util.jar.JarEntry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import java.security.cert.Certificate;
import java.util.jar.Attributes;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class JarArchiveEntryBenchmark {

    private String entryName;
    private JarEntry jarEntry;
    private ZipEntry zipEntry;
    private ZipArchiveEntry zipArchiveEntry;
    private JarArchiveEntry reusableEntry;

    @Setup(Level.Trial)
    public void setUp() throws ZipException {
        entryName = "com/example/Test.class";
        jarEntry = new JarEntry(entryName);
        zipEntry = new ZipEntry(entryName);
        zipArchiveEntry = new ZipArchiveEntry(entryName);
        // Create a reusable JarArchiveEntry for getter benchmarks
        reusableEntry = new JarArchiveEntry(entryName);
    }

    @Benchmark
    public JarArchiveEntry createFromString() throws ZipException {
        return new JarArchiveEntry(entryName);
    }

    @Benchmark
    public JarArchiveEntry createFromJarEntry() throws ZipException {
        return new JarArchiveEntry(jarEntry);
    }

    @Benchmark
    public JarArchiveEntry createFromZipEntry() throws ZipException {
        return new JarArchiveEntry(zipEntry);
    }

    @Benchmark
    public JarArchiveEntry createFromZipArchiveEntry() throws ZipException {
        return new JarArchiveEntry(zipArchiveEntry);
    }

    @Benchmark
    public void getCertificates(Blackhole bh) {
        bh.consume(reusableEntry.getCertificates());
    }

    @Benchmark
    public void getManifestAttributes(Blackhole bh) {
        bh.consume(reusableEntry.getManifestAttributes());
    }
}
