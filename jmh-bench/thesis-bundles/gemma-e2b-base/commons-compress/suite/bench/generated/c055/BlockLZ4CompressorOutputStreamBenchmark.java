package bench.generated.c055;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
    private byte[] largePayload;
    private byte[] smallPayload;

    private static final int LARGE_SIZE = 1024 * 1024; // 1 MB
    private static final int SMALL_SIZE = 1024 * 100; // 100 KB

    @Setup
    public void setup() throws IOException {
        // 1. Prepare large payload
        largePayload = new byte[LARGE_SIZE];
        for (int i = 0; i < LARGE_SIZE; i++) {
            largePayload[i] = (byte) (i % 256);
        }

        // 2. Prepare small payload
        smallPayload = new byte[SMALL_SIZE];
        for (int i = 0; i < SMALL_SIZE; i++) {
            smallPayload[i] = (byte) (i % 256);
        }
    }

    /**
     * Benchmark for compressing a large payload.
     * Measures the time taken by BlockLZ4CompressorOutputStream.write(byte[], int, int).
     */
    @Benchmark
    public void compressLargePayload(Blackhole bh) throws IOException {
        outputStream = new ByteArrayOutputStream();
        try (BlockLZ4CompressorOutputStream compressor = new BlockLZ4CompressorOutputStream(outputStream)) {
            compressor.write(largePayload, 0, largePayload.length);
        }
        bh.consume(outputStream.toByteArray());
    }

    /**
     * Benchmark for compressing a small payload.
     * Measures the time taken by BlockLZ4CompressorOutputStream.write(byte[], int, int) on smaller data.
     */
    @Benchmark
    public void compressSmallPayload(Blackhole bh) throws IOException {
        outputStream = new ByteArrayOutputStream();
        try (BlockLZ4CompressorOutputStream compressor = new BlockLZ4CompressorOutputStream(outputStream)) {
            compressor.write(smallPayload, 0, smallPayload.length);
        }
        bh.consume(outputStream.toByteArray());
    }

    /**
     * Benchmark for writing a single byte.
     * Measures the overhead of the single-byte write operation.
     */
    @Benchmark
    public void writeSingleByte(Blackhole bh) throws IOException {
        outputStream = new ByteArrayOutputStream();
        try (BlockLZ4CompressorOutputStream compressor = new BlockLZ4CompressorOutputStream(outputStream)) {
            compressor.write(10);
        }
        bh.consume(outputStream.toByteArray());
    }

    /**
     * Benchmark for pre-filling the window with data.
     * Measures the time taken by BlockLZ4CompressorOutputStream.prefill(byte[], int, int).
     */
    @Benchmark
    public void prefillWindow(Blackhole bh) throws IOException {
        outputStream = new ByteArrayOutputStream();
        try (BlockLZ4CompressorOutputStream compressor = new BlockLZ4CompressorOutputStream(outputStream)) {
            // Pre-fill the window with a chunk of the large payload
            compressor.prefill(largePayload, 0, LARGE_SIZE / 4);
        }
        bh.consume(outputStream.toByteArray());
    }
}
