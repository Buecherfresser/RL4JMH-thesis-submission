package bench.generated.c036;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import java.util.zip.ZipEntry;
import org.apache.commons.compress.utils.IOUtils;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.CRC32;
import java.util.Arrays;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ZipArchiveInputStreamBenchmark {

    // In‑memory ZIP archive used by all benchmarks
    private byte[] zipData;

    // Sample entry used for canReadEntryData()
    private ZipArchiveEntry sampleEntry;

    // Stream used for canReadEntryData() (no state needed)
    private ZipArchiveInputStream canReadStream;

    // Per‑invocation streams
    private ZipArchiveInputStream countStream;   // fully read entry, used for count benchmarks
    private ZipArchiveInputStream skipStream;    // positioned at entry start, used for skip benchmark
    private long skipSize;                       // size of the entry to skip

    @Setup(Level.Trial)
    public void buildZipArchive() throws IOException {
        // Build a ZIP with one STORED entry and one DEFLATED entry (both 256 KB)
        final int payloadSize = 256 * 1024;
        final byte[] payload = new byte[payloadSize];
        Arrays.fill(payload, (byte) 'A');

        // Compute CRC for the stored entry
        CRC32 crc = new CRC32();
        crc.update(payload);
        long crcValue = crc.getValue();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipArchiveOutputStream zos = new ZipArchiveOutputStream(baos);

        // STORED entry
        ZipArchiveEntry stored = new ZipArchiveEntry("stored.bin");
        stored.setMethod(ZipEntry.STORED);
        stored.setSize(payloadSize);
        stored.setCompressedSize(payloadSize);
        stored.setCrc(crcValue);
        zos.putArchiveEntry(stored);
        zos.write(payload);
        zos.closeArchiveEntry();

        // DEFLATED entry
        ZipArchiveEntry deflated = new ZipArchiveEntry("deflated.bin");
        deflated.setMethod(ZipEntry.DEFLATED);
        zos.putArchiveEntry(deflated);
        zos.write(payload);
        zos.closeArchiveEntry();

        zos.finish();
        zos.close();

        zipData = baos.toByteArray();

        // Prepare a stream and a sample entry for canReadEntryData()
        canReadStream = new ZipArchiveInputStream(new ByteArrayInputStream(zipData));
        sampleEntry = canReadStream.getNextEntry();
        // No need to keep the stream open for canReadEntryData()
        canReadStream.close();
    }

    @Setup(Level.Invocation)
    public void perInvocationSetup() throws IOException {
        // Stream for getCompressedCount() / getUncompressedCount()
        countStream = new ZipArchiveInputStream(new ByteArrayInputStream(zipData));
        ZipArchiveEntry e1 = countStream.getNextEntry();
        // Drain the entry completely so counters are populated
        byte[] buf = new byte[8192];
        while (countStream.read(buf, 0, buf.length) != -1) {
            // consume
        }

        // Stream for skip()
        skipStream = new ZipArchiveInputStream(new ByteArrayInputStream(zipData));
        ZipArchiveEntry e2 = skipStream.getNextEntry();
        skipSize = e2.getCompressedSize();
    }

    @Benchmark
    public ZipArchiveEntry benchmarkGetNextEntry() throws IOException {
        ZipArchiveInputStream zis = new ZipArchiveInputStream(new ByteArrayInputStream(zipData));
        return zis.getNextEntry();
    }

    @Benchmark
    public boolean benchmarkCanReadEntryData() {
        return canReadStream.canReadEntryData(sampleEntry);
    }

    @Benchmark
    public long benchmarkGetCompressedCount() {
        return countStream.getCompressedCount();
    }

    @Benchmark
    public long benchmarkGetUncompressedCount() {
        return countStream.getUncompressedCount();
    }

    @Benchmark
    public long benchmarkSkip() throws IOException {
        return skipStream.skip(skipSize);
    }
}
