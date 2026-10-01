package bench.generated.c069;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.compressors.snappy.SnappyCompressorOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class SnappyCompressorOutputStreamBenchmark {

    private byte[] inputData;
    private final int INPUT_SIZE = 1024; // 1KB input payload

    @Setup(Level.Trial)
    public void setup() {
        // Generate a fixed, compressible input payload
        inputData = new byte[INPUT_SIZE];
        Random random = new Random(42); // Fixed seed for reproducibility
        random.nextBytes(inputData);
    }

    /**
     * Benchmarks the full compression cycle: initialization, writing data, and finishing the stream.
     * This measures the overhead of the SnappyCompressorOutputStream implementation.
     */
    @Benchmark
    public void benchmarkFullCompressionCycle(Blackhole bh) throws IOException {
        // 1. Setup output stream
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        // 2. Instantiate SUT. We use the default block size (32k).
        // The uncompressed size must be provided.
        SnappyCompressorOutputStream compressor = new SnappyCompressorOutputStream(baos, INPUT_SIZE);

        try {
            // 3. Write input data
            compressor.write(inputData, 0, INPUT_SIZE);

            // 4. Finish compression
            compressor.finish();

            // 5. Get compressed result
            byte[] compressedData = baos.toByteArray();

            // 6. Consume result
            bh.consume(compressedData);
        } finally {
            // 7. Ensure stream is closed
            compressor.close();
        }
    }
}
