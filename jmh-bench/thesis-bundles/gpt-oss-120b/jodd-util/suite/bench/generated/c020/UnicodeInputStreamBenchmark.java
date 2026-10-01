package bench.generated.c020;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import jodd.io.UnicodeInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class UnicodeInputStreamBenchmark {

    private byte[] utf8BomPayload;
    private byte[] utf16leBomPayload;
    private byte[] utf32beBomPayload;
    private byte[] noBomPayload;

    @Setup(Level.Trial)
    public void setup() {
        byte[] payloadUtf8 = "Hello".getBytes(StandardCharsets.UTF_8);
        utf8BomPayload = new byte[UnicodeInputStream.BOM_UTF8.length + payloadUtf8.length];
        System.arraycopy(UnicodeInputStream.BOM_UTF8, 0, utf8BomPayload, 0, UnicodeInputStream.BOM_UTF8.length);
        System.arraycopy(payloadUtf8, 0, utf8BomPayload, UnicodeInputStream.BOM_UTF8.length, payloadUtf8.length);

        byte[] payloadUtf16le = "Hello".getBytes(StandardCharsets.UTF_16LE);
        utf16leBomPayload = new byte[UnicodeInputStream.BOM_UTF16_LE.length + payloadUtf16le.length];
        System.arraycopy(UnicodeInputStream.BOM_UTF16_LE, 0, utf16leBomPayload, 0, UnicodeInputStream.BOM_UTF16_LE.length);
        System.arraycopy(payloadUtf16le, 0, utf16leBomPayload, UnicodeInputStream.BOM_UTF16_LE.length, payloadUtf16le.length);

        byte[] payloadForUtf32 = payloadUtf8; // payload content irrelevant for BOM detection
        utf32beBomPayload = new byte[UnicodeInputStream.BOM_UTF32_BE.length + payloadForUtf32.length];
        System.arraycopy(UnicodeInputStream.BOM_UTF32_BE, 0, utf32beBomPayload, 0, UnicodeInputStream.BOM_UTF32_BE.length);
        System.arraycopy(payloadForUtf32, 0, utf32beBomPayload, UnicodeInputStream.BOM_UTF32_BE.length, payloadForUtf32.length);

        noBomPayload = payloadUtf8;
    }

    @Benchmark
    public Charset detectUTF8Encoding() throws IOException {
        UnicodeInputStream uis = new UnicodeInputStream(new ByteArrayInputStream(utf8BomPayload), null);
        return uis.getDetectedEncoding();
    }

    @Benchmark
    public Charset detectUTF16LEEncoding() throws IOException {
        UnicodeInputStream uis = new UnicodeInputStream(new ByteArrayInputStream(utf16leBomPayload), null);
        return uis.getDetectedEncoding();
    }

    @Benchmark
    public Charset detectNoBOM() throws IOException {
        UnicodeInputStream uis = new UnicodeInputStream(new ByteArrayInputStream(noBomPayload), null);
        return uis.getDetectedEncoding();
    }

    @Benchmark
    public int getBOMSizeUTF32BE() throws IOException {
        UnicodeInputStream uis = new UnicodeInputStream(new ByteArrayInputStream(utf32beBomPayload), null);
        uis.getDetectedEncoding(); // trigger init
        return uis.getBOMSize();
    }

    @Benchmark
    public int readByteAfterDetectUTF8() throws IOException {
        UnicodeInputStream uis = new UnicodeInputStream(new ByteArrayInputStream(utf8BomPayload), null);
        return uis.read();
    }

    @Benchmark
    public int readModeSkipBOM_UTF8(Blackhole bh) throws IOException {
        UnicodeInputStream uis = new UnicodeInputStream(new ByteArrayInputStream(utf8BomPayload), StandardCharsets.UTF_8);
        int b = uis.read();
        bh.consume(b);
        return b;
    }

    @Benchmark
    public int readModeNoBOM(Blackhole bh) throws IOException {
        UnicodeInputStream uis = new UnicodeInputStream(new ByteArrayInputStream(noBomPayload), StandardCharsets.UTF_8);
        int b = uis.read();
        bh.consume(b);
        return b;
    }
}
