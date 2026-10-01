package bench.generated.c038;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.utils.SeekableInMemoryByteChannel;
import org.apache.commons.compress.utils.IOUtils;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Iterator;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ZipFileBenchmark {

    private ZipFile zipFile;
    private ZipArchiveEntry sampleEntry;
    private byte[] zipBytes;

    @Setup(Level.Trial)
    public void setUp() throws IOException {
        // Build a simple in‑memory ZIP archive with a single file entry
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipArchiveOutputStream zos = new ZipArchiveOutputStream(baos)) {
            byte[] content = "Hello JMH ZipFile benchmark".getBytes("UTF-8");
            ZipArchiveEntry entry = new ZipArchiveEntry("file.txt");
            entry.setSize(content.length);
            zos.putArchiveEntry(entry);
            zos.write(content);
            zos.closeArchiveEntry();
            zos.finish();
        }
        zipBytes = baos.toByteArray();

        // Create ZipFile from the in‑memory byte array
        SeekableInMemoryByteChannel channel = new SeekableInMemoryByteChannel(zipBytes);
        zipFile = ZipFile.builder().setSeekableByteChannel(channel).get();

        // Cache a sample entry for stream‑based benchmarks
        sampleEntry = zipFile.getEntry("file.txt");
    }

    @Benchmark
    public Enumeration<ZipArchiveEntry> benchmarkGetEntries() {
        return zipFile.getEntries();
    }

    @Benchmark
    public ZipArchiveEntry benchmarkGetEntryByName() {
        return zipFile.getEntry("file.txt");
    }

    @Benchmark
    public Iterable<ZipArchiveEntry> benchmarkGetEntriesByName() {
        return zipFile.getEntries("file.txt");
    }

    @Benchmark
    public Enumeration<ZipArchiveEntry> benchmarkGetEntriesInPhysicalOrder() {
        return zipFile.getEntriesInPhysicalOrder();
    }

    @Benchmark
    public Iterable<ZipArchiveEntry> benchmarkGetEntriesInPhysicalOrderByName() {
        return zipFile.getEntriesInPhysicalOrder("file.txt");
    }

    @Benchmark
    public boolean benchmarkCanReadEntryData() {
        return zipFile.canReadEntryData(sampleEntry);
    }

    @Benchmark
    public int benchmarkGetInputStream() throws IOException {
        try (InputStream is = zipFile.getInputStream(sampleEntry)) {
            return IOUtils.toByteArray(is).length;
        }
    }

    @Benchmark
    public int benchmarkGetRawInputStream() throws IOException {
        try (InputStream is = zipFile.getRawInputStream(sampleEntry)) {
            return IOUtils.toByteArray(is).length;
        }
    }

    @Benchmark
    public String benchmarkGetEncoding() {
        return zipFile.getEncoding();
    }

    @Benchmark
    public long benchmarkGetFirstLocalFileHeaderOffset() {
        return zipFile.getFirstLocalFileHeaderOffset();
    }

    @Benchmark
    public int benchmarkGetContentBeforeFirstLocalFileHeader() throws IOException {
        InputStream is = zipFile.getContentBeforeFirstLocalFileHeader();
        if (is == null) {
            return 0;
        }
        try (InputStream in = is) {
            return IOUtils.toByteArray(in).length;
        }
    }

    @Benchmark
    public String benchmarkGetUnixSymlink() throws IOException {
        // No symlink entry in our test archive; returns null
        return zipFile.getUnixSymlink(sampleEntry);
    }

    @Benchmark
    public long benchmarkStreamCount() {
        return zipFile.stream().count();
    }

    // Ensure the ZipFile is closed after the benchmark run
    @TearDown(Level.Trial)
    public void tearDown() throws IOException {
        if (zipFile != null) {
            zipFile.close();
        }
    }
}
