package bench.generated.c045;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class BZip2CompressorOutputStreamBenchmark {

    // --- Setup Data ---
    // Payload size chosen to be large enough to stress compression but small enough to run quickly.
    private static final int PAYLOAD_SIZE = 1024 * 1024; // 1 MB payload
    private byte[] payload;

    // --- State for Compression ---
    private ByteArrayOutputStream outputStream;
    private BZip2CompressorOutputStream compressorStream;

    @Setup
    public void setup() throws IOException {
        // 1. Prepare the fixed payload
        this.payload = new byte[PAYLOAD_SIZE];
        // Fill payload with semi-random data to ensure compression is meaningful
        for (int i = 0; i < PAYLOAD_SIZE; i++) {
            payload[i] = (byte) (i % 256);
        }

        // 2. Initialize the output stream for capturing compressed data
        this.outputStream = new ByteArrayOutputStream();
    }

    /**
     * Benchmark for BZip2 compression using the minimum block size (1).
     */
    @Benchmark
    public void benchmarkCompression_BlockSize1(Blackhole bh) throws IOException {
        // Initialize the compressor with block size 1
        this.compressorStream = new BZip2CompressorOutputStream(this.outputStream, 1);

        // Write the payload
        this.compressorStream.write(payload, 0, payload.length);

        // Finalize compression and get the result into the outputStream
        this.compressorStream.close();

        // Consume the result to prevent dead code elimination
        bh.consume(this.outputStream.toByteArray());
    }

    /**
     * Benchmark for BZip2 compression using a medium block size (4).
     */
    @Benchmark
    public void benchmarkCompression_BlockSize4(Blackhole bh) throws IOException {
        // Initialize the compressor with block size 4
        this.compressorStream = new BZip2CompressorOutputStream(this.outputStream, 4);

        // Write the payload
        this.compressorStream.write(payload, 0, payload.length);

        // Finalize compression and get the result into the outputStream
        this.compressorStream.close();

        // Consume the result
        bh.consume(this.outputStream.toByteArray());
    }

    /**
     * Benchmark for BZip2 compression using a large block size (9).
     */
    @Benchmark
    public void benchmarkCompression_BlockSize9(Blackhole bh) throws IOException {
        // Initialize the compressor with block size 9
        this.compressorStream = new BZip2CompressorOutputStream(this.outputStream, 9);

        // Write the payload
        this.compressorStream.write(payload, 0, payload.length);

        // Finalize compression and get the result into the outputStream
        this.compressorStream.close();

        // Consume the result
        bh.consume(this.outputStream.toByteArray());
    }
}
