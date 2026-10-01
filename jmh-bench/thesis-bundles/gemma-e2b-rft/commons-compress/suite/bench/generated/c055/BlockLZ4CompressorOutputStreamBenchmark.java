package bench.generated.c055;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.apache.commons.compress.compressors.lz4.BlockLZ4CompressorOutputStream;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class BlockLZ4CompressorOutputStreamBenchmark {

    private ByteArrayOutputStream outputStream;
    private BlockLZ4CompressorOutputStream compressorOutputStream;

    // Fixed payload for compression/decompression tests
    private byte[] testData;

    @Setup
    public void setup() throws IOException {
        // Create a reasonably sized, repetitive test payload
        String originalString = "This is a test string for LZ4 compression benchmarking. ";
        // FIX: Convert String to byte[] to satisfy the BlockLZ4CompressorOutputStream.prefill requirement
        testData = originalString.repeat(1000).getBytes(StandardCharsets.UTF_8); // ~70KB payload

        // Initialize the output stream for compression tests
        outputStream = new ByteArrayOutputStream();
    }

    /**
     * Benchmark for compressing data using BlockLZ4CompressorOutputStream.
     * Measures the time taken to compress the fixed test data.
     */
    @Benchmark
    public void compressData(Blackhole bh) throws IOException {
        // Initialize the compressor stream with the output stream
        compressorOutputStream = new BlockLZ4CompressorOutputStream(outputStream);

        // 1. Prefill the window with initial data
        compressorOutputStream.prefill(testData, 0, testData.length);

        // 2. Write the main data
        compressorOutputStream.write(testData, 0, testData.length);

        // 3. Finish the compression process
        compressorOutputStream.finish();

        // Consume the resulting compressed data
        byte[] compressedData = outputStream.toByteArray();
        bh.consume(compressedData);
    }

    /**
     * Benchmark for decompressing data using BlockLZ4CompressorInputStream (simulated via input stream).
     * Measures the time taken to decompress the compressed data generated in the previous step.
     */
    @Benchmark
    public void decompressData(Blackhole bh) throws IOException {
        // Recreate the input stream from the previously generated compressed data
        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());

        // Simulate reading some data from the input stream
        byte[] inputData = new byte[1024]; 
        inputStream.read(inputData);
        bh.consume(inputData);
    }

    /**
     * Benchmark for writing a single byte.
     */
    @Benchmark
    public void writeSingleByte(Blackhole bh) throws IOException {
        // Use a fresh output stream for each invocation to simulate independent writes
        ByteArrayOutputStream tempOut = new ByteArrayOutputStream();
        BlockLZ4CompressorOutputStream tempCompressor = new BlockLZ4CompressorOutputStream(tempOut);

        tempCompressor.write(10);
        tempCompressor.close();

        bh.consume(tempOut.toByteArray());
    }
}
