package bench.generated.c075;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.compressors.zstandard.ZstdCompressorOutputStream;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ZstdCompressorOutputStreamBenchmark {

    private byte[] inputData;
    private ByteArrayOutputStream baos;
    private ZstdCompressorOutputStream compressor;

    @Setup(Level.Trial)
    public void setup() throws IOException {
        // 1. Prepare input data (e.g., 1KB of random data)
        int dataSize = 1024;
        inputData = new byte[dataSize];
        for (int i = 0; i < dataSize; i++) {
            inputData[i] = (byte) (i % 256);
        }

        // 2. Prepare underlying stream
        baos = new ByteArrayOutputStream();

        // 3. Initialize the SUT (ZstdCompressorOutputStream)
        // We use the builder pattern to configure the stream
        compressor = ZstdCompressorOutputStream.builder()
                .setOutputStream(baos)
                .get();
    }

    @TearDown(Level.Trial)
    public void tearDown() throws IOException {
        // Ensure the stream is closed after all benchmarks run
        if (compressor != null) {
            compressor.close();
        }
    }

    /**
     * Benchmarks a single write operation using default compression settings.
     * This measures the overhead of writing a chunk of data into the Zstd stream.
     */
    @Benchmark
    public void benchmarkWriteSingleChunk(Blackhole bh) throws IOException {
        // Call the subject method exactly once
        compressor.write(inputData, 0, inputData.length);
        // Consume the result (since write is void, we consume a dummy value to satisfy JMH rules)
        bh.consume(null);
    }

    /**
     * Benchmarks writing multiple small chunks sequentially.
     * This simulates streaming behavior where data arrives in smaller blocks.
     */
    @Benchmark
    public void benchmarkWriteMultipleSmallChunks(Blackhole bh) throws IOException {
        int chunkSize = 64;
        int numChunks = inputData.length / chunkSize;

        for (int i = 0; i < numChunks; i++) {
            compressor.write(inputData, i * chunkSize, chunkSize);
        }
        bh.consume(null);
    }

    /**
     * Benchmarks writing a large chunk of data, simulating a large buffer write.
     */
    @Benchmark
    public void benchmarkWriteLargeChunk(Blackhole bh) throws IOException {
        // Use the full input data size
        compressor.write(inputData, 0, inputData.length);
        bh.consume(null);
    }
}
