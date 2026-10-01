package bench.generated.c069;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import org.apache.commons.compress.compressors.snappy.SnappyCompressorInputStream;
import org.apache.commons.compress.compressors.snappy.SnappyCompressorOutputStream;
import org.apache.commons.compress.utils.IOUtils;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class SnappyCompressorOutputStreamBenchmark {

    private ByteArrayOutputStream baos;
    private SnappyCompressorOutputStream compressorStream;

    // Fixed payload for benchmarking. Must be non-final or built in @Setup if mutable.
    private byte[] testData;

    @Setup
    public void setup() throws IOException {
        // Create a fixed, non-trivial payload.
        // This data should be large enough to exercise compression logic.
        this.testData = new byte[1024 * 10]; // 10 KB of data
        for (int i = 0; i < this.testData.length; i++) {
            this.testData[i] = (byte) (i % 256);
        }
    }

    /**
     * Benchmarks the compression process using the default block size.
     * This tests the write path and the internal compression logic.
     */
    @Benchmark
    public void benchmarkCompression(Blackhole bh) throws IOException {
        // 1. Setup the stream. We use a ByteArrayOutputStream to capture the output.
        this.baos = new ByteArrayOutputStream();
        try {
            // Using the default constructor (which uses default block size)
            this.compressorStream = new SnappyCompressorOutputStream(this.baos, 1024 * 1024);
            
            // 2. Write data
            this.compressorStream.write(this.testData, 0, this.testData.length);
            
            // 3. Finish and close to flush the stream
            this.compressorStream.finish();
            this.compressorStream.close();
            
        } catch (IOException e) {
            // Ignore
        }
        bh.consume(this.baos.toByteArray());
    }

    /**
     * Benchmarks compression with a custom, smaller block size.
     * This tests the constructor path that accepts a block size parameter.
     */
    @Benchmark
    public void benchmarkCompressionWithCustomBlockSize(Blackhole bh) throws IOException {
        this.baos = new ByteArrayOutputStream();
        try {
            // Use a smaller block size (e.g., 1024) to test parameter handling
            this.compressorStream = new SnappyCompressorOutputStream(this.baos, 1024 * 1024, 1024);
            
            this.compressorStream.write(this.testData, 0, this.testData.length);
            
            this.compressorStream.finish();
            this.compressorStream.close();
            
        } catch (IOException e) {
            // Ignore
        }
        bh.consume(this.baos.toByteArray());
    }

    /**
     * Benchmarks writing a single byte, which triggers the internal write path.
     * This tests the path that calls write(int b).
     */
    @Benchmark
    public void benchmarkWriteSingleByte(Blackhole bh) throws IOException {
        this.baos = new ByteArrayOutputStream();
        try {
            this.compressorStream = new SnappyCompressorOutputStream(this.baos, 1024 * 1024);
            
            // Write a single byte
            this.compressorStream.write(0xAA);
            
            this.compressorStream.finish();
            this.compressorStream.close();
            
        } catch (IOException e) {
            // Ignore
        }
        bh.consume(this.baos.toByteArray());
    }

    /**
     * Benchmarks the decompression process (read path).
     * This requires a setup that produces a compressed stream first.
     */
    @Benchmark
    public void benchmarkDecompression(Blackhole bh) throws IOException {
        // --- Compression Phase (Setup for Decompression) ---
        ByteArrayOutputStream compressedBaos = new ByteArrayOutputStream();
        try {
            // Compress the data
            try (SnappyCompressorOutputStream compressor = new SnappyCompressorOutputStream(compressedBaos, 1024 * 1024)) {
                compressor.write(this.testData, 0, this.testData.length);
                compressor.finish();
                compressor.close();
            }
        } catch (IOException e) {
            // Ignore
        }
        byte[] compressedData = compressedBaos.toByteArray();

        // --- Decompression Phase (The actual benchmark) ---
        try (ByteArrayInputStream bais = new ByteArrayInputStream(compressedData);
             // Use SnappyCompressorInputStream to decompress the data
             SnappyCompressorInputStream decompressor = new SnappyCompressorInputStream(bais)) {
            
            // Read all decompressed data
            byte[] decompressedData = IOUtils.toByteArray(decompressor);
            
            bh.consume(decompressedData);
        } catch (IOException e) {
            // Ignore
        }
    }
}
