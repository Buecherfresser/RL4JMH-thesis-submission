package bench.generated.c020;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;
import jodd.io.UnicodeInputStream;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class UnicodeInputStreamBenchmark {

    private ByteArrayInputStream inputStream;
    private UnicodeInputStream unicodeInputStream;
    private Charset targetEncoding;

    // --- Input Data Definitions ---

    // 1. UTF-8 BOM + Payload
    private static final byte[] UTF8_BOM = { (byte) 0xEF, (byte) 0xBB, (byte) 0xBF };
    private byte[] PAYLOAD = new byte[1024]; // 1KB payload
    private byte[] inputWithUtf8Bom;

    // 2. No BOM + Payload
    private byte[] inputNoBom;

    // 3. UTF-16LE BOM + Payload
    private static final byte[] UTF16LE_BOM = { (byte) 0xFF, (byte) 0xFE };
    private byte[] inputWithUtf16leBom;

    // 4. Random data (to ensure no false positives)
    private byte[] inputRandom;

    @Setup(Level.Trial)
    public void setupTrial() {
        // Initialize large, fixed inputs once per trial
        java.util.Arrays.fill(PAYLOAD, (byte) 0x42); // 'B'
        inputWithUtf8Bom = new byte[UTF8_BOM.length + PAYLOAD.length];
        System.arraycopy(UTF8_BOM, 0, inputWithUtf8Bom, 0, UTF8_BOM.length);
        System.arraycopy(PAYLOAD, 0, inputWithUtf8Bom, UTF8_BOM.length, PAYLOAD.length);

        inputNoBom = PAYLOAD;

        inputWithUtf16leBom = new byte[UTF16LE_BOM.length + PAYLOAD.length];
        System.arraycopy(UTF16LE_BOM, 0, inputWithUtf16leBom, 0, UTF16LE_BOM.length);
        System.arraycopy(PAYLOAD, 0, inputWithUtf16leBom, UTF16LE_BOM.length, PAYLOAD.length);

        inputRandom = new byte[1024];
        java.util.Random rand = new java.util.Random();
        rand.nextBytes(inputRandom);
    }

    // Helper to create a fresh stream instance for each invocation
    private UnicodeInputStream createStream(byte[] data, Charset targetEncoding) throws IOException {
        inputStream = new ByteArrayInputStream(data);
        unicodeInputStream = new UnicodeInputStream(inputStream, targetEncoding);
        return unicodeInputStream;
    }

    // --- Detect Mode Benchmarks (targetEncoding = null) ---

    @Benchmark
    public void getDetectedEncoding_Utf8Bom(Blackhole bh) throws IOException {
        // Setup fresh stream for each invocation
        unicodeInputStream = createStream(inputWithUtf8Bom, null);
        
        // Action: Check detection
        Charset detected = unicodeInputStream.getDetectedEncoding();
        
        // Consume result
        bh.consume(detected);
    }

    @Benchmark
    public void getDetectedEncoding_NoBom(Blackhole bh) throws IOException {
        // Setup fresh stream for each invocation
        unicodeInputStream = createStream(inputNoBom, null);
        
        // Action: Check detection
        Charset detected = unicodeInputStream.getDetectedEncoding();
        
        // Consume result
        bh.consume(detected);
    }

    @Benchmark
    public void getDetectedEncoding_Utf16leBom(Blackhole bh) throws IOException {
        // Setup fresh stream for each invocation
        unicodeInputStream = createStream(inputWithUtf16leBom, null);
        
        // Action: Check detection
        Charset detected = unicodeInputStream.getDetectedEncoding();
        
        // Consume result
        bh.consume(detected);
    }

    @Benchmark
    public void getDetectedEncoding_RandomData(Blackhole bh) throws IOException {
        // Setup fresh stream for each invocation
        unicodeInputStream = createStream(inputRandom, null);
        
        // Action: Check detection
        Charset detected = unicodeInputStream.getDetectedEncoding();
        
        // Consume result
        bh.consume(detected);
    }

    // --- Read Mode Benchmarks (targetEncoding specified) ---

    @Benchmark
    public void read_Utf8Mode_BomPresent(Blackhole bh) throws IOException {
        // Setup fresh stream for each invocation
        targetEncoding = Charset.forName("UTF-8");
        unicodeInputStream = createStream(inputWithUtf8Bom, targetEncoding);
        
        // Action: Read a byte (should skip BOM)
        int b = unicodeInputStream.read();
        
        // Consume result
        bh.consume(b);
    }

    @Benchmark
    public void read_Utf8Mode_NoBom(Blackhole bh) throws IOException {
        // Setup fresh stream for each invocation
        targetEncoding = Charset.forName("UTF-8");
        unicodeInputStream = createStream(inputNoBom, targetEncoding);
        
        // Action: Read a byte (should read first payload byte)
        int b = unicodeInputStream.read();
        
        // Consume result
        bh.consume(b);
    }

    @Benchmark
    public void read_Utf16leMode_BomPresent(Blackhole bh) throws IOException {
        // Setup fresh stream for each invocation
        targetEncoding = Charset.forName("UTF-16LE");
        unicodeInputStream = createStream(inputWithUtf16leBom, targetEncoding);
        
        // Action: Read a byte (should skip BOM)
        int b = unicodeInputStream.read();
        
        // Consume result
        bh.consume(b);
    }

    @Benchmark
    public void read_Utf16leMode_NoBom(Blackhole bh) throws IOException {
        // Setup fresh stream for each invocation
        targetEncoding = Charset.forName("UTF-16LE");
        unicodeInputStream = createStream(inputNoBom, targetEncoding);
        
        // Action: Read a byte (should read first payload byte)
        int b = unicodeInputStream.read();
        
        // Consume result
        bh.consume(b);
    }
    
    // --- Utility Benchmarks ---

    @Benchmark
    public void getBOMSize_Utf8Bom(Blackhole bh) throws IOException {
        // Setup fresh stream for each invocation
        unicodeInputStream = createStream(inputWithUtf8Bom, null);
        
        // Action: Check BOM size
        int size = unicodeInputStream.getBOMSize();
        
        // Consume result
        bh.consume(size);
    }

    @Benchmark
    public void getBOMSize_NoBom(Blackhole bh) throws IOException {
        // Setup fresh stream for each invocation
        unicodeInputStream = createStream(inputNoBom, null);
        
        // Action: Check BOM size
        int size = unicodeInputStream.getBOMSize();
        
        // Consume result
        bh.consume(size);
    }
}
