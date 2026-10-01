package bench.generated.c019;

import org.apache.commons.compress.archivers.jar.JarArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.JarMarker;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class JarArchiveOutputStreamBenchmark {

    private ByteArrayOutputStream baos;
    private JarArchiveOutputStream jarOutputStream;
    private ZipArchiveEntry entry;

    @Setup
    public void setup() throws IOException {
        // 1. Setup the output stream buffer
        this.baos = new ByteArrayOutputStream();

        // 2. Setup a representative ZipArchiveEntry
        this.entry = new ZipArchiveEntry("test_file.txt");

        // 3. Initialize the JarArchiveOutputStream wrapping the buffer
        this.jarOutputStream = new JarArchiveOutputStream(baos);
    }

    @Benchmark
    public void benchmarkPutArchiveEntry() throws IOException {
        // Call the method under test exactly once per invocation
        jarOutputStream.putArchiveEntry(entry);

        // Ensure the stream is flushed/closed if necessary for accurate measurement,
        // although JMH usually handles stream state well if the operation is self-contained.
        // For this specific benchmark focusing only on putArchiveEntry, we just consume the result.
    }
}
