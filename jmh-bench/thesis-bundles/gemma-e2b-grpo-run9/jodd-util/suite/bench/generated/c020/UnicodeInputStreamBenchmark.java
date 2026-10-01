package bench.generated.c020;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import jodd.io.UnicodeInputStream;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class UnicodeInputStreamBenchmark {

    // Helper method to create a stream for testing
    private UnicodeInputStream createStream(byte[] data, Charset targetEncoding) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        return new UnicodeInputStream(bais, targetEncoding);
    }

    // --- Benchmarks for read() ---

    @Benchmark
    public void benchmarkRead_UTF8(Blackhole bh) throws IOException {
        // Test case: Data starting with UTF-8 BOM (0xEF, 0xBB, 0xBF)
        byte[] data = new byte[]{
            (byte) 0xEF, (byte) 0xBB, (byte) 0xBF, // UTF-8 BOM
            (byte) 0x48, (byte) 0x65, (byte) 0x6C, (byte) 0x6C
        };
        try (UnicodeInputStream stream = createStream(data, StandardCharsets.UTF_8)) {
            // Calling read() triggers init()
            int result = stream.read();
            bh.consume(result);
        }
    }

    @Benchmark
    public void benchmarkRead_NoBOM(Blackhole bh) throws IOException {
        // Test case: Data with no BOM
        byte[] data = new byte[]{
            (byte) 0x48, (byte) 0x65, (byte) 0x6C, (byte) 0x6C
        };
        try (UnicodeInputStream stream = createStream(data, StandardCharsets.UTF_8)) {
            // Calling read() triggers init()
            int result = stream.read();
            bh.consume(result);
        }
    }

    @Benchmark
    public void benchmarkRead_UTF16LE(Blackhole bh) throws IOException {
        // Test case: Data starting with UTF-16LE BOM (0xFF, 0xFE)
        byte[] data = new byte[]{
            (byte) 0xFF, (byte) 0xFE, // UTF-16LE BOM
            (byte) 0x48, (byte) 0x65
        };
        try (UnicodeInputStream stream = createStream(data, StandardCharsets.UTF_16LE)) {
            // Calling read() triggers init()
            int result = stream.read();
            bh.consume(result);
        }
    }

    // --- Benchmarks for getDetectedEncoding() ---

    @Benchmark
    public void benchmarkGetDetectedEncoding_UTF32BE(Blackhole bh) throws IOException {
        // Test case: Data starting with UTF-32BE BOM (0x00, 0x00, 0xFE, 0xFF)
        byte[] data = new byte[]{(byte) 0x00, (byte) 0x00, (byte) 0xFE, (byte) 0xFF};
        try (UnicodeInputStream stream = createStream(data, null)) { // null target encoding triggers detect mode
            // Calling getDetectedEncoding() triggers init()
            stream.getDetectedEncoding();
            bh.consume(null);
        }
    }

    @Benchmark
    public void benchmarkGetDetectedEncoding_NoBOM(Blackhole bh) throws IOException {
        // Test case: Data with no BOM
        byte[] data = new byte[]{(byte) 0x48, (byte) 0x65};
        try (UnicodeInputStream stream = createStream(data, null)) {
            // Calling getDetectedEncoding() triggers init()
            stream.getDetectedEncoding();
            bh.consume(null);
        }
    }
}
