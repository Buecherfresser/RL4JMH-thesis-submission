package bench.generated.c003;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.io.*;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.archivers.ar.*;
import org.apache.commons.compress.utils.IOUtils;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ArArchiveInputStreamBenchmark {

    private byte[] archiveData;

    @Setup
    public void setup() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ArArchiveOutputStream aos = new ArArchiveOutputStream(baos)) {
            // First entry
            byte[] content1 = "Hello World".getBytes("US-ASCII");
            ArArchiveEntry entry1 = new ArArchiveEntry("file1.txt", content1.length);
            aos.putArchiveEntry(entry1);
            aos.write(content1);
            aos.closeArchiveEntry();

            // Second entry with a longer name and content
            byte[] content2 = "This is a longer content for the second file.\nIt spans multiple lines.\n".getBytes("US-ASCII");
            ArArchiveEntry entry2 = new ArArchiveEntry("dir/file2.txt", content2.length);
            aos.putArchiveEntry(entry2);
            aos.write(content2);
            aos.closeArchiveEntry();
        }
        archiveData = baos.toByteArray();
    }

    @Benchmark
    public void readAllEntries(Blackhole bh) throws IOException {
        try (ArArchiveInputStream ais = new ArArchiveInputStream(new ByteArrayInputStream(archiveData))) {
            ArArchiveEntry entry;
            while ((entry = ais.getNextEntry()) != null) {
                byte[] data = IOUtils.toByteArray(ais);
                bh.consume(data);
            }
        }
    }

    @Benchmark
    public void readFirstEntry(Blackhole bh) throws IOException {
        try (ArArchiveInputStream ais = new ArArchiveInputStream(new ByteArrayInputStream(archiveData))) {
            ArArchiveEntry entry = ais.getNextEntry();
            if (entry != null) {
                byte[] data = IOUtils.toByteArray(ais);
                bh.consume(data);
            }
        }
    }

    @Benchmark
    public void getNextEntryOnly(Blackhole bh) throws IOException {
        try (ArArchiveInputStream ais = new ArArchiveInputStream(new ByteArrayInputStream(archiveData))) {
            ArArchiveEntry entry;
            while ((entry = ais.getNextEntry()) != null) {
                bh.consume(entry.getName());
            }
        }
    }

    @Benchmark
    public void matches(Blackhole bh) {
        // The archive signature is the first 8 bytes
        boolean result = ArArchiveInputStream.matches(archiveData, 8);
        bh.consume(result);
    }
}
