package bench.generated.c018;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.apache.commons.compress.archivers.jar.JarArchiveEntry;
import org.apache.commons.compress.archivers.jar.JarArchiveInputStream;
import org.apache.commons.compress.archivers.jar.JarArchiveOutputStream;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class JarArchiveInputStreamBenchmark {

    private byte[] jarBytes;
    private byte[] signature;

    @Setup(Level.Trial)
    public void setup() throws Exception {
        // Build a JAR archive in memory with a few entries
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (JarArchiveOutputStream jarOut = new JarArchiveOutputStream(baos)) {
            addEntry(jarOut, "entry1.txt", "Hello World 1".getBytes(StandardCharsets.UTF_8));
            addEntry(jarOut, "dir/entry2.txt", "Hello World 2 with more data".getBytes(StandardCharsets.UTF_8));
            addEntry(jarOut, "entry3.bin", new byte[1024]);
        }
        jarBytes = baos.toByteArray();
        // Typical JAR/ZIP local file header signature: PK\x03\x04
        signature = new byte[] {0x50, 0x4B, 0x03, 0x04};
    }

    private void addEntry(JarArchiveOutputStream out, String name, byte[] data) throws IOException {
        JarArchiveEntry entry = new JarArchiveEntry(name);
        entry.setSize(data.length);
        out.putArchiveEntry(entry);
        out.write(data);
        out.closeArchiveEntry();
    }

    @Benchmark
    public int readAllEntriesAndData(Blackhole bh) throws IOException {
        int totalBytes = 0;
        try (JarArchiveInputStream jarIn = new JarArchiveInputStream(new ByteArrayInputStream(jarBytes))) {
            JarArchiveEntry entry;
            byte[] buffer = new byte[8192];
            while ((entry = jarIn.getNextEntry()) != null) {
                int len;
                while ((len = jarIn.read(buffer, 0, buffer.length)) != -1) {
                    totalBytes += len;
                    bh.consume(buffer);
                }
            }
        }
        return totalBytes;
    }

    @Benchmark
    public int readAllEntriesAndDataWithEncoding(Blackhole bh) throws IOException {
        int totalBytes = 0;
        try (JarArchiveInputStream jarIn = new JarArchiveInputStream(new ByteArrayInputStream(jarBytes), "UTF-8")) {
            JarArchiveEntry entry;
            byte[] buffer = new byte[8192];
            while ((entry = jarIn.getNextEntry()) != null) {
                int len;
                while ((len = jarIn.read(buffer, 0, buffer.length)) != -1) {
                    totalBytes += len;
                    bh.consume(buffer);
                }
            }
        }
        return totalBytes;
    }

    @Benchmark
    public int readFirstEntryData(Blackhole bh) throws IOException {
        try (JarArchiveInputStream jarIn = new JarArchiveInputStream(new ByteArrayInputStream(jarBytes))) {
            JarArchiveEntry entry = jarIn.getNextEntry();
            if (entry == null) {
                return 0;
            }
            byte[] buffer = new byte[8192];
            int total = 0;
            int len;
            while ((len = jarIn.read(buffer, 0, buffer.length)) != -1) {
                total += len;
                bh.consume(buffer);
            }
            return total;
        }
    }

    @Benchmark
    public int countEntries() throws IOException {
        int count = 0;
        try (JarArchiveInputStream jarIn = new JarArchiveInputStream(new ByteArrayInputStream(jarBytes))) {
            while (jarIn.getNextEntry() != null) {
                count++;
            }
        }
        return count;
    }

    @Benchmark
    public boolean canReadFirstEntryData() throws IOException {
        try (JarArchiveInputStream jarIn = new JarArchiveInputStream(new ByteArrayInputStream(jarBytes))) {
            JarArchiveEntry entry = jarIn.getNextEntry();
            return entry != null && jarIn.canReadEntryData(entry);
        }
    }

    @Benchmark
    public boolean matchesSignature() {
        return JarArchiveInputStream.matches(signature, signature.length);
    }
}
