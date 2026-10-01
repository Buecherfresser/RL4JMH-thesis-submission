package bench.generated.c095;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import org.apache.commons.compress.utils.SkipShieldingInputStream;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class SkipShieldingInputStreamBenchmark {

    // State field to hold the stream instance, initialized in setup
    private SkipShieldingInputStream skipInputStream;

    // Fixed payload for the input stream setup
    private byte[] fixedPayload;

    @Setup
    public void setup() throws IOException {
        // Create a repeatable, in-memory input stream.
        // Using a non-empty payload ensures the read operation has something to process.
        this.fixedPayload = new byte[8192 * 2]; // 16KB payload
        for (int i = 0; i < this.fixedPayload.length; i++) {
            this.fixedPayload[i] = (byte) (i % 256);
        }

        try (InputStream is = new ByteArrayInputStream(this.fixedPayload)) {
            // Initialize the subject under test.
            // We pass the stream to the constructor.
            this.skipInputStream = new SkipShieldingInputStream(is);
        }
    }

    @Benchmark
    public void benchmarkSkip(Blackhole bh) {
        try {
            // Call the method being tested.
            // We call skip with a large value to ensure it reads multiple buffer chunks.
            long bytesRead = this.skipInputStream.skip(1024 * 1024); // Try to skip 1MB
            bh.consume(bytesRead);
        } catch (IOException e) {
            // In a real scenario, we might log this, but for JMH, we just let the benchmark proceed.
        }
    }
}
