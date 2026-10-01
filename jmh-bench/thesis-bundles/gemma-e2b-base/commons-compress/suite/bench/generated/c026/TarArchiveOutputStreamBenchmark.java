package bench.generated.c026;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class TarArchiveOutputStreamBenchmark {

    private ByteArrayOutputStream baos;
    private TarArchiveOutputStream tarOutputStream;
    private byte[] testData;
    private TarArchiveEntry testEntry;

    // --- Setup Data ---

    @Setup
    public void setup() throws IOException {
        // 1. Create a representative entry
        testEntry = new TarArchiveEntry("testfile.txt");
        testEntry.setSize(1024); // 1KB size
        testEntry.setLastModifiedTime(java.nio.file.attribute.FileTime.from(java.time.Instant.now().minusSeconds(3600)));
        testEntry.setMode(0644); // Regular file mode

        // 2. Create test data payload
        String content = "This is a test file content for benchmarking TarArchiveOutputStream.";
        testData = content.getBytes(StandardCharsets.UTF_8);

        // 3. Initialize the output stream (using default settings)
        baos = new ByteArrayOutputStream();
        tarOutputStream = new TarArchiveOutputStream(baos);
    }

    // --- Benchmarks ---

    /**
     * Benchmark 1: Basic writing of a single entry with default settings.
     */
    @Benchmark
    public void benchmarkBasicWrite(Blackhole bh) throws IOException {
        tarOutputStream.putArchiveEntry(testEntry);
        tarOutputStream.write(testData, 0, testData.length);
        tarOutputStream.closeArchiveEntry();
        bh.consume(baos.toByteArray());
    }

    /**
     * Benchmark 2: Writing a single entry using a custom block size (e.g., 10240).
     */
    @Benchmark
    public void benchmarkCustomBlockSize(Blackhole bh) throws IOException {
        final int customBlockSize = 10240;
        baos = new ByteArrayOutputStream();
        tarOutputStream = new TarArchiveOutputStream(baos, customBlockSize);

        tarOutputStream.putArchiveEntry(testEntry);
        tarOutputStream.write(testData, 0, testData.length);
        tarOutputStream.closeArchiveEntry();
        tarOutputStream.close();
        bh.consume(baos.toByteArray());
    }

    /**
     * Benchmark 3: Writing a single entry using a specific charset (e.g., ISO-8859-1).
     */
    @Benchmark
    public void benchmarkCustomCharset(Blackhole bh) throws IOException {
        final String customCharset = "ISO-8859-1";
        baos = new ByteArrayOutputStream();
        tarOutputStream = new TarArchiveOutputStream(baos, customCharset);

        tarOutputStream.putArchiveEntry(testEntry);
        tarOutputStream.write(testData, 0, testData.length);
        tarOutputStream.closeArchiveEntry();
        tarOutputStream.close();
        bh.consume(baos.toByteArray());
    }

    /**
     * Benchmark 4: Testing long file name handling using LONGFILE_GNU mode.
     */
    @Benchmark
    public void benchmarkLongNameGNU(Blackhole bh) throws IOException {
        // Create a long name entry (simulating a long file name)
        TarArchiveEntry longEntry = new TarArchiveEntry(Path.of("a/very/long/path/to/a/file_name_that_exceeds_limits_for_testing.txt"), "longfile.txt");
        longEntry.setSize(100);

        baos = new ByteArrayOutputStream();
        tarOutputStream = new TarArchiveOutputStream(baos);
        tarOutputStream.setLongFileMode(TarArchiveOutputStream.LONGFILE_GNU);

        tarOutputStream.putArchiveEntry(longEntry);
        tarOutputStream.write(testData, 0, testData.length);
        tarOutputStream.closeArchiveEntry();
        tarOutputStream.close();
        bh.consume(baos.toByteArray());
    }

    /**
     * Benchmark 5: Testing big number handling using BIGNUMBER_STAR mode.
     */
    @Benchmark
    public void benchmarkBigNumberSTAR(Blackhole bh) throws IOException {
        // Create an entry with a size that might exceed standard limits (simulating a big file)
        TarArchiveEntry bigEntry = new TarArchiveEntry("bigfile.dat");
        bigEntry.setSize(Long.MAX_VALUE / 2); // Very large size

        baos = new ByteArrayOutputStream();
        tarOutputStream = new TarArchiveOutputStream(baos);
        tarOutputStream.setBigNumberMode(TarArchiveOutputStream.BIGNUMBER_STAR);

        tarOutputStream.putArchiveEntry(bigEntry);
        tarOutputStream.write(testData, 0, testData.length);
        tarOutputStream.closeArchiveEntry();
        tarOutputStream.close();
        bh.consume(baos.toByteArray());
    }

    /**
     * Benchmark 6: Testing PAX header generation for non-ASCII names.
     */
    @Benchmark
    public void benchmarkNonAsciiPaxHeaders(Blackhole bh) throws IOException {
        // Use a non-ASCII name (e.g., UTF-8 characters)
        String nonAsciiName = "ファイル名";
        TarArchiveEntry nonAsciiEntry = new TarArchiveEntry(nonAsciiName);
        nonAsciiEntry.setSize(10);

        baos = new ByteArrayOutputStream();
        tarOutputStream = new TarArchiveOutputStream(baos);
        tarOutputStream.setAddPaxHeadersForNonAsciiNames(true);

        tarOutputStream.putArchiveEntry(nonAsciiEntry);
        tarOutputStream.write(testData, 0, testData.length);
        tarOutputStream.closeArchiveEntry();
        tarOutputStream.close();
        bh.consume(baos.toByteArray());
    }
}
