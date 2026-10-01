package bench.generated.c037;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.zip.CRC32;
import java.util.zip.Deflater;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.Zip64Mode;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream.UnicodeExtraFieldPolicy;

/**
 * JMH benchmark for {@link org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream}.
 */
@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ZipArchiveOutputStreamBenchmark {

    private static final int PAYLOAD_SIZE = 1024; // 1 KiB payload
    private byte[] payload;
    private byte[] preamble;

    @Setup
    public void setUp() {
        payload = new byte[PAYLOAD_SIZE];
        for (int i = 0; i < PAYLOAD_SIZE; i++) {
            payload[i] = (byte) (i & 0xFF);
        }
        preamble = new byte[] { 'P', 'R', 'E', 'A', 'M', 'B', 'L', 'E' };
    }

    // -------------------------------------------------------------------------
    // Core archive writing (deflated)
    // -------------------------------------------------------------------------

    @Benchmark
    public void benchmarkPutArchiveEntryDeflated(Blackhole bh) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipArchiveOutputStream zos = new ZipArchiveOutputStream(baos);

        ZipArchiveEntry entry = new ZipArchiveEntry("deflated.txt");
        zos.putArchiveEntry(entry);
        zos.write(payload, 0, payload.length);
        zos.closeArchiveEntry();
        zos.finish();

        bh.consume(baos.toByteArray().length);
    }

    // -------------------------------------------------------------------------
    // Core archive writing (stored)
    // -------------------------------------------------------------------------

    @Benchmark
    public void benchmarkPutArchiveEntryStored(Blackhole bh) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipArchiveOutputStream zos = new ZipArchiveOutputStream(baos);

        ZipArchiveEntry entry = new ZipArchiveEntry("stored.txt");
        entry.setMethod(ZipArchiveOutputStream.STORED);
        entry.setSize(payload.length);
        CRC32 crc32 = new CRC32();
        crc32.update(payload, 0, payload.length);
        entry.setCrc(crc32.getValue());

        zos.putArchiveEntry(entry);
        zos.write(payload, 0, payload.length);
        zos.closeArchiveEntry();
        zos.finish();

        bh.consume(baos.toByteArray().length);
    }

    // -------------------------------------------------------------------------
    // addRawArchiveEntry
    // -------------------------------------------------------------------------

    @Benchmark
    public void benchmarkAddRawArchiveEntry(Blackhole bh) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipArchiveOutputStream zos = new ZipArchiveOutputStream(baos);

        ZipArchiveEntry entry = new ZipArchiveEntry("raw.txt");
        entry.setMethod(ZipArchiveOutputStream.STORED);
        entry.setSize(payload.length);
        CRC32 crc32 = new CRC32();
        crc32.update(payload, 0, payload.length);
        entry.setCrc(crc32.getValue());

        ByteArrayInputStream rawIn = new ByteArrayInputStream(payload);
        zos.addRawArchiveEntry(entry, rawIn);
        zos.finish();

        bh.consume(baos.toByteArray().length);
    }

    // -------------------------------------------------------------------------
    // writePreamble
    // -------------------------------------------------------------------------

    @Benchmark
    public void benchmarkWritePreamble(Blackhole bh) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipArchiveOutputStream zos = new ZipArchiveOutputStream(baos);

        zos.writePreamble(preamble);
        zos.finish();

        bh.consume(baos.toByteArray().length);
    }

    // -------------------------------------------------------------------------
    // Configuration setters
    // -------------------------------------------------------------------------

    @Benchmark
    public void benchmarkSetLevel(Blackhole bh) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipArchiveOutputStream zos = new ZipArchiveOutputStream(baos);

        zos.setLevel(Deflater.BEST_COMPRESSION);
        zos.finish();

        bh.consume(baos.toByteArray().length);
    }

    @Benchmark
    public void benchmarkSetMethod(Blackhole bh) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipArchiveOutputStream zos = new ZipArchiveOutputStream(baos);

        zos.setMethod(ZipArchiveOutputStream.STORED);
        zos.finish();

        bh.consume(baos.toByteArray().length);
    }

    @Benchmark
    public void benchmarkSetComment(Blackhole bh) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipArchiveOutputStream zos = new ZipArchiveOutputStream(baos);

        zos.setComment("Benchmark comment");
        zos.finish();

        bh.consume(baos.toByteArray().length);
    }

    @Benchmark
    public void benchmarkSetEncoding(Blackhole bh) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipArchiveOutputStream zos = new ZipArchiveOutputStream(baos);

        zos.setEncoding("UTF-8");
        zos.finish();

        bh.consume(baos.toByteArray().length);
    }

    @Benchmark
    public void benchmarkSetCreateUnicodeExtraFields(Blackhole bh) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipArchiveOutputStream zos = new ZipArchiveOutputStream(baos);

        zos.setCreateUnicodeExtraFields(UnicodeExtraFieldPolicy.ALWAYS);
        zos.finish();

        bh.consume(baos.toByteArray().length);
    }

    @Benchmark
    public void benchmarkSetUseZip64(Blackhole bh) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipArchiveOutputStream zos = new ZipArchiveOutputStream(baos);

        zos.setUseZip64(Zip64Mode.Always);
        zos.finish();

        bh.consume(baos.toByteArray().length);
    }

    @Benchmark
    public void benchmarkSetFallbackToUTF8(Blackhole bh) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipArchiveOutputStream zos = new ZipArchiveOutputStream(baos);

        zos.setFallbackToUTF8(true);
        zos.finish();

        bh.consume(baos.toByteArray().length);
    }

    @Benchmark
    public void benchmarkSetUseLanguageEncodingFlag(Blackhole bh) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipArchiveOutputStream zos = new ZipArchiveOutputStream(baos);

        zos.setUseLanguageEncodingFlag(true);
        zos.finish();

        bh.consume(baos.toByteArray().length);
    }
}
