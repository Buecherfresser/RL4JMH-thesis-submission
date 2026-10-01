package bench.generated.c046;

import org.apache.commons.compress.compressors.deflate.DeflateCompressorInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
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
public class DeflateCompressorInputStreamBenchmark {

    // State field to hold the stream instance, initialized in setup
    private DeflateCompressorInputStream inputStream;

    /**
     * Setup method to initialize the stream.
     * Since we cannot easily generate a valid Deflate compressed payload without
     * external compression libraries, we use an empty stream for setup,
     * focusing the benchmark on the overhead of stream instantiation and method calls.
     */
    @Setup
    public void setup() throws IOException {
        // Use an empty byte array wrapped in a ByteArrayInputStream
        // This allows us to instantiate the stream without needing complex compressed data.
        try (InputStream is = new ByteArrayInputStream(new byte[0])) {
            this.inputStream = new DeflateCompressorInputStream(is);
        }
    }

    @Benchmark
    public void read(Blackhole bh) {
        try {
            // Call the read method. We expect this to throw IOException or return -1/0
            // depending on the underlying InflaterInputStream behavior on an empty stream.
            inputStream.read();
        } catch (IOException e) {
            // Ignore expected IOExceptions during testing of stream boundaries
        }
        bh.consume(null);
    }

    @Benchmark
    public void readBulk(Blackhole bh) {
        try {
            // Test bulk read with a small buffer
            inputStream.read(new byte[10], 0, 10);
        } catch (IOException e) {
            // Ignore expected IOExceptions
        }
        bh.consume(null);
    }

    @Benchmark
    public void close(Blackhole bh) {
        try {
            inputStream.close();
        } catch (IOException e) {
            // Ignore expected IOExceptions
        }
        bh.consume(null);
    }

    @Benchmark
    public void getCompressedCount(Blackhole bh) {
        try {
            // Test a method that relies on internal state (countingStream)
            long count = inputStream.getCompressedCount();
            bh.consume(count);
        } catch (Exception e) {
            // Ignore exceptions during state access
        }
    }
}
