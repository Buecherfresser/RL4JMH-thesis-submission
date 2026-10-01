package bench.generated.c061;

import org.apache.commons.compress.compressors.lzma.LZMACompressorInputStream;
import org.apache.commons.compress.compressors.lzma.LZMACompressorOutputStream;
import org.apache.commons.io.IOUtils;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class LZMACompressorOutputStreamBenchmark {

    private static final int PAYLOAD_SIZE = 1024 * 1024; // 1 MB payload
    private byte[] inputPayload;
    private byte[] compressedPayload;

    @Setup
    public void setup() throws IOException {
        // 1. Create a fixed, non-final payload
        inputPayload = new byte[PAYLOAD_SIZE];
        for (int i = 0; i < PAYLOAD_SIZE; i++) {
            inputPayload[i] = (byte) (i % 256);
        }

        // 2. Pre-compress the payload once for decompression benchmarks
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             LZMACompressorOutputStream compressor = LZMACompressorOutputStream.builder()
                     .setLzma2Options(new org.tukaani.xz.LZMA2Options())
                     .get()) {

            compressor.write(inputPayload, 0, inputPayload.length);
            compressor.finish();
            compressedPayload = baos.toByteArray();
        }
    }

    /**
     * Benchmark for LZMA Compression (Writing)
     * Measures the time taken to compress a fixed payload using LZMACompressorOutputStream.
     */
    @Benchmark
    public void compressData(Blackhole bh) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             LZMACompressorOutputStream compressor = LZMACompressorOutputStream.builder()
                     .setLzma2Options(new org.tukaani.xz.LZMA2Options())
                     .get()) {

            compressor.write(inputPayload, 0, inputPayload.length);
            compressor.finish();

            byte[] result = baos.toByteArray();
            bh.consume(result);
        }
    }

    /**
     * Benchmark for LZMA Decompression (Reading)
     * Measures the time taken to decompress a fixed payload using LZMACompressorInputStream.
     */
    @Benchmark
    public void decompressData(Blackhole bh) throws IOException {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(compressedPayload);
             LZMACompressorInputStream decompressor = new LZMACompressorInputStream(bais)) {

            byte[] result = IOUtils.toByteArray(decompressor);
            bh.consume(result);
        }
    }
}
