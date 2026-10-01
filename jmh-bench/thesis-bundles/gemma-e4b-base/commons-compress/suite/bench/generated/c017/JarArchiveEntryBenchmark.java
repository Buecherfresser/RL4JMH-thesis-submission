package bench.generated.c017;

import org.apache.commons.compress.archivers.jar.JarArchiveEntry;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.security.cert.Certificate;
import java.util.jar.Attributes;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class JarArchiveEntryBenchmark {

    private JarArchiveEntry entry;

    @Setup(Level.Trial)
    public void setup() throws Exception {
        // Initialize a representative entry instance
        // Using a simple name constructor
        entry = new JarArchiveEntry("test_entry.jar");
    }

    @Benchmark
    public void testGetCertificates(Blackhole bh) {
        // Test the deprecated getCertificates() method
        Certificate[] certificates = entry.getCertificates();
        bh.consume(certificates);
    }

    @Benchmark
    public void testGetManifestAttributes(Blackhole bh) {
        // Test the deprecated getManifestAttributes() method
        Attributes attributes = entry.getManifestAttributes();
        bh.consume(attributes);
    }
}
