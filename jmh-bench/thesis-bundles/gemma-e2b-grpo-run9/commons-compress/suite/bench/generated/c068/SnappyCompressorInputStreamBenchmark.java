package bench.generated.c068;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import org.apache.commons.compress.compressors.snappy.SnappyCompressorInputStream;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class SnappyCompressorInputStreamBenchmark {

    private SnappyCompressorInputStream inputStream;

    // Fixed payload for testing. Must be immutable or handled carefully if the SUT mutates state.
    // Since SnappyCompressorInputStream is a stream wrapper, we can reuse the input stream
    // for read operations, but we must create a new instance for each benchmark if it holds
    // mutable state that isn't reset (which is true for streams).
    private byte[] fixedPayload;

    @Setup
    public void setup() throws IOException {
        // 1. Create a representative payload (e.g., a simple string converted to bytes)
        // For a real benchmark, this payload should be complex or large enough to stress the compression logic.
        // Here, we use a small, fixed payload.
        String originalString = "This is a test string for Snappy compression benchmarking.";
        this.fixedPayload = originalString.getBytes(java.nio.charset.StandardCharsets.UTF_8);

        // 2. Initialize the stream. We use ByteArrayInputStream to provide an in-memory source.
        // Since the constructor takes an InputStream, we wrap our payload.
        try (InputStream is = new ByteArrayInputStream(fixedPayload)) {
            // Using default block size for simplicity
            this.inputStream = new SnappyCompressorInputStream(is);
        }
    }

    @Benchmark
    public void readData(Blackhole bh) {
        try {
            // Attempt to read a small chunk of data. This forces the stream to potentially fill its buffer.
            int bytesRead = inputStream.read(new byte[100], 0, 100);
            bh.consume(bytesRead);
        } catch (IOException e) {
            // Ignore IOExceptions during benchmarking if they are expected in edge cases,
            // but in a real scenario, this should be handled or logged.
        }
    }

    @Benchmark
    public void readDataLarge(Blackhole bh) {
        try {
            // Attempt to read a larger chunk.
            int bytesRead = inputStream.read(new byte[1024], 0, 1024);
            bh.consume(bytesRead);
        } catch (IOException e) {
            // Ignore
        }
    }

    @Benchmark
    public void getSize(Blackhole bh) {
        // Test the getSize method, which should be read-only and fast.
        bh.consume(inputStream.getSize());
    }

    @Benchmark
    public void readDataZeroLength(Blackhole bh) {
        try {
            // Test reading zero length, which should return 0 immediately.
            int bytesRead = inputStream.read(new byte[10], 0, 0);
            bh.consume(bytesRead);
        } catch (IOException e) {
            // Ignore
        }
    }
}
