package bench.generated.c095;

import org.apache.commons.compress.utils.SkipShieldingInputStream;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
public class SkipShieldingInputStreamBenchmark {

    private InputStream inputStream;
    private SkipShieldingInputStream skipInputStream;

    // Define a reasonably large payload for testing
    private static final int PAYLOAD_SIZE = 1024 * 1024; // 1 MB

    @Setup
    public void setup() throws IOException {
        // 1. Create the underlying input stream from a large byte array
        byte[] data = new byte[PAYLOAD_SIZE];
        // Fill data with some content (e.g., non-zero values)
        for (int i = 0; i < PAYLOAD_SIZE; i++) {
            data[i] = (byte) (i % 256);
        }
        this.inputStream = new ByteArrayInputStream(data);

        // 2. Wrap the input stream with the class under test
        this.skipInputStream = new SkipShieldingInputStream(this.inputStream);
    }

    @Benchmark
    public void benchmarkSkipSmall(Blackhole bh) throws IOException {
        // Test skipping a small amount (e.g., 1KB)
        long skipAmount = 1024;
        long result = skipInputStream.skip(skipAmount);
        
        // Consume the result
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkSkipMedium(Blackhole bh) throws IOException {
        // Test skipping an amount close to the internal buffer size (8192)
        long skipAmount = 8192;
        long result = skipInputStream.skip(skipAmount);
        
        // Consume the result
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkSkipLarge(Blackhole bh) throws IOException {
        // Test skipping a large amount (e.g., 16KB)
        long skipAmount = 16384;
        long result = skipInputStream.skip(skipAmount);
        
        // Consume the result
        bh.consume(result);
    }
}
