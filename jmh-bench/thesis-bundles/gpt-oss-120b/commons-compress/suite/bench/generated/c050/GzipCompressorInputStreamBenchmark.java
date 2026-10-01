package bench.generated.c050;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipParameters;

@State(org.openjdk.jmh.annotations.Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class GzipCompressorInputStreamBenchmark {

    private byte[] rawData;
    private byte[] gzipData;
    private byte[] gzipConcatData;

    @Setup(org.openjdk.jmh.annotations.Level.Trial)
    public void setUp() throws IOException {
        // Prepare a deterministic payload
        rawData = new byte[1024];
        for (int i = 0; i < rawData.length; i++) {
            rawData[i] = (byte) (i & 0xFF);
        }

        // Single gzip member
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             GzipCompressorOutputStream gzos = new GzipCompressorOutputStream(bos)) {
            gzos.write(rawData);
            gzos.finish();
            gzipData = bos.toByteArray();
        }

        // Concatenated gzip members (two identical members)
        byte[] first;
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             GzipCompressorOutputStream gzos = new GzipCompressorOutputStream(bos)) {
            gzos.write(rawData);
            gzos.finish();
            first = bos.toByteArray();
        }
        byte[] second;
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             GzipCompressorOutputStream gzos = new GzipCompressorOutputStream(bos)) {
            gzos.write(rawData);
            gzos.finish();
            second = bos.toByteArray();
        }
        gzipConcatData = new byte[first.length + second.length];
        System.arraycopy(first, 0, gzipConcatData, 0, first.length);
        System.arraycopy(second, 0, gzipConcatData, first.length, second.length);
    }

    @Benchmark
    public int decompressSingleMember() throws IOException {
        GzipCompressorInputStream in = new GzipCompressorInputStream(new ByteArrayInputStream(gzipData));
        byte[] out = new byte[rawData.length];
        int read = in.read(out, 0, out.length);
        in.close();
        return read;
    }

    @Benchmark
    public int decompressConcatenated() throws IOException {
        GzipCompressorInputStream in = GzipCompressorInputStream.builder()
                .setInputStream(new ByteArrayInputStream(gzipConcatData))
                .setDecompressConcatenated(true)
                .get();
        byte[] out = new byte[rawData.length * 2];
        int read = in.read(out, 0, out.length);
        in.close();
        return read;
    }

    @Benchmark
    public int getMetaDataSingleMember() throws IOException {
        GzipCompressorInputStream in = new GzipCompressorInputStream(new ByteArrayInputStream(gzipData));
        GzipParameters params = in.getMetaData();
        int level = params.getCompressionLevel();
        in.close();
        return level;
    }

    @Benchmark
    public int getMetaDataConcatenated() throws IOException {
        GzipCompressorInputStream in = GzipCompressorInputStream.builder()
                .setInputStream(new ByteArrayInputStream(gzipConcatData))
                .setDecompressConcatenated(true)
                .get();
        GzipParameters params = in.getMetaData();
        int level = params.getCompressionLevel();
        in.close();
        return level;
    }

    @Benchmark
    public boolean matchesSignature() {
        return GzipCompressorInputStream.matches(gzipData, gzipData.length);
    }

    @Benchmark
    public void consumeDecompressedSingleMember(Blackhole bh) throws IOException {
        GzipCompressorInputStream in = new GzipCompressorInputStream(new ByteArrayInputStream(gzipData));
        byte[] out = new byte[rawData.length];
        int read = in.read(out, 0, out.length);
        bh.consume(read);
        bh.consume(out);
        in.close();
    }

    @Benchmark
    public void consumeDecompressedConcatenated(Blackhole bh) throws IOException {
        GzipCompressorInputStream in = GzipCompressorInputStream.builder()
                .setInputStream(new ByteArrayInputStream(gzipConcatData))
                .setDecompressConcatenated(true)
                .get();
        byte[] out = new byte[rawData.length * 2];
        int read = in.read(out, 0, out.length);
        bh.consume(read);
        bh.consume(out);
        in.close();
    }
}
