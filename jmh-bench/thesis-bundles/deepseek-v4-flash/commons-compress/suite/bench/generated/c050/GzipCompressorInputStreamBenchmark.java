package bench.generated.c050;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipParameters;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class GzipCompressorInputStreamBenchmark {

    private byte[] payload;
    private byte[] compressed;
    private byte[] compressedWithMeta;
    private byte[] compressedConcatenated;
    private byte[] gzipSignature;

    @Setup(Level.Trial)
    public void setUp() throws IOException {
        payload = createPayload(64 * 1024);
        compressed = gzip(payload, null);

        GzipParameters params = new GzipParameters();
        params.setFileName("benchmark.txt");
        params.setComment("benchmark comment");
        compressedWithMeta = gzip(payload, params);

        ByteArrayOutputStream concat = new ByteArrayOutputStream();
        concat.write(compressed);
        concat.write(gzip(payload, null));
        compressedConcatenated = concat.toByteArray();

        gzipSignature = new byte[] {31, -117, 8, 0};
    }

    private static byte[] createPayload(int size) {
        byte[] data = new byte[size];
        for (int i = 0; i < size; i++) {
            data[i] = (byte) (i % 128);
        }
        return data;
    }

    private static byte[] gzip(byte[] data, GzipParameters params) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (GzipCompressorOutputStream gzos = params == null
                ? new GzipCompressorOutputStream(bos)
                : new GzipCompressorOutputStream(bos, params)) {
            gzos.write(data);
        }
        return bos.toByteArray();
    }

    private static byte[] readAll(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[8192];
        int n;
        while ((n = in.read(buffer, 0, buffer.length)) != -1) {
            out.write(buffer, 0, n);
        }
        return out.toByteArray();
    }

    @Benchmark
    public byte[] readBulk() throws IOException {
        try (GzipCompressorInputStream gz = new GzipCompressorInputStream(new ByteArrayInputStream(compressed))) {
            return readAll(gz);
        }
    }

    @Benchmark
    public int readSingleByte() throws IOException {
        try (GzipCompressorInputStream gz = new GzipCompressorInputStream(new ByteArrayInputStream(compressed))) {
            int count = 0;
            while (gz.read() != -1) {
                count++;
            }
            return count;
        }
    }

    @Benchmark
    public byte[] readConcatenated() throws IOException {
        try (GzipCompressorInputStream gz = new GzipCompressorInputStream(new ByteArrayInputStream(compressedConcatenated), true)) {
            return readAll(gz);
        }
    }

    @Benchmark
    public byte[] readNonConcatenated() throws IOException {
        try (GzipCompressorInputStream gz = new GzipCompressorInputStream(new ByteArrayInputStream(compressedConcatenated), false)) {
            return readAll(gz);
        }
    }

    @Benchmark
    public byte[] builderReadWithCallbacks() throws IOException {
        final int[] starts = {0};
        final int[] ends = {0};
        try (GzipCompressorInputStream gz = GzipCompressorInputStream.builder()
                .setInputStream(new ByteArrayInputStream(compressedWithMeta))
                .setDecompressConcatenated(true)
                .setOnMemberStart(s -> starts[0]++)
                .setOnMemberEnd(s -> ends[0]++)
                .get()) {
            byte[] data = readAll(gz);
            if (starts[0] != 1 || ends[0] != 1) {
                throw new IOException("Unexpected callback count");
            }
            return data;
        }
    }

    @Benchmark
    public String constructAndGetMetaData() throws IOException {
        try (GzipCompressorInputStream gz = new GzipCompressorInputStream(new ByteArrayInputStream(compressedWithMeta))) {
            return gz.getMetaData().getFileName();
        }
    }

    @Benchmark
    public String readAndGetMetaData() throws IOException {
        try (GzipCompressorInputStream gz = new GzipCompressorInputStream(new ByteArrayInputStream(compressedWithMeta))) {
            readAll(gz);
            return gz.getMetaData().getFileName();
        }
    }

    @Benchmark
    public long readAndGetCompressedCount() throws IOException {
        try (GzipCompressorInputStream gz = new GzipCompressorInputStream(new ByteArrayInputStream(compressed))) {
            readAll(gz);
            return gz.getCompressedCount();
        }
    }

    @Benchmark
    public boolean matchesSignature() {
        return GzipCompressorInputStream.matches(gzipSignature, gzipSignature.length);
    }
}
