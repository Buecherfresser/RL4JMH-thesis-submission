package bench.generated.c063;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.compressors.pack200.Pack200CompressorInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Pack200CompressorInputStreamBenchmark {

    private Pack200CompressorInputStream compressorInputStream;
    private ByteArrayInputStream compressedInput;
    private byte[] testBuffer;
    private static final int BUFFER_SIZE = 4096;

    // NOTE: In a real scenario, COMPRESSED_DATA must be a valid Pack200 compressed payload.
    // Since we cannot generate one here, we use a placeholder byte array.
    // This placeholder assumes the stream initialization succeeds and measures the stream operations.
    private static final byte[] COMPRESSED_DATA = new byte[1024 * 10]; 

    @Setup(Level.Trial)
    public void setupTrial() throws IOException {
        // 1. Setup the compressed input stream
        compressedInput = new ByteArrayInputStream(COMPRESSED_DATA);

        // 2. Initialize the subject under test (Pack200CompressorInputStream)
        // This setup simulates the decompression process initialization.
        compressorInputStream = new Pack200CompressorInputStream(compressedInput);

        // 3. Setup reusable buffer
        testBuffer = new byte[BUFFER_SIZE];
    }

    @Setup(Level.Invocation)
    public void setupInvocation() throws IOException {
        // Reset the stream state before each invocation to ensure consistent timing
        compressorInputStream.reset();
    }

    @Benchmark
    public int benchmarkReadSingleByte(Blackhole bh) throws IOException {
        int result = compressorInputStream.read();
        bh.consume(result);
        return result;
    }

    @Benchmark
    public int benchmarkReadIntoBuffer(Blackhole bh) throws IOException {
        int bytesRead = compressorInputStream.read(testBuffer);
        bh.consume(bytesRead);
        return bytesRead;
    }

    @Benchmark
    public int benchmarkReadSpecificRange(Blackhole bh) throws IOException {
        // Read 100 bytes starting at offset 0
        int bytesRead = compressorInputStream.read(testBuffer, 0, 100);
        bh.consume(bytesRead);
        return bytesRead;
    }

    @Benchmark
    public int benchmarkAvailable(Blackhole bh) throws IOException {
        int available = compressorInputStream.available();
        bh.consume(available);
        return available;
    }

    @Benchmark
    public long benchmarkSkip(Blackhole bh) throws IOException {
        // Skip 100 bytes
        long skipped = compressorInputStream.skip(100);
        bh.consume(skipped);
        return skipped;
    }

    @Benchmark
    public void benchmarkMarkSupported(Blackhole bh) {
        boolean supported = compressorInputStream.markSupported();
        bh.consume(supported);
    }

    @Benchmark
    public void benchmarkMark(Blackhole bh) throws IOException {
        // Mark 1024 bytes
        compressorInputStream.mark(1024);
        bh.consume(true);
    }

    @Benchmark
    public void benchmarkReset(Blackhole bh) throws IOException {
        // Reset the stream
        compressorInputStream.reset();
        bh.consume(true);
    }
}
