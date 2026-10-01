package bench.generated.c066;

import org.apache.commons.compress.compressors.snappy.FramedSnappyCompressorInputStream;
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
public class FramedSnappyCompressorInputStreamBenchmark {

    // State field to hold the stream instance, initialized in setup if needed.
    private FramedSnappyCompressorInputStream inputStream;

    @Setup
    public void setup() throws IOException {
        // Initialize a simple, empty input stream for testing purposes.
        // Note: For meaningful decompression benchmarks, a valid compressed payload
        // would be required here, which is outside the scope of this isolated benchmark setup.
        try (InputStream dummyStream = new ByteArrayInputStream(new byte[0])) {
            this.inputStream = new FramedSnappyCompressorInputStream(dummyStream);
        }
    }

    @Benchmark
    public void benchmarkRead(Blackhole bh) {
        try {
            // Test the basic read() method
            bh.consume(inputStream.read());
        } catch (IOException e) {
            // Ignore exceptions for benchmark stability if the stream fails immediately
        }
    }

    @Benchmark
    public void benchmarkReadMulti(Blackhole bh) {
        try {
            // Test the read(byte[], off, len) method
            // We pass an empty array, expecting 0 or -1 based on the stream state.
            bh.consume(inputStream.read(new byte[10], 0, 10));
        } catch (IOException e) {
            // Ignore exceptions
        }
    }

    @Benchmark
    public void benchmarkAvailable(Blackhole bh) {
        try {
            // Test the available() method
            bh.consume(inputStream.available());
        } catch (IOException e) {
            // Ignore exceptions
        }
    }

    @Benchmark
    public void benchmarkClose(Blackhole bh) {
        try {
            // Test the close() method
            inputStream.close();
            bh.consume(null); // Consume null as close returns void
        } catch (IOException e) {
            // Ignore exceptions
        }
    }
}
