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

    // --- Setup State ---
    private InputStream inputUtf8Bom;
    private InputStream inputUtf16LeBom;
    private InputStream inputUtf32BeBom;
    private InputStream inputNoBom;

    private Charset utf8Charset = StandardCharsets.UTF_8;
    private Charset utf16LeCharset = StandardCharsets.UTF_16LE;
    private Charset utf32BeCharset = Charset.forName("UTF-32BE");

    private UnicodeInputStream streamUtf8ReadMode;
    private UnicodeInputStream streamUtf16LeReadMode;
    private UnicodeInputStream streamUtf32BeReadMode;
    private UnicodeInputStream streamDetectMode;

    private static final int DATA_SIZE = 1024 * 1024; // 1MB of data

    @Setup
    public void setup() throws IOException {
        // 1. Create large byte array for input data
        byte[] data = new byte[DATA_SIZE];
        for (int i = 0; i < DATA_SIZE; i++) {
            data[i] = (byte) (i % 256);
        }

        // 2. Create BOM inputs by combining BOM and Data into a single array
        
        // UTF-8 BOM: EF BB BF
        byte[] utf8Bom = UnicodeInputStream.BOM_UTF8;
        byte[] utf8Combined = new byte[utf8Bom.length + DATA_SIZE];
        System.arraycopy(utf8Bom, 0, utf8Combined, 0, utf8Bom.length);
        System.arraycopy(data, 0, utf8Combined, utf8Bom.length, DATA_SIZE);
        this.inputUtf8Bom = new ByteArrayInputStream(utf8Combined);

        // UTF-16LE BOM: FF FE
        byte[] utf16LeBom = UnicodeInputStream.BOM_UTF16_LE;
        byte[] utf16LeCombined = new byte[utf16LeBom.length + DATA_SIZE];
        System.arraycopy(utf16LeBom, 0, utf16LeCombined, 0, utf16LeBom.length);
        System.arraycopy(data, 0, utf16LeCombined, utf16LeBom.length, DATA_SIZE);
        this.inputUtf16LeBom = new ByteArrayInputStream(utf16LeCombined);

        // UTF-32BE BOM: 00 00 FE FF
        byte[] utf32BeBom = UnicodeInputStream.BOM_UTF32_BE;
        byte[] utf32BeCombined = new byte[utf32BeBom.length + DATA_SIZE];
        System.arraycopy(utf32BeBom, 0, utf32BeCombined, 0, utf32BeBom.length);
        System.arraycopy(data, 0, utf32BeCombined, utf32BeBom.length, DATA_SIZE);
        this.inputUtf32BeBom = new ByteArrayInputStream(utf32BeCombined);

        // Input without BOM (for Read Mode testing)
        this.inputNoBom = new ByteArrayInputStream(data);


        // 3. Initialize streams for Read Mode benchmarks
        streamUtf8ReadMode = new UnicodeInputStream(inputUtf8Bom, utf8Charset);
        streamUtf16LeReadMode = new UnicodeInputStream(inputUtf16LeBom, utf16LeCharset);
        streamUtf32BeReadMode = new UnicodeInputStream(inputUtf32BeBom, utf32BeCharset);

        // 4. Initialize stream for Detect Mode benchmark (targetEncoding = null)
        streamDetectMode = new UnicodeInputStream(inputUtf8Bom, null);
    }

    // --- Benchmarks for Read Mode (Target Encoding Specified) ---

    @Benchmark
    public void readUtf8ReadMode(Blackhole bh) throws IOException {
        int result = streamUtf8ReadMode.read();
        bh.consume(result);
    }

    @Benchmark
    public void readUtf16LeReadMode(Blackhole bh) throws IOException {
        int result = streamUtf16LeReadMode.read();
        bh.consume(result);
    }

    @Benchmark
    public void readUtf32BeReadMode(Blackhole bh) throws IOException {
        int result = streamUtf32BeReadMode.read();
        bh.consume(result);
    }

    // --- Benchmarks for Detect Mode (Target Encoding Null) ---

    @Benchmark
    public void getDetectedEncodingDetectMode(Blackhole bh) {
        Charset detected = streamDetectMode.getDetectedEncoding();
        bh.consume(detected);
    }

    // --- Benchmarks for Utility Methods ---

    @Benchmark
    public void getBomSize(Blackhole bh) {
        int bomSize = streamUtf8ReadMode.getBOMSize();
        bh.consume(bomSize);
    }
}
