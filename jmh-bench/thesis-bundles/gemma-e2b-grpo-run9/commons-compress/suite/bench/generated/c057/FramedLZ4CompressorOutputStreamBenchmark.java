package bench.generated.c057;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

import org.apache.commons.compress.compressors.lz4.FramedLZ4CompressorOutputStream;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class FramedLZ4CompressorOutputStreamBenchmark {

    // We don't need @State fields since the stream is created and destroyed per benchmark
    // to ensure isolation, which is safer for stream-based operations.

    /**
     * Benchmarks the core write operation of the FramedLZ4CompressorOutputStream.
     * This tests the internal buffering and compression logic for a single write call.
     *
     * @param bh Blackhole to consume the result.
     * @throws IOException if stream creation fails (JMH handles this by skipping the benchmark run).
     */
    @Benchmark
    public void benchmarkWrite(Blackhole bh) throws IOException {
        // 1. Setup: Create a fresh output stream for each invocation.
        try (OutputStream baos = new ByteArrayOutputStream()) {
            // Instantiate the compressor stream using default parameters.
            // This constructor performs initial setup (writing signature, frame descriptor).
            try (FramedLZ4CompressorOutputStream compressor = new FramedLZ4CompressorOutputStream(baos)) {
                
                // 2. Action: Write some data.
                byte[] data = new byte[1024]; // 1KB of data
                // Fill data with non-zero values to ensure some processing occurs
                for (int i = 0; i < data.length; i++) {
                    data[i] = (byte) (i % 256);
                }
                
                // Call the method under test.
                compressor.write(data, 0, data.length);
                
                // 3. Consume result: Ensure the operation isn't optimized away.
                // We don't need to read the output here, just ensure the method runs.
                bh.consume(compressor);
            }
        }
    }

    /**
     * Benchmarks the write operation using a different, smaller data size.
     * This tests the overhead for smaller payloads.
     *
     * @param bh Blackhole to consume the result.
     * @throws IOException if stream creation fails.
     */
    @Benchmark
    public void benchmarkWriteSmallData(Blackhole bh) throws IOException {
        try (OutputStream baos = new ByteArrayOutputStream()) {
            try (FramedLZ4CompressorOutputStream compressor = new FramedLZ4CompressorOutputStream(baos)) {
                
                byte[] data = new byte[128]; // 128 bytes of data
                for (int i = 0; i < data.length; i++) {
                    data[i] = (byte) (i % 256);
                }
                
                compressor.write(data, 0, data.length);
                bh.consume(compressor);
            }
        }
    }
}
