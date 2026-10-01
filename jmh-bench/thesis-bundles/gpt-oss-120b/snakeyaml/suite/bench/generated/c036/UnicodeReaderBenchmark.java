package bench.generated.c036;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.reader.UnicodeReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@State(org.openjdk.jmh.annotations.Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class UnicodeReaderBenchmark {

    private byte[] utf8NoBom;
    private byte[] utf8Bom;
    private byte[] utf16LEBom;
    private byte[] utf16BEBom;
    private char[] buffer;

    @Setup(org.openjdk.jmh.annotations.Level.Trial)
    public void setUp() {
        String text = "Hello, Unicode! Привет мир! こんにちは世界";
        // UTF-8 without BOM
        utf8NoBom = text.getBytes(StandardCharsets.UTF_8);
        // UTF-8 with BOM
        byte[] utf8Bytes = text.getBytes(StandardCharsets.UTF_8);
        utf8Bom = new byte[3 + utf8Bytes.length];
        utf8Bom[0] = (byte) 0xEF;
        utf8Bom[1] = (byte) 0xBB;
        utf8Bom[2] = (byte) 0xBF;
        System.arraycopy(utf8Bytes, 0, utf8Bom, 3, utf8Bytes.length);
        // UTF-16LE with BOM
        byte[] utf16LEBytes = text.getBytes(StandardCharsets.UTF_16LE);
        utf16LEBom = new byte[2 + utf16LEBytes.length];
        utf16LEBom[0] = (byte) 0xFF;
        utf16LEBom[1] = (byte) 0xFE;
        System.arraycopy(utf16LEBytes, 0, utf16LEBom, 2, utf16LEBytes.length);
        // UTF-16BE with BOM
        byte[] utf16BEBytes = text.getBytes(StandardCharsets.UTF_16BE);
        utf16BEBom = new byte[2 + utf16BEBytes.length];
        utf16BEBom[0] = (byte) 0xFE;
        utf16BEBom[1] = (byte) 0xFF;
        System.arraycopy(utf16BEBytes, 0, utf16BEBom, 2, utf16BEBytes.length);
        // Buffer for reading
        buffer = new char[256];
    }

    @Benchmark
    public int readUtf8NoBom() throws IOException {
        UnicodeReader reader = new UnicodeReader(new ByteArrayInputStream(utf8NoBom));
        int read = reader.read(buffer, 0, buffer.length);
        reader.close();
        return read;
    }

    @Benchmark
    public int readUtf8Bom() throws IOException {
        UnicodeReader reader = new UnicodeReader(new ByteArrayInputStream(utf8Bom));
        int read = reader.read(buffer, 0, buffer.length);
        reader.close();
        return read;
    }

    @Benchmark
    public int readUtf16LEBom() throws IOException {
        UnicodeReader reader = new UnicodeReader(new ByteArrayInputStream(utf16LEBom));
        int read = reader.read(buffer, 0, buffer.length);
        reader.close();
        return read;
    }

    @Benchmark
    public int readUtf16BEBom() throws IOException {
        UnicodeReader reader = new UnicodeReader(new ByteArrayInputStream(utf16BEBom));
        int read = reader.read(buffer, 0, buffer.length);
        reader.close();
        return read;
    }

    @Benchmark
    public void closeUtf8NoBom(Blackhole bh) throws IOException {
        UnicodeReader reader = new UnicodeReader(new ByteArrayInputStream(utf8NoBom));
        reader.close();
        bh.consume(reader);
    }

    @Benchmark
    public void closeUtf8Bom(Blackhole bh) throws IOException {
        UnicodeReader reader = new UnicodeReader(new ByteArrayInputStream(utf8Bom));
        reader.close();
        bh.consume(reader);
    }

    @Benchmark
    public void closeUtf16LEBom(Blackhole bh) throws IOException {
        UnicodeReader reader = new UnicodeReader(new ByteArrayInputStream(utf16LEBom));
        reader.close();
        bh.consume(reader);
    }

    @Benchmark
    public void closeUtf16BEBom(Blackhole bh) throws IOException {
        UnicodeReader reader = new UnicodeReader(new ByteArrayInputStream(utf16BEBom));
        reader.close();
        bh.consume(reader);
    }
}
