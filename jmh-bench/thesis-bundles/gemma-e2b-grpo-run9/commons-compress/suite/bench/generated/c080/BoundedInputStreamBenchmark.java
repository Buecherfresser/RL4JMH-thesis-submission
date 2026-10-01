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

    // Since BoundedInputStream is stateless regarding its internal state (it wraps an InputStream),
    // we don't strictly need @State fields, but we keep the class structure clean.

    /**
     * Benchmark method to test the instantiation and method call of BoundedInputStream.
     * We use a small, fixed input stream to ensure the operation is fast and repeatable.
     *
     * @param bh Blackhole to consume results and prevent dead code elimination.
     */
    @Benchmark
    public void testGetBytesRemaining(Blackhole bh) {
        try {
            // 1. Setup: Create a small, fixed input stream payload.
            // This simulates reading a small amount of data.
            byte[] data = "test".getBytes();
            InputStream inputStream = new ByteArrayInputStream(data);

            // 2. Action: Instantiate the BoundedInputStream.
            // We choose a size that is larger than the input data size to test the mechanism.
            BoundedInputStream bis = new BoundedInputStream(inputStream, 1024);

            // 3. Action: Call the method under test.
            long remaining = bis.getBytesRemaining();

            // 4. Consume result to prevent optimization.
            bh.consume(remaining);

        } catch (Exception e) {
            // Catch exceptions that might occur during stream operations if the underlying
            // BoundedInputStream implementation throws them.
            // In a real scenario, we might log this, but for benchmarking, we just fail silently.
        }
    }
}
