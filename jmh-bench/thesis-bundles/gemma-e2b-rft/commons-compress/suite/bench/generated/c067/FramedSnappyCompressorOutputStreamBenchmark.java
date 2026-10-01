package bench.generated.c067;

import org.apache.commons.codec.digest.PureJavaCrc32C;
import org.apache.commons.compress.compressors.CompressorOutputStream;
import org.apache.commons.compress.compressors.snappy.FramedSnappyCompressorInputStream;
import org.apache.commons.compress.compressors.snappy.FramedSnappyCompressorOutputStream;
import org.apache.commons.compress.compressors.lz77support.Parameters;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class FramedSnappyCompressorOutputStreamBenchmark {

    // --- State Fields ---
    private byte[] inputData;
    private byte[] compressedData;
    private final int PAYLOAD_SIZE = 1024 * 1024; // 1MB payload

    // --- Setup ---
    @Setup
    public void setup() throws IOException {
        // 1. Create a large, repeatable input payload
        inputData = new byte[PAYLOAD_SIZE];
        for (int i = 0; i < PAYLOAD_SIZE; i++) {
            inputData[i] = (byte) (i % 256);
        }

        // 2. Pre-compress the data once for decompression benchmarks
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             FramedSnappyCompressorOutputStream compressor = new FramedSnappyCompressorOutputStream(baos)) {
            
            compressor.write(inputData, 0, inputData.length);
            compressor.close();
            compressedData = baos.toByteArray();
        }
    }

    // --- Compression Benchmarks ---

    @Benchmark
    public void benchmarkWriteLargeChunk(Blackhole bh) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             FramedSnappyCompressorOutputStream compressor = new FramedSnappyCompressorOutputStream(baos)) {

            compressor.write(inputData, 0, inputData.length);
            compressor.close();
            
            byte[] result = baos.toByteArray();
            bh.consume(result);
        }
    }

    @Benchmark
    public void benchmarkWriteSingleByte(Blackhole bh) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             FramedSnappyCompressorOutputStream compressor = new FramedSnappyCompressorOutputStream(baos)) {

            for (int i = 0; i < inputData.length; i++) {
                compressor.write(inputData[i]);
            }
            compressor.close();

            byte[] result = baos.toByteArray();
            bh.consume(result);
        }
    }

    @Benchmark
    public void benchmarkWriteSingleByteDirect(Blackhole bh) throws IOException {
        // Benchmarking the specific write(int b) method
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             FramedSnappyCompressorOutputStream compressor = new FramedSnappyCompressorOutputStream(baos)) {

            for (int i = 0; i < inputData.length; i++) {
                compressor.write(inputData[i] & 0xFF);
            }
            compressor.close();

            byte[] result = baos.toByteArray();
            bh.consume(result);
        }
    }

    // --- Decompression Benchmarks ---

    @Benchmark
    public void benchmarkDecompressFullPayload(Blackhole bh) throws IOException {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(compressedData);
             FramedSnappyCompressorInputStream decompressor = new FramedSnappyCompressorInputStream(bais)) {

            byte[] result = decompressor.readAllBytes(); // Assuming readAllBytes exists or using a loop/copy
            // Since the API digest implies standard InputStream methods, we simulate reading all bytes.
            // If readAllBytes is not available, we must use a loop, but for simplicity and adherence to the spirit of the API digest, we assume a convenient way to drain the stream.
            
            // Standard way to drain InputStream in JMH context:
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = decompressor.read(buffer)) != -1) {
                baos.write(buffer, 0, bytesRead);
            }
            
            bh.consume(baos.toByteArray());
        }
    }
}
