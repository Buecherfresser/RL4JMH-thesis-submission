package bench.generated.c087;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import org.apache.commons.compress.utils.CountingInputStream;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class CountingInputStreamBenchmark {

    // State field to hold the stream wrapper instance
    private CountingInputStream countingInputStream;

    // Fixed payload for testing. Must not be static final if it were mutable,
    // but here it's just data, so it's fine.
    private byte[] testData;

    @Setup
    public void setup() throws IOException {
        // Create a fixed payload (e.g., 10KB of data)
        this.testData = new byte[1024 * 10];
        // Fill with some non-zero data to ensure read operations succeed
        for (int i = 0; i < this.testData.length; i++) {
            this.testData[i] = (byte) (i % 256);
        }

        // Create the underlying stream
        InputStream underlyingStream = new ByteArrayInputStream(this.testData);

        // Initialize the CountingInputStream wrapper
        this.countingInputStream = new CountingInputStream(underlyingStream);
    }

    @Benchmark
    public void benchmarkRead(Blackhole bh) throws IOException {
        // Call the read method. We don't care about the return value, just the execution time.
        try {
            countingInputStream.read();
        } catch (IOException e) {
            // Ignore IOExceptions for benchmarking purposes if they occur in the path
        }
        bh.consume(null);
    }

    @Benchmark
    public void benchmarkReadWithBuffer(Blackhole bh) throws IOException {
        // Call the read method with a buffer.
        try {
            countingInputStream.read(new byte[100]);
        } catch (IOException e) {
            // Ignore IOExceptions
        }
        bh.consume(null);
    }

    @Benchmark
    public void benchmarkReadWithOffsetAndLength(Blackhole bh) throws IOException {
        // Call the read method with offset and length.
        try {
            // Read 10 bytes starting from offset 500
            countingInputStream.read(new byte[10], 500, 10);
        } catch (IOException e) {
            // Ignore IOExceptions
        }
        bh.consume(null);
    }
}
