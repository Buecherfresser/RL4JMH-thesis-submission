package bench.generated.c053;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
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
        this.payload = originalString.getBytes(java.nio.charset.StandardCharsets.UTF_8);

        // Compress the payload once to get the compressed data
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream gzipOut =
                 new org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream(baos)) {
            gzipOut.write(payload);
            gzipOut.finish();
            byte[] compressedData = baos.toByteArray();

            // Decompress the compressed data once to get the original payload (for verification/decompress benchmark)
            try (ByteArrayInputStream bais = new ByteArrayInputStream(compressedData);
                 org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream gzipIn =
                     new org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream(bais)) {
                byte[] decompressedData = org.apache.commons.compress.utils.IOUtils.toByteArray(gzipIn);
                this.decompressedPayload = decompressedData;
            }
        }
    }

    // --- Compression Benchmarks ---

    @Benchmark
    public void benchmarkGzipCompression(Blackhole bh) throws IOException {
        // Compress the original payload
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream gzipOut =
                 new org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream(baos)) {
            gzipOut.write(payload);
            gzipOut.finish();
            byte[] compressed = baos.toByteArray();
            bh.consume(compressed);
        }
    }

    // --- Decompression Benchmarks ---

    @Benchmark
    public void benchmarkGzipDecompression(Blackhole bh) throws IOException {
        // Decompress the pre-compressed payload
        try (ByteArrayInputStream bais = new ByteArrayInputStream(decompressedPayload);
             org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream gzipIn =
                 new org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream(bais)) {
            byte[] decompressed = org.apache.commons.compress.utils.IOUtils.toByteArray(gzipIn);
            bh.consume(decompressed);
        }
    }

    // --- Utility Method Benchmarks (Name Mapping) ---

    @Benchmark
    public void benchmarkGetCompressedFileName(Blackhole bh) {
        String fileName = "archive.tar";
        String result = GzipUtils.getCompressedFileName(fileName);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkGetUncompressedFileName(Blackhole bh) {
        String fileName = "archive.tgz";
        String result = GzipUtils.getUncompressedFileName(fileName);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkIsCompressedFileNameTrue(Blackhole bh) {
        String fileName = "file.gz";
        boolean result = GzipUtils.isCompressedFileName(fileName);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkIsCompressedFileNameFalse(Blackhole bh) {
        String fileName = "file.txt";
        boolean result = GzipUtils.isCompressedFileName(fileName);
        bh.consume(result);
    }
}
