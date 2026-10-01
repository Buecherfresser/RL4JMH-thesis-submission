package bench.generated.c061;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.apache.commons.compress.compressors.lzma.LZMACompressorOutputStream;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class LZMACompressorOutputStreamBenchmark {

    private byte[] inputData;
    private ByteArrayOutputStream outputStream;

    // We must rebuild the compressor instance per invocation because it is a mutating stateful object.
    // We will handle the creation inside the benchmark method itself.

    @Setup(Level.Trial)
    public void setup() throws IOException {
        // Create a representative payload (e.g., 1MB of data)
        int payloadSize = 1024 * 1024;
        inputData = new byte[payloadSize];
        for (int i = 0; i < payloadSize; i++) {
            inputData[i] = (byte) (i % 256);
        }
        // Reusable output stream
        outputStream = new ByteArrayOutputStream();
    }

    /**
     * Benchmarks the full compression cycle: initialization, writing data, and finishing.
     * This measures the total time taken to compress the input payload.
     */
    @Benchmark
    public void compressFullCycle(Blackhole bh) throws IOException {
        // 1. Reset output stream for this invocation
        outputStream.reset();

        // 2. Initialize the compressor stream
        // We use the default builder settings for simplicity and consistency.
        LZMACompressorOutputStream compressor = new LZMACompressorOutputStream(outputStream);

        try {
            // 3. Write the data
            compressor.write(inputData, 0, inputData.length);

            // 4. Finish compression
            compressor.finish();
        } finally {
            // Ensure the stream is closed/cleaned up if necessary, though finish() is the key operation here.
            // Since we are benchmarking the process, we rely on the try-finally block to manage resources.
        }

        // 5. Consume the result
        bh.consume(outputStream.toByteArray());
    }

    /**
     * Benchmarks the write operation only, assuming the stream is already initialized.
     * Note: This benchmark is less representative of real-world usage than the full cycle,
     * but tests the core write method performance.
     */
    @Benchmark
    public void writeDataOnly(Blackhole bh) throws IOException {
        // 1. Reset output stream
        outputStream.reset();

        // 2. Initialize the compressor stream
        LZMACompressorOutputStream compressor = new LZMACompressorOutputStream(outputStream);

        try {
            // 3. Write a chunk of data (e.g., 64KB)
            int chunkSize = 64 * 1024;
            compressor.write(inputData, 0, chunkSize);
        } finally {
            // We don't call finish() here, so the output is incomplete, but we measure the write cost.
        }

        // 4. Consume the partial result
        bh.consume(outputStream.toByteArray());
    }

    /**
     * Benchmarks the finish operation only, assuming data has already been written.
     * This tests the finalization overhead.
     */
    @Benchmark
    public void finishOperationOnly(Blackhole bh) throws IOException {
        // 1. Reset output stream
        outputStream.reset();

        // 2. Initialize the compressor stream
        LZMACompressorOutputStream compressor = new LZMACompressorOutputStream(outputStream);

        try {
            // 3. Write a small amount of data to ensure the stream is active
            compressor.write(inputData, 0, 1024);
            
            // 4. Finish compression
            compressor.finish();
        } finally {
            // Cleanup
        }

        // 5. Consume the result
        bh.consume(outputStream.toByteArray());
    }
}
