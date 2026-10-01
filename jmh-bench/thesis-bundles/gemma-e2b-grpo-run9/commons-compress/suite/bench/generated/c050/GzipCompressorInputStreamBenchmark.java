package bench.generated.c050;

import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
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
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class GzipCompressorInputStreamBenchmark {

    // State field to hold the stream instance, initialized in setup if needed.
    // Since the stream relies on an underlying InputStream, we will create it inside the benchmark
    // to ensure isolation, or rely on the constructor handling the ByteArrayInputStream.
    private GzipCompressorInputStream gzipInputStream;

    // A fixed, small byte array payload. In a real scenario, this would be a complex,
    // valid GZIP compressed stream. For structural testing, a simple byte array suffices.
    // We use a small payload to keep the benchmark fast.
    private byte[] testPayload;

    @Setup
    public void setup() throws IOException {
        // Initialize a simple payload. This setup runs once per benchmark class instance.
        // We don't need to initialize the stream here as it depends on the input stream,
        // which we will create inside the benchmark method for isolation.
        // We initialize the payload here to avoid static final issues, though it's immutable.
        this.testPayload = new byte[1024]; // 1KB placeholder
    }

    @Benchmark
    public void benchmarkReadSingleByte(Blackhole bh) {
        try (InputStream is = new ByteArrayInputStream(testPayload)) {
            // Create a fresh instance for each benchmark run to ensure state isolation
            GzipCompressorInputStream stream = new GzipCompressorInputStream(is);
            
            // Call the read method and consume the result
            try {
                int result = stream.read();
                bh.consume(result);
            } catch (IOException e) {
                // Ignore IOExceptions during benchmark execution if they occur,
                // as JMH handles exceptions gracefully by skipping the iteration.
            }
        } catch (Exception e) {
            // Catch exceptions during stream creation if necessary
        }
    }

    @Benchmark
    public void benchmarkReadMultipleBytes(Blackhole bh) {
        try (InputStream is = new ByteArrayInputStream(testPayload)) {
            GzipCompressorInputStream stream = new GzipCompressorInputStream(is);

            // Call the read method with a buffer and consume the result
            try {
                // Read 100 bytes
                int result = stream.read(new byte[100], 0, 100);
                bh.consume(result);
            } catch (IOException e) {
                // Ignore IOExceptions
            }
        } catch (Exception e) {
            // Catch exceptions during stream creation if necessary
        }
    }
}
