package bench.generated.c075;

import org.apache.commons.compress.compressors.CompressorOutputStream;
import org.apache.commons.compress.compressors.zstandard.ZstdCompressorOutputStream;
import org.apache.commons.compress.compressors.zstandard.ZstdCompressorInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class ZstdCompressorOutputStreamBenchmark {

    // Fixed payload size for testing
    private static final int PAYLOAD_SIZE = 1024 * 1024; // 1 MB
    private byte[] inputData;
    private byte[] compressedData;

    // State for compression setup
    private ZstdCompressorOutputStream compressor;

    @Setup
    public void setup() throws IOException {
        // 1. Build fixed input data
        inputData = new byte[PAYLOAD_SIZE];
        for (int i = 0; i < PAYLOAD_SIZE; i++) {
            inputData[i] = (byte) (i % 256);
        }

        // 2. Setup compressor instance (using default settings for simplicity)
        compressor = ZstdCompressorOutputStream.builder().get();
    }

    @Benchmark
    public void benchmarkCompression(Blackhole bh) throws IOException {
        // Use ByteArrayOutputStream to capture compressed output
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            // Use the compressor instance configured in setup
            try (ZstdCompressorOutputStream stream = compressor) {
                stream.write(inputData, 0, inputData.length);
                stream.finish(); // Ensure all data is flushed
                byte[] compressed = baos.toByteArray();
                bh.consume(compressed);
            }
        }
    }

    @Benchmark
    public void benchmarkDecompression(Blackhole bh) throws IOException {
        // Setup: We must re-run the compression logic to get the input for decompression,
        // adhering to the rule of one call per benchmark.

        // Compression phase (to generate input data)
        byte[] compressed = new byte[PAYLOAD_SIZE * 2]; // Estimate max size
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            try (ZstdCompressorOutputStream stream = compressor) {
                stream.write(inputData, 0, inputData.length);
                stream.finish();
                compressed = baos.toByteArray();
            }
        }

        // Decompression phase
        try (ByteArrayInputStream bais = new ByteArrayInputStream(compressed)) {
            // FIX: Use the constructor to instantiate ZstdCompressorInputStream,
            // as the static factory method createCompressorInputStream was not found.
            try (ZstdCompressorInputStream stream = new ZstdCompressorInputStream(bais)) {
                byte[] decompressed = stream.readAllBytes();
                bh.consume(decompressed);
            }
        }
    }
}
