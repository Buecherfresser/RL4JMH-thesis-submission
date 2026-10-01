package bench.generated.c029;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.archivers.tar.TarFile;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.utils.IOUtils;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.util.List;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class TarFileBenchmark {

    private byte[] tarBytes;
    private List<TarArchiveEntry> entries;
    private TarFile tarFile;

    @Setup(Level.Trial)
    public void setUp() throws IOException {
        // Build an in‑memory TAR archive with two simple entries
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (TarArchiveOutputStream tos = new TarArchiveOutputStream(baos)) {
            // first entry
            byte[] data1 = "Hello World".getBytes("UTF-8");
            TarArchiveEntry entry1 = new TarArchiveEntry("file1.txt");
            entry1.setSize(data1.length);
            tos.putArchiveEntry(entry1);
            tos.write(data1);
            tos.closeArchiveEntry();

            // second entry
            byte[] data2 = new byte[1024];
            for (int i = 0; i < data2.length; i++) {
                data2[i] = (byte) (i & 0xFF);
            }
            TarArchiveEntry entry2 = new TarArchiveEntry("file2.bin");
            entry2.setSize(data2.length);
            tos.putArchiveEntry(entry2);
            tos.write(data2);
            tos.closeArchiveEntry();

            tos.finish();
        }
        tarBytes = baos.toByteArray();

        // Initialize the TarFile instance once per trial
        tarFile = new TarFile(tarBytes);
        entries = tarFile.getEntries();
    }

    @TearDown(Level.Trial)
    public void tearDown() throws IOException {
        if (tarFile != null) {
            tarFile.close();
        }
    }

    @Benchmark
    public List<TarArchiveEntry> benchmarkGetEntries() {
        // Returns the list of entries; JMH will consume the result
        return tarFile.getEntries();
    }

    @Benchmark
    public byte[] benchmarkReadFirstEntry() throws IOException {
        // Read the first entry completely and return its bytes
        TarArchiveEntry e = entries.get(0);
        try (InputStream in = tarFile.getInputStream(e)) {
            return IOUtils.toByteArray(in);
        }
    }

    @Benchmark
    public long benchmarkReadAllEntries() throws IOException {
        // Read every entry and sum the total number of bytes read
        long total = 0;
        for (TarArchiveEntry e : entries) {
            try (InputStream in = tarFile.getInputStream(e)) {
                byte[] data = IOUtils.toByteArray(in);
                total += data.length;
            }
        }
        return total;
    }

    @Benchmark
    public int benchmarkCreateTarFile() throws IOException {
        // Construct a new TarFile from the pre‑built byte array and return entry count
        try (TarFile tf = new TarFile(tarBytes)) {
            return tf.getEntries().size();
        }
    }
}
