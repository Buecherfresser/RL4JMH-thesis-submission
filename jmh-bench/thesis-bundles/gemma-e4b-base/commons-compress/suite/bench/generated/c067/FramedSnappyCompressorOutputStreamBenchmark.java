package bench.generated.c067;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.compressors.snappy.FramedSnappyCompressorOutputStream;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class FramedSnappyCompressorOutputStreamBenchmark {

    private byte[] inputData;
    private static final int DATA_SIZE = 1024 * 10; // 10 KB input data

    @Setup(Level.Trial)
    public void setupTrial() {
        // Build input data once per trial
        inputData = new byte[DATA_SIZE];
        for (int i = 0; i < DATA_SIZE; i++) {
            inputData[i] = (byte) (i % 256);
        }
    }

    /**
     * Benchmarks the bulk write operation (write(byte[] data, int off, int len)).
     * A fresh stream is created for each invocation to ensure clean state.
     */
    @Benchmark
    public void benchmarkBulkWrite(Blackhole bh) throws IOException {
        // Setup: Create fresh stream and underlying buffer for this invocation
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        FramedSnappyCompressorOutputStream compressor = new FramedSnappyCompressorOutputStream(baos);

        // Action: Write the data
        compressor.write(inputData, 0, DATA_SIZE);

        // Cleanup: Finish the stream to ensure all buffered data is flushed
        compressor.finish();
        compressor.close();

        // Consume the result (the compressed output)
        bh.consume(baos.toByteArray());
    }

    /**
     * Benchmarks the single byte write operation (write(int b)).
     * A fresh stream is created for each invocation.
     */
    @Benchmark
    public void benchmarkSingleByteWrite(Blackhole bh) throws IOException {
        // Setup: Create fresh stream and underlying buffer for this invocation
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        FramedSnappyCompressorOutputStream compressor = new FramedSnappyCompressorOutputStream(baos);

        // Action: Write a single byte
        compressor.write(0xAA);

        // Cleanup: Finish the stream
        compressor.finish();
        compressor.close();

        // Consume the result
        bh.consume(baos.toByteArray());
    }

    /**
     * Benchmarks the finish operation (flush remaining data and write CRC).
     * This requires pre-filling the stream state.
     */
    @Benchmark
    public void benchmarkFinishOperation(Blackhole bh) throws IOException {
        // Setup: Create stream and pre-fill it with data
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        FramedSnappyCompressorOutputStream compressor = new FramedSnappyCompressorOutputStream(baos);

        // Pre-fill the stream state (this setup cost is outside the measured loop)
        compressor.write(inputData, 0, DATA_SIZE);

        // Action: Call finish()
        compressor.finish();

        // Cleanup: Close the stream
        compressor.close();

        // Consume the result
        bh.consume(baos.toByteArray());
    }
}
