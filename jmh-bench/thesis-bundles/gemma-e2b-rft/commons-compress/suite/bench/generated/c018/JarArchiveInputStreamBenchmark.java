package bench.generated.c018;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.apache.commons.compress.archivers.jar.JarArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class JarArchiveInputStreamBenchmark {

    private InputStream mockJarInputStream;
    private JarArchiveInputStream jarArchiveInputStream;
    private ZipArchiveEntry mockEntry;

    @Setup
    public void setup() throws IOException {
        // Create a minimal mock input stream representing a JAR file content.
        // This byte array is small and repeatable for benchmarking stream processing overhead.
        byte[] jarData = new byte[1024 * 10]; // 10KB of dummy data
        Arrays.fill(jarData, (byte) 0x00);
        
        mockJarInputStream = new ByteArrayInputStream(jarData);
        
        // Initialize the SUT instance using the input stream
        jarArchiveInputStream = new JarArchiveInputStream(mockJarInputStream);
        
        // Since we cannot easily mock the internal state of ZipArchiveInputStream 
        // without complex setup, we rely on the stream being readable.
    }

    @Benchmark
    public void getNextEntry(Blackhole bh) throws IOException {
        // Call the subject method exactly once per invocation
        ZipArchiveEntry entry = jarArchiveInputStream.getNextEntry();
        
        // Consume the result to prevent optimization removal
        bh.consume(entry);
    }
}
