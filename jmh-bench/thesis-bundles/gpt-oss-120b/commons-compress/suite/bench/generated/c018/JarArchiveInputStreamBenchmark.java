package bench.generated.c018;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.archivers.jar.JarArchiveInputStream;
import org.apache.commons.compress.archivers.jar.JarArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class JarArchiveInputStreamBenchmark {

    private byte[] jarBytes;
    private byte[] signature;
    private int sigLength;

    @Setup(Level.Trial)
    public void setUp() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipArchiveOutputStream zos = new ZipArchiveOutputStream(baos)) {
            // First entry: text file
            ZipArchiveEntry e1 = new ZipArchiveEntry("test.txt");
            byte[] data1 = "Hello, World!".getBytes(StandardCharsets.UTF_8);
            e1.setSize(data1.length);
            zos.putArchiveEntry(e1);
            zos.write(data1);
            zos.closeArchiveEntry();

            // Second entry: binary data
            ZipArchiveEntry e2 = new ZipArchiveEntry("data.bin");
            byte[] data2 = new byte[1024];
            for (int i = 0; i < data2.length; i++) {
                data2[i] = (byte) (i & 0xFF);
            }
            e2.setSize(data2.length);
            zos.putArchiveEntry(e2);
            zos.write(data2);
            zos.closeArchiveEntry();

            zos.finish();
        }
        jarBytes = baos.toByteArray();

        sigLength = Math.min(4, jarBytes.length);
        signature = new byte[sigLength];
        System.arraycopy(jarBytes, 0, signature, 0, sigLength);
    }

    @Benchmark
    public boolean benchmarkMatches() {
        return JarArchiveInputStream.matches(signature, sigLength);
    }

    @Benchmark
    public JarArchiveInputStream benchmarkConstructorDefault() {
        return new JarArchiveInputStream(new ByteArrayInputStream(jarBytes));
    }

    @Benchmark
    public JarArchiveInputStream benchmarkConstructorWithEncoding() {
        return new JarArchiveInputStream(new ByteArrayInputStream(jarBytes), "UTF-8");
    }

    @Benchmark
    public String benchmarkReadFirstEntryName() throws IOException {
        try (JarArchiveInputStream jis = new JarArchiveInputStream(new ByteArrayInputStream(jarBytes))) {
            JarArchiveEntry entry = jis.getNextEntry();
            return entry != null ? entry.getName() : null;
        }
    }

    @Benchmark
    public String benchmarkDeprecatedGetNextJarEntry() throws IOException {
        try (JarArchiveInputStream jis = new JarArchiveInputStream(new ByteArrayInputStream(jarBytes))) {
            JarArchiveEntry entry = jis.getNextJarEntry();
            return entry != null ? entry.getName() : null;
        }
    }

    @Benchmark
    public int benchmarkReadAllEntriesCount() throws IOException {
        int count = 0;
        try (JarArchiveInputStream jis = new JarArchiveInputStream(new ByteArrayInputStream(jarBytes))) {
            JarArchiveEntry entry;
            while ((entry = jis.getNextEntry()) != null) {
                count++;
                // consume name to avoid dead code elimination
                entry.getName();
            }
        }
        return count;
    }

    @Benchmark
    public int benchmarkReadAllData() throws IOException {
        int totalBytes = 0;
        byte[] buffer = new byte[256];
        try (JarArchiveInputStream jis = new JarArchiveInputStream(new ByteArrayInputStream(jarBytes))) {
            JarArchiveEntry entry;
            while ((entry = jis.getNextEntry()) != null) {
                int n;
                while ((n = jis.read(buffer)) != -1) {
                    totalBytes += n;
                }
            }
        }
        return totalBytes;
    }
}
