package bench.generated.c020;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import jodd.io.UnicodeInputStream;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class UnicodeInputStreamBenchmark {

    // --- Setup Fixtures ---

    // Input data without BOM (for Detect Mode testing)
    private final byte[] dataNoBom = "Hello World".getBytes(StandardCharsets.UTF_8);
    private final InputStream inputStreamNoBom = new ByteArrayInputStream(dataNoBom);

    // Input data with UTF-8 BOM
    private final byte[] dataWithUtf8Bom = new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF, 'A', 'B'};
    private final InputStream inputStreamWithUtf8Bom = new ByteArrayInputStream(dataWithUtf8Bom);

    // Input data with UTF-16LE BOM
    private final byte[] dataWithUtf16LeBom = new byte[]{(byte) 0xFF, (byte) 0xFE, 'A', 'B'};
    private final InputStream inputStreamWithUtf16LeBom = new ByteArrayInputStream(dataWithUtf16LeBom);

    // Target Charsets
    private final Charset utf8Charset = StandardCharsets.UTF_8;
    private final Charset utf16LeCharset = StandardCharsets.UTF_16LE;

    // UnicodeInputStream instances
    private UnicodeInputStream detectModeStream;
    private UnicodeInputStream readModeUtf8Stream;
    private UnicodeInputStream readModeUtf16LeStream;

    @Setup
    public void setup() throws IOException {
        // 1. Setup for Detect Mode (targetEncoding = null)
        detectModeStream = new UnicodeInputStream(inputStreamNoBom, null);

        // 2. Setup for Read Mode (UTF-8)
        readModeUtf8Stream = new UnicodeInputStream(inputStreamWithUtf8Bom, utf8Charset);

        // 3. Setup for Read Mode (UTF-16LE)
        readModeUtf16LeStream = new UnicodeInputStream(inputStreamWithUtf16LeBom, utf16LeCharset);
    }

    // --- Benchmarks ---

    /**
     * Benchmarks the initialization process in Detect Mode (targetEncoding is null).
     * This tests the BOM detection logic within init().
     */
    @Benchmark
    public void benchmarkInitDetectMode(Blackhole bh) throws IOException {
        // Calling getDetectedEncoding() forces init() if not initialized.
        bh.consume(detectModeStream.getDetectedEncoding());
    }

    /**
     * Benchmarks the initialization process in Read Mode (UTF-8).
     * This tests the BOM skipping logic within init() for a known encoding.
     */
    @Benchmark
    public void benchmarkInitReadModeUtf8(Blackhole bh) throws IOException {
        // Calling getDetectedEncoding() forces init() if not initialized.
        bh.consume(readModeUtf8Stream.getDetectedEncoding());
    }

    /**
     * Benchmarks the initialization process in Read Mode (UTF-16LE).
     * This tests the BOM skipping logic within init() for a known encoding.
     */
    @Benchmark
    public void benchmarkInitReadModeUtf16Le(Blackhole bh) throws IOException {
        // Calling getDetectedEncoding() forces init() if not initialized.
        bh.consume(readModeUtf16LeStream.getDetectedEncoding());
    }

    /**
     * Benchmarks the core read operation in Read Mode (UTF-8).
     * This tests the internal PushbackInputStream reading after initialization.
     */
    @Benchmark
    public void benchmarkReadUtf8(Blackhole bh) throws IOException {
        // Since read() calls init(), this tests the full cycle.
        int result = readModeUtf8Stream.read();
        bh.consume(result);
    }

    /**
     * Benchmarks the core read operation in Read Mode (UTF-16LE).
     * This tests the internal PushbackInputStream reading after initialization.
     */
    @Benchmark
    public void benchmarkReadUtf16Le(Blackhole bh) throws IOException {
        // Since read() calls init(), this tests the full cycle.
        int result = readModeUtf16LeStream.read();
        bh.consume(result);
    }
}
