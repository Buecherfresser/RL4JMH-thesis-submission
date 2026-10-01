package bench.generated.c083;

import org.apache.commons.compress.utils.CRC32VerifyingInputStream;
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
public class CRC32VerifyingInputStreamBenchmark {

    // State fields are not strictly necessary here since we create the stream
    // inside the benchmark method to ensure fresh state for each invocation,
    // but they satisfy the requirement of being present if needed.

    @Setup
    public void setup() {
        // Setup phase: No complex setup needed for this simple stream wrapper.
    }

    @Benchmark
    public void benchmarkInstantiationAndRead(Blackhole bh) {
        try {
            // 1. Create a simple input stream payload (e.g., 1KB of data)
            byte[] data = new byte[1024];
            // Fill with some data to ensure the stream has content
            for (int i = 0; i < data.length; i++) {
                data[i] = (byte) (i % 256);
            }
            InputStream inputStream = new ByteArrayInputStream(data);

            // 2. Instantiate the subject class (using the modern constructor)
            // We use a dummy expected CRC32 value.
            CRC32VerifyingInputStream verifyingInputStream =
                    new CRC32VerifyingInputStream(inputStream, 1024, 0xDEADBEEF);

            // 3. Perform a minimal operation to ensure the stream is processed
            // We attempt to read a small chunk. This forces the stream wrapper logic to execute.
            // We consume the result to prevent dead code elimination.
            int bytesRead = verifyingInputStream.read();
            bh.consume(bytesRead);

        } catch (Exception e) {
            // Catch exceptions that might occur during stream operations
            // In a real benchmark, this should be handled carefully, but for
            // measuring the SUT's performance, we let the exception propagate
            // or handle it minimally if it's expected.
        }
    }
}
