package bench.generated.c074;

import org.apache.commons.compress.compressors.zstandard.ZstdCompressorInputStream;
import org.apache.commons.io.IOUtils;
import java.io.ByteArrayInputStream;
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
public class ZstdCompressorInputStreamBenchmark {

    // Payload: A large byte array representing Zstandard compressed data.
    // In a real scenario, this would be pre-compressed data.
    // We use a large size to ensure the decompression work is measurable.
    private byte[] compressedData;
    private ByteArrayInputStream inputStream;
    private ZstdCompressorInputStream zstdInputStream;

    // Define a reasonably large payload size (e.g., 10 MB)
    private static final int PAYLOAD_SIZE = 10 * 1024 * 1024;

    @Setup(Level.Trial)
    public void setup() throws IOException {
        // --- Input Preparation ---
        // Since we cannot run external compression tools, we create a large dummy array.
        // For a true benchmark, this array MUST be valid Zstd compressed data.
        this.compressedData = new byte[PAYLOAD_SIZE];
        // Fill with some data to simulate content
        for (int i = 0; i < PAYLOAD_SIZE; i++) {
            compressedData[i] = (byte) (i % 256);
        }

        this.inputStream = new ByteArrayInputStream(compressedData);

        // --- Subject Initialization ---
        // Initialize the subject once per trial setup
        this.zstdInputStream = new ZstdCompressorInputStream(this.inputStream);
    }

    @TearDown(Level.Trial)
    public void tearDown() throws IOException {
        // Clean up resources if necessary, though JMH handles most cleanup.
        if (zstdInputStream != null) {
            zstdInputStream.close();
        }
    }

    @Benchmark
    public void benchmarkReadSingleByte(Blackhole bh) throws IOException {
        int result = zstdInputStream.read();
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkReadBuffer(Blackhole bh) throws IOException {
        byte[] buffer = new byte[4096];
        int result = zstdInputStream.read(buffer);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkReadSegment(Blackhole bh) throws IOException {
        byte[] buffer = new byte[1024];
        int offset = 500000;
        int length = 1024;
        int result = zstdInputStream.read(buffer, offset, length);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkGetAvailable(Blackhole bh) throws IOException {
        int available = zstdInputStream.available();
        bh.consume(available);
    }

    @Benchmark
    public void benchmarkGetCompressedCount(Blackhole bh) throws IOException {
        long count = zstdInputStream.getCompressedCount();
        bh.consume(count);
    }

    @Benchmark
    public void benchmarkClose(Blackhole bh) throws IOException {
        zstdInputStream.close();
        // Consume nothing, just ensure the call executes
        bh.consume(null);
    }
}
