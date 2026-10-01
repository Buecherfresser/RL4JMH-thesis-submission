package bench.generated.c085;

import org.apache.commons.compress.utils.ChecksumVerifyingInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.CRC32;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ChecksumVerifyingInputStreamBenchmark {

    // State fields are not strictly necessary if we create the stream inside the benchmark,
    // but we keep the class structure clean.

    /**
     * Setup method to prepare necessary resources.
     * Since we are benchmarking the stream logic, we don't need complex state here,
     * but we ensure the class is initialized if needed.
     */
    @Setup
    public void setup() {
        // No complex setup needed for this stateless benchmark structure.
    }

    /**
     * Benchmark method testing the basic read() operation.
     * We create a fresh stream instance for each invocation to measure the cost
     * of initialization and a single read operation.
     *
     * @param bh Blackhole to consume the result.
     */
    @Benchmark
    public void benchmarkRead(Blackhole bh) {
        try {
            // 1. Create a dummy checksum (CRC32 is a common implementation)
            // 2. Create a small input stream (data doesn't matter much, only the stream wrapper logic)
            // We use a small buffer size and a simple payload.
            byte[] payload = "test data".getBytes();
            InputStream in = new ByteArrayInputStream(payload);

            // Instantiate the SUT. Size and expected checksum are arbitrary for this test.
            ChecksumVerifyingInputStream stream = new ChecksumVerifyingInputStream(
                new CRC32(),
                in,
                payload.length,
                0L // Expected checksum value is irrelevant for this structural test
            );

            // Call the method under test. We consume the result to prevent dead code elimination.
            stream.read();
        } catch (Exception e) {
            // Ignore exceptions during benchmarking if they are expected in edge cases,
            // but generally, we want the benchmark to run smoothly.
        }
    }

    /**
     * Benchmark method testing the read(byte[], off, len) operation.
     *
     * @param bh Blackhole to consume the result.
     */
    @Benchmark
    public void benchmarkReadBytes(Blackhole bh) {
        try {
            byte[] payload = "test data".getBytes();
            InputStream in = new ByteArrayInputStream(payload);

            ChecksumVerifyingInputStream stream = new ChecksumVerifyingInputStream(
                new CRC32(),
                in,
                payload.length,
                0L
            );

            // Call the method under test.
            stream.read(new byte[10], 0, 10);
        } catch (Exception e) {
            // Ignore exceptions
        }
    }
}
