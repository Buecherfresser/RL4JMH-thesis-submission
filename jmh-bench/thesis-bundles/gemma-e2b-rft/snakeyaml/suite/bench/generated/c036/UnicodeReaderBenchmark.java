package bench.generated.c036;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import org.yaml.snakeyaml.reader.UnicodeReader;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class UnicodeReaderBenchmark {

    // --- Test Data Setup ---

    // UTF-8 data (no BOM)
    private byte[] utf8Data;
    // UTF-16BE data (BOM: FE FF)
    private byte[] utf16BeData;
    // UTF-16LE data (BOM: FF FE)
    private byte[] utf16LeData;

    // Buffer for reading characters
    private char[] charBuffer;

    // --- Benchmarks ---

    /**
     * Benchmark for reading UTF-8 data (no BOM).
     * This tests the default UTF-8 path in init().
     */
    @Benchmark
    public void readUtf8(Blackhole bh) throws IOException {
        try (InputStream is = new ByteArrayInputStream(utf8Data);
             UnicodeReader reader = new UnicodeReader(is)) {

            // The read method calls init() internally
            int charsRead = reader.read(charBuffer, 0, charBuffer.length);
            bh.consume(charsRead);
        }
    }

    /**
     * Benchmark for reading UTF-16BE data (BOM: FE FF).
     * This tests the UTF-16BE path in init().
     */
    @Benchmark
    public void readUtf16Be(Blackhole bh) throws IOException {
        try (InputStream is = new ByteArrayInputStream(utf16BeData);
             UnicodeReader reader = new UnicodeReader(is)) {

            int charsRead = reader.read(charBuffer, 0, charBuffer.length);
            bh.consume(charsRead);
        }
    }

    /**
     * Benchmark for reading UTF-16LE data (BOM: FF FE).
     * This tests the UTF-16LE path in init().
     */
    @Benchmark
    public void readUtf16Le(Blackhole bh) throws IOException {
        try (InputStream is = new ByteArrayInputStream(utf16LeData);
             UnicodeReader reader = new UnicodeReader(is)) {

            int charsRead = reader.read(charBuffer, 0, charBuffer.length);
            bh.consume(charsRead);
        }
    }

    /**
     * Benchmark for checking the encoding detection mechanism (getEncoding) for UTF-8.
     */
    @Benchmark
    public void getEncodingUtf8(Blackhole bh) throws IOException {
        try (InputStream is = new ByteArrayInputStream(utf8Data);
             UnicodeReader reader = new UnicodeReader(is)) {
            
            // Call read() to ensure init() is executed, as init() is protected.
            reader.read(charBuffer, 0, charBuffer.length); 
            
            String encoding = reader.getEncoding();
            bh.consume(encoding);
        }
    }

    /**
     * Benchmark for checking the encoding detection mechanism (getEncoding) for UTF-16BE.
     */
    @Benchmark
    public void getEncodingUtf16Be(Blackhole bh) throws IOException {
        try (InputStream is = new ByteArrayInputStream(utf16BeData);
             UnicodeReader reader = new UnicodeReader(is)) {
            
            // Call read() to ensure init() is executed.
            reader.read(charBuffer, 0, charBuffer.length);
            
            String encoding = reader.getEncoding();
            bh.consume(encoding);
        }
    }

    /**
     * Benchmark for checking the encoding detection mechanism (getEncoding) for UTF-16LE.
     */
    @Benchmark
    public void getEncodingUtf16Le(Blackhole bh) throws IOException {
        try (InputStream is = new ByteArrayInputStream(utf16LeData);
             UnicodeReader reader = new UnicodeReader(is)) {
            
            // Call read() to ensure init() is executed.
            reader.read(charBuffer, 0, charBuffer.length);
            
            String encoding = reader.getEncoding();
            bh.consume(encoding);
        }
    }
}
