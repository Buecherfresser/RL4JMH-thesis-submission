package bench.generated.c021;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;
import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.utils.SeekableInMemoryByteChannel;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.compress.utils.InputStreamStatistics;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class SevenZFileBenchmark {

    private byte[] archiveBytes;
    private SevenZArchiveEntry firstEntry;

    @Setup
    public void setUp() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ArchiveStreamFactory factory = new ArchiveStreamFactory();
        try (ArchiveOutputStream out = factory.createArchiveOutputStream(ArchiveStreamFactory.SEVEN_Z, baos)) {
            // First entry
            String name1 = "file1.txt";
            byte[] data1 = "Hello World".getBytes(StandardCharsets.UTF_8);
            SevenZArchiveEntry entry1 = new SevenZArchiveEntry();
            entry1.setName(name1);
            entry1.setSize(data1.length);
            out.putArchiveEntry(entry1);
            out.write(data1);
            out.closeArchiveEntry();

            // Second entry
            String name2 = "file2.bin";
            byte[] data2 = new byte[1024];
            for (int i = 0; i < data2.length; i++) {
                data2[i] = (byte) i;
            }
            SevenZArchiveEntry entry2 = new SevenZArchiveEntry();
            entry2.setName(name2);
            entry2.setSize(data2.length);
            out.putArchiveEntry(entry2);
            out.write(data2);
            out.closeArchiveEntry();

            out.finish();
        }
        archiveBytes = baos.toByteArray();

        // Capture a reference to the first entry for random‑access benchmark
        try (SevenZFile file = SevenZFile.builder()
                .setSeekableByteChannel(new SeekableInMemoryByteChannel(archiveBytes))
                .get()) {
            firstEntry = file.getNextEntry();
        }
    }

    @Benchmark
    public long readAllEntriesSequentially() throws IOException {
        try (SevenZFile file = SevenZFile.builder()
                .setSeekableByteChannel(new SeekableInMemoryByteChannel(archiveBytes))
                .get()) {
            long total = 0;
            SevenZArchiveEntry entry;
            byte[] buffer = new byte[8192];
            while ((entry = file.getNextEntry()) != null) {
                int n;
                while ((n = file.read(buffer)) != -1) {
                    total += n;
                }
            }
            return total;
        }
    }

    @Benchmark
    public long readFirstEntryRandomAccess() throws IOException {
        try (SevenZFile file = SevenZFile.builder()
                .setSeekableByteChannel(new SeekableInMemoryByteChannel(archiveBytes))
                .get()) {
            InputStream is = file.getInputStream(firstEntry);
            byte[] all = IOUtils.toByteArray(is);
            return all.length;
        }
    }

    @Benchmark
    public String getDefaultNameBenchmark() throws IOException {
        try (SevenZFile file = SevenZFile.builder()
                .setSeekableByteChannel(new SeekableInMemoryByteChannel(archiveBytes))
                .get()) {
            return file.getDefaultName();
        }
    }

    @Benchmark
    public long readAndGetStatistics(Blackhole bh) throws IOException {
        try (SevenZFile file = SevenZFile.builder()
                .setSeekableByteChannel(new SeekableInMemoryByteChannel(archiveBytes))
                .get()) {
            SevenZArchiveEntry entry = file.getNextEntry();
            byte[] buffer = new byte[256];
            int n = file.read(buffer);
            InputStreamStatistics stats = file.getStatisticsForCurrentEntry();
            long compressed = stats.getCompressedCount();
            long uncompressed = stats.getUncompressedCount();
            bh.consume(compressed);
            bh.consume(uncompressed);
            return n;
        }
    }
}
