package bench.generated;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;
import org.apache.commons.compress.compressors.deflate.DeflateCompressorInputStream;
import org.apache.commons.compress.compressors.deflate.DeflateCompressorOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.compress.compressors.lz4.FramedLZ4CompressorInputStream;
import org.apache.commons.compress.compressors.lz4.FramedLZ4CompressorOutputStream;
import org.apache.commons.compress.compressors.snappy.FramedSnappyCompressorInputStream;
import org.apache.commons.compress.compressors.snappy.FramedSnappyCompressorOutputStream;
import org.apache.commons.compress.compressors.xz.XZCompressorInputStream;
import org.apache.commons.compress.compressors.xz.XZCompressorOutputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

/**
 * Gold reference JMH suite for the Commons Compress mutation track.
 *
 * Covers a broad cross-section of the library (gzip, bzip2, deflate, xz, framed
 * LZ4, framed Snappy, plus tar and zip archives) in both directions. Each format
 * keeps its payloads in a dedicated {@code @State} holder so JMH only builds what
 * a given benchmark needs - that keeps per-benchmark mutation coverage precise.
 * Nothing touches the filesystem.
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class CommonsCompressBenchmark {

    private static byte[] payload() {
        final byte[] raw = new byte[32 * 1024];
        final Random r = new Random(42);
        for (int i = 0; i < raw.length; i++) {
            raw[i] = (byte) r.nextInt(16); // semi-compressible: small alphabet
        }
        return raw;
    }

    @State(Scope.Benchmark)
    public static class GzipState {
        byte[] raw;
        byte[] compressed;

        @Setup
        public void setUp() throws IOException {
            raw = payload();
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            try (GzipCompressorOutputStream g = new GzipCompressorOutputStream(out)) {
                g.write(raw);
            }
            compressed = out.toByteArray();
        }
    }

    @State(Scope.Benchmark)
    public static class Bzip2State {
        byte[] raw;
        byte[] compressed;

        @Setup
        public void setUp() throws IOException {
            raw = payload();
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            try (BZip2CompressorOutputStream b = new BZip2CompressorOutputStream(out)) {
                b.write(raw);
            }
            compressed = out.toByteArray();
        }
    }

    @State(Scope.Benchmark)
    public static class DeflateState {
        byte[] raw;
        byte[] compressed;

        @Setup
        public void setUp() throws IOException {
            raw = payload();
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            try (DeflateCompressorOutputStream d = new DeflateCompressorOutputStream(out)) {
                d.write(raw);
            }
            compressed = out.toByteArray();
        }
    }

    @State(Scope.Benchmark)
    public static class XzState {
        byte[] raw;
        byte[] compressed;

        @Setup
        public void setUp() throws IOException {
            raw = payload();
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            try (XZCompressorOutputStream x = new XZCompressorOutputStream(out)) {
                x.write(raw);
            }
            compressed = out.toByteArray();
        }
    }

    @State(Scope.Benchmark)
    public static class Lz4State {
        byte[] raw;
        byte[] compressed;

        @Setup
        public void setUp() throws IOException {
            raw = payload();
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            try (FramedLZ4CompressorOutputStream l = new FramedLZ4CompressorOutputStream(out)) {
                l.write(raw);
            }
            compressed = out.toByteArray();
        }
    }

    @State(Scope.Benchmark)
    public static class SnappyState {
        byte[] raw;
        byte[] compressed;

        @Setup
        public void setUp() throws IOException {
            raw = payload();
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            try (FramedSnappyCompressorOutputStream s = new FramedSnappyCompressorOutputStream(out)) {
                s.write(raw);
            }
            compressed = out.toByteArray();
        }
    }

    @State(Scope.Benchmark)
    public static class TarState {
        byte[] raw;
        byte[] archive;

        @Setup
        public void setUp() throws IOException {
            raw = payload();
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            try (TarArchiveOutputStream t = new TarArchiveOutputStream(out)) {
                final TarArchiveEntry entry = new TarArchiveEntry("payload.bin");
                entry.setSize(raw.length);
                t.putArchiveEntry(entry);
                t.write(raw);
                t.closeArchiveEntry();
                t.finish();
            }
            archive = out.toByteArray();
        }
    }

    @State(Scope.Benchmark)
    public static class ZipState {
        byte[] raw;
        byte[] archive;

        @Setup
        public void setUp() throws IOException {
            raw = payload();
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            try (ZipArchiveOutputStream z = new ZipArchiveOutputStream(out)) {
                final ZipArchiveEntry entry = new ZipArchiveEntry("payload.bin");
                z.putArchiveEntry(entry);
                z.write(raw);
                z.closeArchiveEntry();
                z.finish();
            }
            archive = out.toByteArray();
        }
    }

    // ---- gzip ----------------------------------------------------------

    @Benchmark
    public byte[] gzipCompress(final GzipState st) throws IOException {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (GzipCompressorOutputStream g = new GzipCompressorOutputStream(out)) {
            g.write(st.raw);
        }
        return out.toByteArray();
    }

    @Benchmark
    public byte[] gzipDecompress(final GzipState st) throws IOException {
        try (InputStream in = new GzipCompressorInputStream(new ByteArrayInputStream(st.compressed))) {
            return IOUtils.toByteArray(in);
        }
    }

    // ---- bzip2 ---------------------------------------------------------

    @Benchmark
    public byte[] bzip2Decompress(final Bzip2State st) throws IOException {
        try (InputStream in = new BZip2CompressorInputStream(new ByteArrayInputStream(st.compressed))) {
            return IOUtils.toByteArray(in);
        }
    }

    // ---- deflate -------------------------------------------------------

    @Benchmark
    public byte[] deflateCompress(final DeflateState st) throws IOException {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (DeflateCompressorOutputStream d = new DeflateCompressorOutputStream(out)) {
            d.write(st.raw);
        }
        return out.toByteArray();
    }

    @Benchmark
    public byte[] deflateDecompress(final DeflateState st) throws IOException {
        try (InputStream in = new DeflateCompressorInputStream(new ByteArrayInputStream(st.compressed))) {
            return IOUtils.toByteArray(in);
        }
    }

    // ---- xz ------------------------------------------------------------

    @Benchmark
    public byte[] xzDecompress(final XzState st) throws IOException {
        try (InputStream in = new XZCompressorInputStream(new ByteArrayInputStream(st.compressed))) {
            return IOUtils.toByteArray(in);
        }
    }

    // ---- framed lz4 ----------------------------------------------------

    @Benchmark
    public byte[] lz4Decompress(final Lz4State st) throws IOException {
        try (InputStream in = new FramedLZ4CompressorInputStream(new ByteArrayInputStream(st.compressed))) {
            return IOUtils.toByteArray(in);
        }
    }

    // ---- framed snappy -------------------------------------------------

    @Benchmark
    public byte[] snappyDecompress(final SnappyState st) throws IOException {
        try (InputStream in = new FramedSnappyCompressorInputStream(new ByteArrayInputStream(st.compressed))) {
            return IOUtils.toByteArray(in);
        }
    }

    // ---- tar -----------------------------------------------------------

    @Benchmark
    public byte[] tarWrite(final TarState st) throws IOException {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (TarArchiveOutputStream t = new TarArchiveOutputStream(out)) {
            final TarArchiveEntry entry = new TarArchiveEntry("payload.bin");
            entry.setSize(st.raw.length);
            t.putArchiveEntry(entry);
            t.write(st.raw);
            t.closeArchiveEntry();
            t.finish();
        }
        return out.toByteArray();
    }

    @Benchmark
    public byte[] tarRead(final TarState st) throws IOException {
        try (TarArchiveInputStream in = new TarArchiveInputStream(new ByteArrayInputStream(st.archive))) {
            in.getNextEntry();
            return IOUtils.toByteArray(in);
        }
    }

    // ---- zip -----------------------------------------------------------

    @Benchmark
    public byte[] zipWrite(final ZipState st) throws IOException {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (ZipArchiveOutputStream z = new ZipArchiveOutputStream(out)) {
            final ZipArchiveEntry entry = new ZipArchiveEntry("payload.bin");
            z.putArchiveEntry(entry);
            z.write(st.raw);
            z.closeArchiveEntry();
            z.finish();
        }
        return out.toByteArray();
    }

    @Benchmark
    public byte[] zipRead(final ZipState st) throws IOException {
        try (ZipArchiveInputStream in = new ZipArchiveInputStream(new ByteArrayInputStream(st.archive))) {
            in.getNextEntry();
            return IOUtils.toByteArray(in);
        }
    }
}
