package bench.generated.c053;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import org.apache.commons.compress.compressors.gzip.GzipUtils;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class GzipUtilsBenchmark {

    private byte[] payload;
    private byte[] decompressedPayload;

    @Setup
    public void setup() throws IOException {
        // Create a representative payload (e.g., a string of data)
        String originalString = "This is a test string for Gzip compression benchmarking. " +
                                "We need enough data to make the compression/decompression operation measurable.";
        this.payload = originalString.getBytes(StandardCharsets.UTF_8);

        // Compress the payload once to get the compressed data
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream gzipOut =
                 new org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream(baos)) {
            gzipOut.write(payload);
            gzipOut.finish();
            this.decompressedPayload = baos.toByteArray();
        }
    }

    // --- Compression Benchmarks ---

    @Benchmark
    public void compressData(Blackhole bh) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream gzipOut =
                 new org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream(baos)) {
            gzipOut.write(payload);
            gzipOut.finish();
            bh.consume(baos.toByteArray());
        }
    }

    // --- Decompression Benchmarks ---

    @Benchmark
    public void decompressData(Blackhole bh) throws IOException {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(decompressedPayload);
             org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream gzipIn =
                 new org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream(bais);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            byte[] buffer = new byte[4096];
            int len;
            while ((len = gzipIn.read(buffer)) > 0) {
                baos.write(buffer, 0, len);
            }
            byte[] result = baos.toByteArray();
            bh.consume(result);
        }
    }

    // --- Utility Method Benchmarks (Testing static methods) ---

    @Benchmark
    public void getCompressedFileName(Blackhole bh) {
        String fileName = "my_file.txt";
        String result = GzipUtils.getCompressedFileName(fileName);
        bh.consume(result);
    }

    @Benchmark
    public void getUncompressedFileName(Blackhole bh) {
        String fileName = "my_file.tgz";
        String result = GzipUtils.getUncompressedFileName(fileName);
        bh.consume(result);
    }

    @Benchmark
    public void isCompressedFileName(Blackhole bh) {
        String compressedName = "archive.gz";
        boolean result = GzipUtils.isCompressedFileName(compressedName);
        bh.consume(result);
    }
}
