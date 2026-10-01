package bench.generated.c020;

import org.openjdk.jmh.annotations.*;
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

    private byte[] utf8BomPayload;
    private byte[] utf16LeBomPayload;
    private byte[] utf16BeBomPayload;
    private byte[] utf32LeBomPayload;
    private byte[] utf32BeBomPayload;
    private byte[] noBomPayload;

    private Charset utf8 = StandardCharsets.UTF_8;
    private Charset utf16le = StandardCharsets.UTF_16LE;
    private Charset utf16be = StandardCharsets.UTF_16BE;
    private Charset utf32be = Charset.forName("UTF-32BE");
    private Charset utf32le = Charset.forName("UTF-32LE");
    private Charset iso8859 = Charset.forName("ISO-8859-1");

    @Setup(Level.Trial)
    public void setup() {
        String text = "The quick brown fox jumps over the lazy dog. 1234567890";
        utf8BomPayload = concat(UnicodeInputStream.BOM_UTF8, text.getBytes(utf8));
        utf16LeBomPayload = concat(UnicodeInputStream.BOM_UTF16_LE, text.getBytes(utf16le));
        utf16BeBomPayload = concat(UnicodeInputStream.BOM_UTF16_BE, text.getBytes(utf16be));
        utf32LeBomPayload = concat(UnicodeInputStream.BOM_UTF32_LE, text.getBytes(utf32le));
        utf32BeBomPayload = concat(UnicodeInputStream.BOM_UTF32_BE, text.getBytes(utf32be));
        noBomPayload = text.getBytes(utf8);
    }

    private static byte[] concat(byte[] a, byte[] b) {
        byte[] result = new byte[a.length + b.length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }

    private UnicodeInputStream createStream(byte[] payload, Charset targetEncoding) {
        return new UnicodeInputStream(new ByteArrayInputStream(payload), targetEncoding);
    }

    private byte[] readAll(InputStream in) throws IOException {
        byte[] buffer = new byte[1024];
        int total = 0;
        int n;
        while ((n = in.read(buffer, total, buffer.length - total)) != -1) {
            total += n;
            if (total == buffer.length) {
                byte[] newBuffer = new byte[buffer.length * 2];
                System.arraycopy(buffer, 0, newBuffer, 0, buffer.length);
                buffer = newBuffer;
            }
        }
        byte[] result = new byte[total];
        System.arraycopy(buffer, 0, result, 0, total);
        return result;
    }

    // ---------- Detect mode (targetEncoding = null) ----------

    @Benchmark
    public byte[] detectUtf8BomAndReadAll() throws IOException {
        UnicodeInputStream stream = createStream(utf8BomPayload, null);
        return readAll(stream);
    }

    @Benchmark
    public byte[] detectUtf16LeBomAndReadAll() throws IOException {
        UnicodeInputStream stream = createStream(utf16LeBomPayload, null);
        return readAll(stream);
    }

    @Benchmark
    public byte[] detectUtf16BeBomAndReadAll() throws IOException {
        UnicodeInputStream stream = createStream(utf16BeBomPayload, null);
        return readAll(stream);
    }

    @Benchmark
    public byte[] detectUtf32LeBomAndReadAll() throws IOException {
        UnicodeInputStream stream = createStream(utf32LeBomPayload, null);
        return readAll(stream);
    }

    @Benchmark
    public byte[] detectUtf32BeBomAndReadAll() throws IOException {
        UnicodeInputStream stream = createStream(utf32BeBomPayload, null);
        return readAll(stream);
    }

    @Benchmark
    public byte[] detectNoBomAndReadAll() throws IOException {
        UnicodeInputStream stream = createStream(noBomPayload, null);
        return readAll(stream);
    }

    // ---------- Read mode (targetEncoding set) ----------

    @Benchmark
    public byte[] readModeUtf8SkipBomAndReadAll() throws IOException {
        UnicodeInputStream stream = createStream(utf8BomPayload, utf8);
        return readAll(stream);
    }

    @Benchmark
    public byte[] readModeUtf8NoBomAndReadAll() throws IOException {
        UnicodeInputStream stream = createStream(noBomPayload, utf8);
        return readAll(stream);
    }

    @Benchmark
    public byte[] readModeUtf16LeSkipBomAndReadAll() throws IOException {
        UnicodeInputStream stream = createStream(utf16LeBomPayload, utf16le);
        return readAll(stream);
    }

    @Benchmark
    public byte[] readModeUtf16BeSkipBomAndReadAll() throws IOException {
        UnicodeInputStream stream = createStream(utf16BeBomPayload, utf16be);
        return readAll(stream);
    }

    @Benchmark
    public byte[] readModeUtf32LeSkipBomAndReadAll() throws IOException {
        UnicodeInputStream stream = createStream(utf32LeBomPayload, utf32le);
        return readAll(stream);
    }

    @Benchmark
    public byte[] readModeUtf32BeSkipBomAndReadAll() throws IOException {
        UnicodeInputStream stream = createStream(utf32BeBomPayload, utf32be);
        return readAll(stream);
    }

    @Benchmark
    public byte[] readModeNonUtfNoBomSkip() throws IOException {
        UnicodeInputStream stream = createStream(noBomPayload, iso8859);
        return readAll(stream);
    }

    // ---------- Encoding detection and BOM size ----------

    @Benchmark
    public Charset getDetectedEncodingUtf8() {
        UnicodeInputStream stream = createStream(utf8BomPayload, null);
        return stream.getDetectedEncoding();
    }

    @Benchmark
    public Charset getDetectedEncodingNoBom() {
        UnicodeInputStream stream = createStream(noBomPayload, null);
        return stream.getDetectedEncoding();
    }

    @Benchmark
    public int getBomSizeUtf8() {
        UnicodeInputStream stream = createStream(utf8BomPayload, null);
        stream.getDetectedEncoding(); // force init
        return stream.getBOMSize();
    }

    @Benchmark
    public int getBomSizeNoBom() {
        UnicodeInputStream stream = createStream(noBomPayload, null);
        stream.getDetectedEncoding(); // force init
        return stream.getBOMSize();
    }
}
