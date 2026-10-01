package bench.generated.c061;

import org.apache.commons.compress.compressors.CompressorOutputStream;
import org.apache.commons.compress.compressors.lzma.LZMACompressorInputStream;
import org.apache.commons.compress.compressors.lzma.LZMACompressorOutputStream;

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

    private byte[] inputData;
    private byte[] compressedData;
    private static final int PAYLOAD_SIZE = 1024 * 1024; // 1MB payload

    @Setup
    public void setup() throws IOException {
        // 1. Create a large, repeatable input payload
        inputData = new byte[PAYLOAD_SIZE];
        for (int i = 0; i < PAYLOAD_SIZE; i++) {
            inputData[i] = (byte) (i % 256);
        }

        // 2. Pre-compress the data once for decompression benchmarks
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             LZMACompressorOutputStream compressor = LZMACompressorOutputStream.builder()
                     .setLzma2Options(new org.tukaani.xz.LZMA2Options())
                     .get()) {

            compressor.write(inputData, 0, inputData.length);
            compressor.finish();
            compressedData = baos.toByteArray();
        }
    }

    /**
     * Benchmark for compression: Writing data into the LZMA stream.
     */
    @Benchmark
    public void testCompression(Blackhole bh) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             LZMACompressorOutputStream compressor = LZMACompressorOutputStream.builder()
                     .setLzma2Options(new org.tukaani.xz.LZMA2Options())
                     .get()) {

            compressor.write(inputData, 0, inputData.length);
            compressor.finish();

            byte[] result = baos.toByteArray();
            bh.consume(result);
        }
    }

    /**
     * Benchmark for decompression: Reading data from the LZMA stream.
     */
    @Benchmark
    public void testDecompression(Blackhole bh) throws IOException {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(compressedData);
             LZMACompressorInputStream decompressor = new LZMACompressorInputStream(bais)) {

            byte[] decompressedData = new byte[PAYLOAD_SIZE];
            int bytesRead = decompressor.read(decompressedData, 0, decompressedData.length);
            bh.consume(bytesRead);
        }
    }
}
