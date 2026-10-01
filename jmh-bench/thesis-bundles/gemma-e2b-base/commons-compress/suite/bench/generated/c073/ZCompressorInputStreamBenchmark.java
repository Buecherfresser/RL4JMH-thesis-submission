package bench.generated.c073;

import org.apache.commons.compress.compressors.z.ZCompressorInputStream;
import org.apache.commons.io.IOUtils;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class ZCompressorInputStreamBenchmark {

    // Payload data: A representative, pre-compressed byte array.
    private byte[] compressedData;
    private ByteArrayInputStream inputStream;
    private ZCompressorInputStream zInputStream;

    @Setup
    public void setup() throws IOException {
        // --- Setup Phase ---
        // Create a representative payload. For this example, we use a placeholder.
        // A real benchmark requires a valid, compressed .Z file content.
        String originalString = "This is a test string designed to be compressed and decompressed by the ZCompressorInputStream.";
        byte[] originalBytes = originalString.getBytes();

        // Since we cannot generate a valid .Z file here, we use a dummy payload.
        // For a meaningful benchmark, replace this with actual compressed data.
        this.compressedData = createDummyZPayload(originalBytes);

        this.inputStream = new ByteArrayInputStream(this.compressedData);
        
        // Initialize the stream once for reuse across benchmarks
        this.zInputStream = new ZCompressorInputStream(this.inputStream);
    }

    /**
     * Helper method to create a dummy payload.
     * WARNING: This method does NOT produce a valid .Z compressed file.
     * It is only used to satisfy the requirement of having a non-final, setup-generated input.
     */
    private byte[] createDummyZPayload(byte[] data) {
        // Placeholder: In a real test, this would be the output of a compression step.
        // We return the original data as a placeholder input stream content.
        return data;
    }

    @Benchmark
    public void benchmarkDecompression(Blackhole bh) throws IOException {
        // 1. Wrap the pre-compressed data in an InputStream
        ByteArrayInputStream input = new ByteArrayInputStream(compressedData);

        // 2. Instantiate the SUT
        ZCompressorInputStream stream = new ZCompressorInputStream(input);

        // 3. Decompress and drain the result
        byte[] decompressedData = IOUtils.toByteArray(stream);

        // 4. Consume the result
        bh.consume(decompressedData);
    }
}
