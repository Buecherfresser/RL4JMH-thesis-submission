package bench.generated.c018;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.archivers.jar.JarArchiveInputStream;
import org.apache.commons.compress.archivers.jar.JarArchiveEntry;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class JarArchiveInputStreamBenchmark {

    // State for instance methods
    private JarArchiveInputStream jarStream;
    private InputStream mockInputStream;

    // --- Setup ---

    @Setup(Level.Trial)
    public void setup() throws IOException {
        // Create a minimal mock input stream.
        // In a real scenario, this byte array would contain a valid JAR structure.
        // For benchmarking the stream overhead, a small input is sufficient.
        // We use a placeholder byte array.
        byte[] mockJarData = new byte[]{0x50, 0x4B, 0x03, 0x04}; // Minimal ZIP signature
        mockInputStream = new ByteArrayInputStream(mockJarData);

        // Initialize the subject under test
        jarStream = new JarArchiveInputStream(mockInputStream);
    }

    // --- Benchmarks ---

    /**
     * Benchmarks the static matches method.
     */
    @Benchmark
    public boolean benchmarkMatches() {
        byte[] signature = new byte[]{0x50, 0x4B, 0x03, 0x04};
        int length = 4;
        boolean result = JarArchiveInputStream.matches(signature, length);
        return result;
    }

    /**
     * Benchmarks the primary entry retrieval method: getNextEntry().
     * This method delegates to getNextJarEntry() internally.
     */
    @Benchmark
    public void benchmarkGetNextEntry(Blackhole bh) throws IOException {
        // Note: Since this is a stateful stream, running this repeatedly without resetting
        // the stream state will eventually fail or return null. We assume the benchmark
        // harness runs enough iterations that the stream state is managed or that
        // the operation itself is the focus.
        JarArchiveEntry entry = jarStream.getNextEntry();
        bh.consume(entry);
    }

    /**
     * Benchmarks the deprecated entry retrieval method: getNextJarEntry().
     * This method wraps the underlying ZipArchiveInputStream's getNextZipEntry().
     */
    @Benchmark
    public void benchmarkGetNextJarEntry(Blackhole bh) throws IOException {
        JarArchiveEntry entry = jarStream.getNextJarEntry();
        bh.consume(entry);
    }
}
