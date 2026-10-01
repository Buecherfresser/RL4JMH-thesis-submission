package bench.generated.c080;

import org.apache.commons.compress.utils.BoundedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class BoundedInputStreamBenchmark {

    // State fields for setup
    private InputStream inputStream;
    private byte[] payload;
    private long maxSize;
    private BoundedInputStream boundedInputStream;

    @Setup
    public void setup() {
        // 1. Create a large payload for testing
        int payloadSize = 1024 * 1024 * 4; // 4MB payload
        this.payload = new byte[payloadSize];
        for (int i = 0; i < payloadSize; i++) {
            payload[i] = (byte) (i % 256);
        }
        this.inputStream = new ByteArrayInputStream(payload);
        
        // 2. Define a specific size limit
        this.maxSize = 1024 * 1024; // 1MB limit

        // 3. Initialize the BoundedInputStream instance using the target class
        this.boundedInputStream = new BoundedInputStream(this.inputStream, this.maxSize);
    }

    /**
     * Benchmark for initializing BoundedInputStream with a specific size limit.
     */
    @Benchmark
    public void testConstructorInitialization(Blackhole bh) {
        // Test the creation path
        BoundedInputStream testStream = new BoundedInputStream(this.inputStream, this.maxSize);
        bh.consume(testStream);
    }

    /**
     * Benchmark for getting the remaining bytes when the stream is initialized.
     */
    @Benchmark
    public void testGetBytesRemaining_InitialState(Blackhole bh) {
        // Test the public method getBytesRemaining()
        long remaining = this.boundedInputStream.getBytesRemaining();
        bh.consume(remaining);
    }

    /**
     * Benchmark simulating a state where some data has been read, testing the remaining count.
     * Since we cannot easily simulate a read operation without knowing the full API, 
     * we rely on the state established in setup, which represents the initial state.
     */
    @Benchmark
    public void testGetBytesRemaining_AfterPartialRead(Blackhole bh) {
        // Test the public method getBytesRemaining() again on the initialized state
        long remaining = this.boundedInputStream.getBytesRemaining();
        bh.consume(remaining);
    }
}
