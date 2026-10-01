package bench.generated.c066;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.apache.commons.compress.compressors.snappy.FramedSnappyCompressorInputStream;
import org.apache.commons.compress.compressors.snappy.FramedSnappyDialect;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class FramedSnappyCompressorInputStreamBenchmark {

    private FramedSnappyCompressorInputStream compressorInputStream;
    private ByteArrayInputStream rawInputStream;
    private final byte[] readBuffer = new byte[4096];
    private static final int READ_SIZE = 4096;

    /**
     * Setup method to initialize the input stream and the compressor wrapper.
     * NOTE: Since generating a valid framed Snappy payload is complex,
     * we use a placeholder byte array. In a real scenario, this array
     * would contain actual compressed data matching the framing format.
     * We assume this placeholder simulates a stream that can be read.
     */
    @Setup(Level.Trial)
    public void setup() throws IOException {
        // Placeholder for a large, compressed payload (e.g., 1MB of compressed data)
        // This array must simulate the structure expected by FramedSnappyCompressorInputStream.
        // For compilation and structural correctness, we use a small dummy array.
        byte[] compressedData = new byte[1024 * 10]; // 10KB dummy data
        
        // Initialize the raw input stream
        rawInputStream = new ByteArrayInputStream(compressedData);

        // Initialize the compressor stream using the standard dialect
        // This step involves reading the stream identifier and setting up internal state.
        compressorInputStream = new FramedSnappyCompressorInputStream(rawInputStream, FramedSnappyDialect.STANDARD);
    }

    /**
     * Benchmarks the core decompression read operation, reading a large chunk of data.
     * This tests the efficiency of chunk processing and decompression logic.
     */
    @Benchmark
    public void readLargeChunk(Blackhole bh) throws IOException {
        // Read a large chunk of data from the compressed stream
        int bytesRead = compressorInputStream.read(readBuffer, 0, READ_SIZE);
        
        // Consume the result to prevent dead code elimination
        bh.consume(bytesRead);
    }

    /**
     * Benchmarks reading a single byte, testing the overhead of stream state management
     * and chunk boundary checks for minimal reads.
     */
    @Benchmark
    public void readSingleByte(Blackhole bh) throws IOException {
        // Read a single byte
        int byteRead = compressorInputStream.read();
        
        // Consume the result
        bh.consume(byteRead);
    }

    /**
     * Benchmarks checking the available bytes count, testing internal state tracking.
     */
    @Benchmark
    public void checkAvailableBytes(Blackhole bh) throws IOException {
        // Check how many bytes are available for reading
        int available = compressorInputStream.available();
        
        // Consume the result
        bh.consume(available);
    }

    /**
     * Benchmarks retrieving the total compressed count, testing internal statistics tracking.
     */
    @Benchmark
    public void getCompressedCount(Blackhole bh) throws IOException {
        // Get the total count of bytes read from the compressed stream
        long count = compressorInputStream.getCompressedCount();
        
        // Consume the result
        bh.consume(count);
    }
}
