package bench.generated.c025;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class TarArchiveInputStreamBenchmark {

    private byte[] tarData;
    private byte[] signature;

    @Setup(Level.Trial)
    public void setUp() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (TarArchiveOutputStream tos = new TarArchiveOutputStream(baos)) {
            // regular file entry
            byte[] fileContent = new byte[1024];
            for (int i = 0; i < fileContent.length; i++) {
                fileContent[i] = (byte) (i & 0xFF);
            }
            TarArchiveEntry fileEntry = new TarArchiveEntry("file.txt");
            fileEntry.setSize(fileContent.length);
            tos.putArchiveEntry(fileEntry);
            tos.write(fileContent);
            tos.closeArchiveEntry();

            // directory entry
            TarArchiveEntry dirEntry = new TarArchiveEntry("dir/");
            dirEntry.setMode(TarArchiveEntry.DEFAULT_DIR_MODE);
            tos.putArchiveEntry(dirEntry);
            tos.closeArchiveEntry();
        }
        tarData = baos.toByteArray();

        // prepare a signature buffer (first 512 bytes or less)
        int sigLen = Math.min(512, tarData.length);
        signature = new byte[sigLen];
        System.arraycopy(tarData, 0, signature, 0, sigLen);
    }

    @Benchmark
    public TarArchiveEntry benchmarkGetNextEntry() throws IOException {
        try (TarArchiveInputStream tis = new TarArchiveInputStream(new ByteArrayInputStream(tarData))) {
            return tis.getNextEntry();
        }
    }

    @Benchmark
    public int benchmarkReadEntry(Blackhole bh) throws IOException {
        try (TarArchiveInputStream tis = new TarArchiveInputStream(new ByteArrayInputStream(tarData))) {
            TarArchiveEntry entry = tis.getNextEntry();
            byte[] buf = new byte[256];
            int total = 0;
            int read;
            while ((read = tis.read(buf, 0, buf.length)) != -1) {
                total += read;
            }
            bh.consume(total);
            return total;
        }
    }

    @Benchmark
    public long benchmarkSkipEntry(Blackhole bh) throws IOException {
        try (TarArchiveInputStream tis = new TarArchiveInputStream(new ByteArrayInputStream(tarData))) {
            TarArchiveEntry entry = tis.getNextEntry();
            long toSkip = entry.getSize() / 2;
            long skipped = tis.skip(toSkip);
            bh.consume(skipped);
            return skipped;
        }
    }

    @Benchmark
    public int benchmarkAvailable() throws IOException {
        try (TarArchiveInputStream tis = new TarArchiveInputStream(new ByteArrayInputStream(tarData))) {
            tis.getNextEntry();
            return tis.available();
        }
    }

    @Benchmark
    public int benchmarkGetRecordSize() {
        try (TarArchiveInputStream tis = new TarArchiveInputStream(new ByteArrayInputStream(tarData))) {
            return tis.getRecordSize();
        } catch (IOException e) {
            // getRecordSize does not throw, but constructor may; rethrow as unchecked
            throw new RuntimeException(e);
        }
    }

    @Benchmark
    public boolean benchmarkMatches() {
        return TarArchiveInputStream.matches(signature, signature.length);
    }

    @Benchmark
    public boolean benchmarkCanReadEntryData() throws IOException {
        try (TarArchiveInputStream tis = new TarArchiveInputStream(new ByteArrayInputStream(tarData))) {
            TarArchiveEntry entry = tis.getNextEntry();
            return tis.canReadEntryData(entry);
        }
    }

    @Benchmark
    public TarArchiveEntry benchmarkGetCurrentEntry() throws IOException {
        try (TarArchiveInputStream tis = new TarArchiveInputStream(new ByteArrayInputStream(tarData))) {
            tis.getNextEntry();
            return tis.getCurrentEntry();
        }
    }
}
