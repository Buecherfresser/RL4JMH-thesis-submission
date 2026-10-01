package bench.generated.c001;

import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 3, time = 1)
public class ArchiveStreamFactoryBenchmark {

    private ArchiveStreamFactory factory;

    @Setup
    public void setup() {
        // Initialize the factory instance. Since many methods are static,
        // this primarily ensures we have an instance if needed, and initializes
        // any internal state that might be instance-specific (though mostly static here).
        this.factory = new ArchiveStreamFactory();
    }

    @Benchmark
    public void createArchiveInputStream_AutoDetect(Blackhole bh) {
        try {
            // Test the auto-detect path (relies on stream signature detection)
            // Using a simple ByteArrayInputStream which won't match any signature,
            // testing the exception path or the default path.
            factory.createArchiveInputStream(new ByteArrayInputStream(new byte[0]));
        } catch (Exception e) {
            // Expected if no archiver is detected
        }
        bh.consume(factory);
    }

    @Benchmark
    public void createArchiveInputStream_SpecificName_Zip(Blackhole bh) {
        try {
            // Test a known supported format (ZIP)
            factory.createArchiveInputStream("ZIP", new ByteArrayInputStream(new byte[0]));
        } catch (Exception e) {
            // Ignore expected exceptions for non-existent streams
        }
        bh.consume(factory);
    }

    @Benchmark
    public void createArchiveOutputStream_SpecificName_Tar(Blackhole bh) {
        try {
            // Test a known supported format (TAR)
            // We use a dummy output stream, which should succeed if the factory logic is sound.
            factory.createArchiveOutputStream("TAR", new ByteArrayOutputStream());
        } catch (Exception e) {
            // Ignore expected exceptions
        }
        bh.consume(factory);
    }

    @Benchmark
    public void createArchiveOutputStream_DefaultEncoding(Blackhole bh) {
        try {
            // Test the default encoding path
            factory.createArchiveOutputStream(null, new ByteArrayOutputStream());
        } catch (Exception e) {
            // Ignore expected exceptions
        }
        bh.consume(factory);
    }

    @Benchmark
    public void createArchiveOutputStream_SpecificName_Jar(Blackhole bh) {
        try {
            // Test another known supported format (JAR)
            factory.createArchiveOutputStream("JAR", new ByteArrayOutputStream());
        } catch (Exception e) {
            // Ignore expected exceptions
        }
        bh.consume(factory);
    }
}
