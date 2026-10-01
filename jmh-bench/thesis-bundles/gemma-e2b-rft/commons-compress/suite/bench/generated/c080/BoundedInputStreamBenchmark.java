package bench.generated.c080;

import org.apache.commons.compress.utils.BoundedInputStream;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class BoundedInputStreamBenchmark {

    private InputStream inputStream;
    private BoundedInputStream boundedInputStream;

    // Setup method to prepare the input stream and the bounded stream instance
    @Setup
    public void setup() throws Exception {
        // 1. Create a fixed payload (e.g., 1MB of data)
        byte[] payload = new byte[1024 * 1024]; // 1 MB
        for (int i = 0; i < payload.length; i++) {
            payload[i] = (byte) (i % 256);
        }
        this.inputStream = new ByteArrayInputStream(payload);

        // 2. Define a fixed size limit (e.g., 100 KB)
        final long limit = 1024 * 100; // 100 KB

        // 3. Create the BoundedInputStream instance
        this.boundedInputStream = new BoundedInputStream(this.inputStream, limit);
    }

    /**
     * Benchmark method to test the getBytesRemaining() method.
     * This tests the calculation based on the fixed input stream and size limit set in @Setup.
     *
     * @param bh Blackhole to consume the result.
     */
    @Benchmark
    public void testGetBytesRemaining(Blackhole bh) {
        long remaining = boundedInputStream.getBytesRemaining();
        bh.consume(remaining);
    }
}
