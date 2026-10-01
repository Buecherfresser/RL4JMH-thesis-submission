package bench.generated.c057;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.compressors.lz4.FramedLZ4CompressorOutputStream;
import org.apache.commons.compress.compressors.lz4.FramedLZ4CompressorOutputStream.Parameters;
import org.apache.commons.compress.compressors.lz4.FramedLZ4CompressorOutputStream.BlockSize;
import org.apache.commons.compress.compressors.lz4.FramedLZ4CompressorInputStream;
import org.apache.commons.codec.digest.XXHash32;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class FramedLZ4CompressorOutputStreamBenchmark {

    // --- Input Data Setup ---
    private byte[] smallData;
    private byte[] mediumData;
    private byte[] largeData;

    // --- Stream State ---
    private ByteArrayOutputStream outputStream;
    private ByteArrayOutputStream inputBuffer;
    private FramedLZ4CompressorOutputStream compressorStream;
    private FramedLZ4CompressorInputStream decompressorStream;

    // --- Parameters ---
    private Parameters defaultParams;
    private Parameters k64Params;
    private Parameters m4Params;
    private Parameters withContentChecksumParams;

    @Setup
    public void setup() throws IOException {
        // 1. Prepare fixed payloads
        smallData = new byte[1024 * 10]; // 10KB
        mediumData = new byte[1024 * 1024]; // 1MB
        largeData = new byte[1024 * 1024 * 10]; // 10MB

        // Fill data with semi-random content
        for (int i = 0; i < smallData.length; i++) {
            smallData[i] = (byte) (i % 256);
        }
        for (int i = 0; i < mediumData.length; i++) {
            mediumData[i] = (byte) (i % 256);
        }
        for (int i = 0; i < largeData.length; i++) {
            largeData[i] = (byte) (i % 256);
        }

        // 2. Define Parameters
        defaultParams = Parameters.DEFAULT;
        k64Params = new Parameters(BlockSize.K64);
        m4Params = Parameters.DEFAULT; // Default is M4
        withContentChecksumParams = new Parameters(BlockSize.M4, true, false, false);

        // 3. Initialize Streams
        outputStream = new ByteArrayOutputStream();
        inputBuffer = new ByteArrayOutputStream();

        // Initialize compressor stream (using default parameters)
        compressorStream = new FramedLZ4CompressorOutputStream(outputStream, defaultParams);

        // Initialize decompressor stream (using default parameters)
        decompressorStream = new FramedLZ4CompressorInputStream(new ByteArrayInputStream(outputStream.toByteArray()));
    }

    // --- Compression Benchmarks ---

    @Benchmark
    public void compress_SmallData_DefaultParams(Blackhole bh) throws IOException {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(smallData)) {
            FramedLZ4CompressorOutputStream comp = new FramedLZ4CompressorOutputStream(outputStream, defaultParams);
            comp.write(smallData, 0, smallData.length);
            comp.finish();
        }
        bh.consume(outputStream.toByteArray());
    }

    @Benchmark
    public void compress_MediumData_K64Block(Blackhole bh) throws IOException {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(mediumData)) {
            FramedLZ4CompressorOutputStream comp = new FramedLZ4CompressorOutputStream(outputStream, k64Params);
            comp.write(mediumData, 0, mediumData.length);
            comp.finish();
        }
        bh.consume(outputStream.toByteArray());
    }

    @Benchmark
    public void compress_LargeData_M4Block(Blackhole bh) throws IOException {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(largeData)) {
            FramedLZ4CompressorOutputStream comp = new FramedLZ4CompressorOutputStream(outputStream, m4Params);
            comp.write(largeData, 0, largeData.length);
            comp.finish();
        }
        bh.consume(outputStream.toByteArray());
    }

    @Benchmark
    public void compress_MediumData_WithContentChecksum(Blackhole bh) throws IOException {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(mediumData)) {
            FramedLZ4CompressorOutputStream comp = new FramedLZ4CompressorOutputStream(outputStream, withContentChecksumParams);
            comp.write(mediumData, 0, mediumData.length);
            comp.finish();
        }
        bh.consume(outputStream.toByteArray());
    }

    // --- Single Byte Write Benchmark ---

    @Benchmark
    public void write_SingleByte(Blackhole bh) throws IOException {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(new byte[100])) {
            FramedLZ4CompressorOutputStream comp = new FramedLZ4CompressorOutputStream(outputStream, defaultParams);
            comp.write(10);
            comp.finish();
        }
        bh.consume(outputStream.toByteArray());
    }

    // --- Decompression Benchmarks ---

    @Benchmark
    public void decompress_SmallData(Blackhole bh) throws IOException {
        // Setup: Pre-compress the data once to ensure the input stream is valid
        try (ByteArrayInputStream bais = new ByteArrayInputStream(smallData)) {
            FramedLZ4CompressorOutputStream comp = new FramedLZ4CompressorOutputStream(outputStream, defaultParams);
            comp.write(smallData, 0, smallData.length);
            comp.finish();
        }

        // Benchmark: Decompress
        try (ByteArrayInputStream compressedInput = new ByteArrayInputStream(outputStream.toByteArray())) {
            byte[] decompressed = new byte[smallData.length];
            decompressorStream.read(decompressed);
            bh.consume(decompressed);
        }
    }

    @Benchmark
    public void decompress_MediumData(Blackhole bh) throws IOException {
        // Setup: Pre-compress the data once
        try (ByteArrayInputStream bais = new ByteArrayInputStream(mediumData)) {
            FramedLZ4CompressorOutputStream comp = new FramedLZ4CompressorOutputStream(outputStream, defaultParams);
            comp.write(mediumData, 0, mediumData.length);
            comp.finish();
        }

        // Benchmark: Decompress
        try (ByteArrayInputStream compressedInput = new ByteArrayInputStream(outputStream.toByteArray())) {
            byte[] decompressed = new byte[mediumData.length];
            decompressorStream.read(decompressed);
            bh.consume(decompressed);
        }
    }
}
