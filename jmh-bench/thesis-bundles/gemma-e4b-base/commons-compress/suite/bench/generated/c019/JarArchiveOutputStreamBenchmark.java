package bench.generated.c019;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.JarMarker;
import org.apache.commons.compress.archivers.jar.JarArchiveOutputStream;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class JarArchiveOutputStreamBenchmark {

    private ZipArchiveEntry entry;
    private byte[] payloadData;
    private ByteArrayOutputStream baos;

    @Setup(Level.Trial)
    public void setup() throws IOException {
        // Setup reusable components
        entry = new ZipArchiveEntry("test_file.txt");
        entry.setSize(1024); // Set a representative size
        
        // Setup payload data
        payloadData = new byte[1024];
        for (int i = 0; i < 1024; i++) {
            payloadData[i] = (byte) (i % 256);
        }
        
        // Setup underlying output stream
        baos = new ByteArrayOutputStream();
    }

    /**
     * Benchmarks the process of creating a JarArchiveOutputStream, adding a single entry,
     * writing data, and closing the entry. This tests the core logic, including JarMarker addition.
     */
    @Benchmark
    public void writeSingleEntry(Blackhole bh) throws IOException {
        // Since JarArchiveOutputStream is stateful (jarMarkerAdded), we must create a fresh instance
        // for each invocation to ensure the JarMarker is added exactly once per stream lifecycle.
        ByteArrayOutputStream freshBaos = new ByteArrayOutputStream();
        JarArchiveOutputStream outputStream = new JarArchiveOutputStream(freshBaos);

        try {
            // 1. Add entry (This is where JarMarker is added if it's the first entry)
            outputStream.putArchiveEntry(entry);

            // 2. Write data
            outputStream.write(payloadData, 0, payloadData.length);

            // 3. Close entry
            outputStream.closeArchiveEntry();
        } finally {
            // Ensure stream is closed/finished
            outputStream.close();
        }

        // Consume the result
        bh.consume(freshBaos.toByteArray());
    }
}
