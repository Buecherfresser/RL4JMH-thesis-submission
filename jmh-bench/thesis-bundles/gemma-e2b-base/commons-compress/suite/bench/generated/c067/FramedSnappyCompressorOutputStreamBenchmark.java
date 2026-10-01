package bench.generated.c067;

import org.apache.commons.compress.compressors.snappy.FramedSnappyCompressorOutputStream;
import org.apache.commons.compress.compressors.lz77support.Parameters;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class FramedSnappyCompressorOutputStreamBenchmark {

    private ByteArrayOutputStream outputStream;
    private byte[] inputData;
    private final int DATA_SIZE = 1024 * 1024 * 4; // 4 MB payload

    @Setup
    public void setup() throws IOException {
        // 1. Prepare a fixed, large input payload
        this.inputData = new byte[DATA_SIZE];
        // Fill with some data (e.g., sequential bytes)
        for (int i = 0; i < DATA_SIZE; i++) {
            inputData[i] = (byte) (i % 256);
        }

        // 2. Initialize the output stream buffer
        this.outputStream = new ByteArrayOutputStream();
    }

    /**
     * Benchmark 1: Compressing a large chunk of data using the main write method.
     * This tests the core buffering and compression logic.
     */
    @Benchmark
    public void benchmarkWriteLargeData(Blackhole bh) throws IOException {
        // Create the compressor stream targeting our output buffer
        try (FramedSnappyCompressorOutputStream compressor = new FramedSnappyCompressorOutputStream(outputStream)) {
            compressor.write(inputData, 0, inputData.length);
            compressor.close();
        }
        bh.consume(outputStream.toByteArray());
    }

    /**
     * Benchmark 2: Compressing the same large chunk of data, but using smaller write chunks.
     * This tests the efficiency of the internal buffer flushing mechanism.
     */
    @Benchmark
    public void benchmarkWriteSmallChunks(Blackhole bh) throws IOException {
        // Create the compressor stream targeting our output buffer
        try (FramedSnappyCompressorOutputStream compressor = new FramedSnappyCompressorOutputStream(outputStream)) {
            int chunkSize = 1024 * 1024; // 1 MB chunks
            for (int i = 0; i < inputData.length; i += chunkSize) {
                int len = Math.min(chunkSize, inputData.length - i);
                compressor.write(inputData, i, len);
            }
            compressor.close();
        }
        bh.consume(outputStream.toByteArray());
    }

    /**
     * Benchmark 3: Writing a single byte repeatedly.
     * This tests the overhead of the single-byte write path.
     */
    @Benchmark
    public void benchmarkWriteSingleByte(Blackhole bh) throws IOException {
        // Create the compressor stream targeting our output buffer
        try (FramedSnappyCompressorOutputStream compressor = new FramedSnappyCompressorOutputStream(outputStream)) {
            for (int i = 0; i < 10000; i++) {
                compressor.write(i % 256);
            }
            compressor.close();
        }
        bh.consume(outputStream.toByteArray());
    }
}
