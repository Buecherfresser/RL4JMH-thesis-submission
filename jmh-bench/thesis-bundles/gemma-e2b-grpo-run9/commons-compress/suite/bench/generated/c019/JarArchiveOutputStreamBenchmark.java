package bench.generated.c019;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

import org.apache.commons.compress.archivers.jar.JarArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class JarArchiveOutputStreamBenchmark {

    // Since JarArchiveOutputStream is not static and relies on an OutputStream,
    // we instantiate it inside the benchmark method to ensure a fresh state
    // for each invocation, avoiding complex setup/teardown issues.

    /**
     * Benchmark for the putArchiveEntry method, which tests the JarMarker logic.
     * This method is designed to test the internal state change (jarMarkerAdded).
     *
     * @param bh Blackhole to consume the result.
     */
    @Benchmark
    public void testPutArchiveEntry(Blackhole bh) {
        try {
            // 1. Setup: Create a dummy output stream.
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            
            // 2. Action: Instantiate the SUT.
            // We use the default constructor which wraps the stream.
            JarArchiveOutputStream jos = new JarArchiveOutputStream(baos);

            // 3. Action: Create a dummy entry.
            ZipArchiveEntry entry = new ZipArchiveEntry("test_file.txt");

            // 4. Action: Call the method under test.
            jos.putArchiveEntry(entry);

            // 5. Cleanup (Crucial for stream wrappers, though JMH handles the timing):
            jos.closeArchiveEntry();

        } catch (IOException e) {
            // Ignore exceptions for benchmarking purposes if they are expected during setup/teardown
        }
        // Consume the result (void method, so we just ensure execution path is taken)
        bh.consume(null);
    }
}
