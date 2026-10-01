package bench.generated.c074;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.compressors.zstandard.ZstdCompressorInputStream;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ZstdCompressorInputStreamBenchmark {

    private InputStream compressedInputStream;
    private ZstdCompressorInputStream zstdCompressorInputStream;
    private byte[] readBuffer;
    private final int BUFFER_SIZE = 4096;

    @Setup(Level.Trial)
    public void setup() throws IOException {
        // Simulate a fixed, compressed payload. In a real scenario, this would be generated
        // by ZstdCompressorOutputStream. We use a placeholder byte array here.
        byte[] compressedData = new byte[1024 * 10]; // 10KB simulated compressed data

        // 1. Setup the raw compressed input stream
        this.compressedInputStream = new ByteArrayInputStream(compressedData);

        // 2. Setup the ZstdCompressorInputStream
        // We use the constructor that takes only the InputStream.
        this.zstdCompressorInputStream = new ZstdCompressorInputStream(compressedInputStream);

        // 3. Setup the read buffer
        this.readBuffer = new byte[BUFFER_SIZE];
    }

    @Setup(Level.Invocation)
    public void setupInvocation() throws IOException {
        // For stateful operations (like reading), we must reset or recreate the stream
        // to ensure a clean state for each invocation.
        // Since the underlying compressedInputStream is reusable (ByteArrayInputStream),
        // we just need to reset the ZstdCompressorInputStream.
        zstdCompressorInputStream.reset();
    }

    @Benchmark
    public int benchmarkReadSingleByte(Blackhole bh) throws IOException {
        int result = zstdCompressorInputStream.read();
        bh.consume(result);
        return result;
    }

    @Benchmark
    public int benchmarkReadBulk(Blackhole bh) throws IOException {
        int bytesRead = zstdCompressorInputStream.read(readBuffer, 0, BUFFER_SIZE);
        bh.consume(bytesRead);
        return bytesRead;
    }

    @Benchmark
    public long benchmarkAvailable(Blackhole bh) throws IOException {
        long available = zstdCompressorInputStream.available();
        bh.consume(available);
        return available;
    }

    @Benchmark
    public long benchmarkGetCompressedCount(Blackhole bh) throws IOException {
        long count = zstdCompressorInputStream.getCompressedCount();
        bh.consume(count);
        return count;
    }

    @Benchmark
    public long benchmarkSkip(Blackhole bh) throws IOException {
        // Skip a significant portion of the stream
        long skipped = zstdCompressorInputStream.skip(1024 * 5);
        bh.consume(skipped);
        return skipped;
    }

    @Benchmark
    public void benchmarkMarkAndReset(Blackhole bh) throws IOException {
        // Mark the stream
        zstdCompressorInputStream.mark(1024);

        // Skip some bytes
        zstdCompressorInputStream.skip(100);

        // Reset the stream
        zstdCompressorInputStream.reset();

        // Read one byte to ensure the operation was successful and consumed
        int result = zstdCompressorInputStream.read();
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkClose(Blackhole bh) throws IOException {
        // Closing the stream
        zstdCompressorInputStream.close();
        // Consume a dummy value to prevent dead code elimination
        bh.consume(true);
    }
}
