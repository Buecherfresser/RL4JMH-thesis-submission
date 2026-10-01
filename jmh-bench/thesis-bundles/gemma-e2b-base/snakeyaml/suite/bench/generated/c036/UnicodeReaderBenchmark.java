package bench.generated.c036;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
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

    // --- Setup State ---
    private InputStream utf8NoBomStream;
    private InputStream utf8BomStream;
    private InputStream utf16BeBomStream;
    private InputStream utf16LeBomStream;

    private UnicodeReader readerNoBom;
    private UnicodeReader readerUtf8Bom;
    private UnicodeReader readerUtf16BeBom;
    private UnicodeReader readerUtf16LeBom;

    private static final String UTF8_CONTENT = "Test data in UTF-8.";
    private static final String UTF16BE_CONTENT = "Test data in UTF-16BE.";
    private static final String UTF16LE_CONTENT = "Test data in UTF-16LE.";

    @Setup
    public void setup() throws IOException {
        // 1. UTF-8 (No BOM)
        byte[] utf8Bytes = UTF8_CONTENT.getBytes(StandardCharsets.UTF_8);
        utf8NoBomStream = new ByteArrayInputStream(utf8Bytes);
        readerNoBom = new UnicodeReader(utf8NoBomStream);

        // 2. UTF-8 (With BOM: EF BB BF)
        byte[] bomUtf8 = {(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};
        byte[] contentUtf8 = UTF8_CONTENT.getBytes(StandardCharsets.UTF_8);
        
        byte[] combinedUtf8 = new byte[bomUtf8.length + contentUtf8.length];
        System.arraycopy(bomUtf8, 0, combinedUtf8, 0, bomUtf8.length);
        System.arraycopy(contentUtf8, 0, combinedUtf8, bomUtf8.length, contentUtf8.length);
        
        utf8BomStream = new ByteArrayInputStream(combinedUtf8);
        readerUtf8Bom = new UnicodeReader(utf8BomStream);

        // 3. UTF-16BE (With BOM: FE FF)
        byte[] bomUtf16Be = {(byte) 0xFE, (byte) 0xFF};
        byte[] contentUtf16Be = UTF16BE_CONTENT.getBytes(StandardCharsets.UTF_16BE);
        
        byte[] combinedUtf16Be = new byte[bomUtf16Be.length + contentUtf16Be.length];
        System.arraycopy(bomUtf16Be, 0, combinedUtf16Be, 0, bomUtf16Be.length);
        System.arraycopy(contentUtf16Be, 0, combinedUtf16Be, bomUtf16Be.length, contentUtf16Be.length);
        
        utf16BeBomStream = new ByteArrayInputStream(combinedUtf16Be);
        readerUtf16BeBom = new UnicodeReader(utf16BeBomStream);

        // 4. UTF-16LE (With BOM: FF FE)
        byte[] bomUtf16Le = {(byte) 0xFF, (byte) 0xFE};
        byte[] contentUtf16Le = UTF16LE_CONTENT.getBytes(StandardCharsets.UTF_16LE);
        
        byte[] combinedUtf16Le = new byte[bomUtf16Le.length + contentUtf16Le.length];
        System.arraycopy(bomUtf16Le, 0, combinedUtf16Le, 0, bomUtf16Le.length);
        System.arraycopy(contentUtf16Le, 0, combinedUtf16Le, bomUtf16Le.length, contentUtf16Le.length);
        
        utf16LeBomStream = new ByteArrayInputStream(combinedUtf16Le);
        readerUtf16LeBom = new UnicodeReader(utf16LeBomStream);
    }

    @Benchmark
    public void readNoBom(Blackhole bh) throws IOException {
        // Test reading from stream without BOM (should default to UTF-8)
        char[] buffer = new char[1024];
        int result = readerNoBom.read(buffer, 0, buffer.length);
        bh.consume(result);
    }

    @Benchmark
    public void readUtf8Bom(Blackhole bh) throws IOException {
        // Test reading from stream with UTF-8 BOM
        char[] buffer = new char[1024];
        int result = readerUtf8Bom.read(buffer, 0, buffer.length);
        bh.consume(result);
    }

    @Benchmark
    public void readUtf16BeBom(Blackhole bh) throws IOException {
        // Test reading from stream with UTF-16BE BOM
        char[] buffer = new char[1024];
        int result = readerUtf16BeBom.read(buffer, 0, buffer.length);
        bh.consume(result);
    }

    @Benchmark
    public void readUtf16LeBom(Blackhole bh) throws IOException {
        // Test reading from stream with UTF-16LE BOM
        char[] buffer = new char[1024];
        int result = readerUtf16LeBom.read(buffer, 0, buffer.length);
        bh.consume(result);
    }
}
