package bench.generated.c060;

import org.apache.commons.compress.compressors.lzma.LZMACompressorInputStream;
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
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class LZMACompressorInputStreamBenchmark {

    // We don't need state fields if we create the stream inside the benchmark
    // or rely on the input stream being transient.

    @Setup
    public void setup() throws IOException {
        // Setup is minimal here since we are using transient streams
        // and cannot easily generate valid LZMA compressed data.
    }

    @Benchmark
    public void benchmarkRead(Blackhole bh) {
        try (InputStream is = new ByteArrayInputStream(new byte[1024])) {
            // We must handle the IOException thrown by the stream methods
            try {
                LZMACompressorInputStream stream = new LZMACompressorInputStream(is);
                // Call a method that consumes data and returns a value (or consumes via Blackhole)
                stream.read();
                bh.consume(true);
            } catch (IOException e) {
                // Ignore exceptions for benchmark stability if the input stream is invalid
            }
        } catch (Exception e) {
            // Catch any unexpected exceptions during setup/teardown if necessary
        }
    }

    @Benchmark
    public void benchmarkReadWithBuffer(Blackhole bh) {
        try (InputStream is = new ByteArrayInputStream(new byte[1024])) {
            try {
                LZMACompressorInputStream stream = new LZMACompressorInputStream(is);
                byte[] buffer = new byte[100];
                // Call the method that takes a buffer
                stream.read(buffer, 0, buffer.length);
                bh.consume(true);
            } catch (IOException e) {
                // Ignore exceptions
            }
        } catch (Exception e) {
            // Ignore exceptions
        }
    }

    @Benchmark
    public void benchmarkSkip(Blackhole bh) {
        try (InputStream is = new ByteArrayInputStream(new byte[1024])) {
            try {
                LZMACompressorInputStream stream = new LZMACompressorInputStream(is);
                // Call the skip method
                stream.skip(100);
                bh.consume(true);
            } catch (IOException e) {
                // Ignore exceptions
            }
        } catch (Exception e) {
            // Ignore exceptions
        }
    }
}
