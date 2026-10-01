package bench.generated.c036;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.reader.UnicodeReader;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class UnicodeReaderBenchmark {

    private ByteArrayInputStream utf8BomStream;
    private ByteArrayInputStream utf16BeBomStream;
    private ByteArrayInputStream utf16LeBomStream;
    private ByteArrayInputStream noBomStream;

    private static final int READ_BUFFER_SIZE = 1024;

    @Setup(Level.Trial)
    public void setup() throws IOException {
        // Data payload (e.g., "Hello World!")
        String data = "This is a test payload for UnicodeReader.";
        byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);

        // 1. UTF-8 BOM: EF BB BF
        byte[] utf8Bom = new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};
        byte[] utf8BomData = new byte[utf8Bom.length + dataBytes.length];
        System.arraycopy(utf8Bom, 0, utf8BomData, 0, utf8Bom.length);
        System.arraycopy(dataBytes, 0, utf8BomData, utf8Bom.length, dataBytes.length);
        utf8BomStream = new ByteArrayInputStream(utf8BomData);

        // 2. UTF-16BE BOM: FE FF
        byte[] utf16BeBom = new byte[]{(byte) 0xFE, (byte) 0xFF};
        // Convert data to UTF-16BE bytes
        byte[] dataUtf16Be = data.getBytes(StandardCharsets.UTF_16BE);
        byte[] utf16BeBomData = new byte[utf16BeBom.length + dataUtf16Be.length];
        System.arraycopy(utf16BeBom, 0, utf16BeBomData, 0, utf16BeBom.length);
        System.arraycopy(dataUtf16Be, 0, utf16BeBomData, utf16BeBom.length, dataUtf16Be.length);
        utf16BeBomStream = new ByteArrayInputStream(utf16BeBomData);

        // 3. UTF-16LE BOM: FF FE
        byte[] utf16LeBom = new byte[]{(byte) 0xFF, (byte) 0xFE};
        // Convert data to UTF-16LE bytes
        byte[] dataUtf16Le = data.getBytes(StandardCharsets.UTF_16LE);
        byte[] utf16LeBomData = new byte[utf16LeBom.length + dataUtf16Le.length];
        System.arraycopy(utf16LeBom, 0, utf16LeBomData, 0, utf16LeBom.length);
        System.arraycopy(dataUtf16Le, 0, utf16LeBomData, utf16LeBom.length, dataUtf16Le.length);
        utf16LeBomStream = new ByteArrayInputStream(utf16LeBomData);

        // 4. No BOM (Standard UTF-8 data)
        noBomStream = new ByteArrayInputStream(dataBytes);
    }

    /**
     * Benchmarks reading characters from a stream containing a UTF-8 BOM.
     * This tests the BOM detection logic and subsequent reading.
     */
    @Benchmark
    public int benchmarkReadWithUtf8Bom(Blackhole bh) throws IOException {
        // Must create a new reader instance for each invocation to ensure BOM detection runs
        try (UnicodeReader reader = new UnicodeReader(utf8BomStream)) {
            char[] buffer = new char[READ_BUFFER_SIZE];
            int count = reader.read(buffer, 0, READ_BUFFER_SIZE);
            bh.consume(count);
            return count;
        }
    }

    /**
     * Benchmarks reading characters from a stream containing a UTF-16BE BOM.
     */
    @Benchmark
    public int benchmarkReadWithUtf16BeBom(Blackhole bh) throws IOException {
        try (UnicodeReader reader = new UnicodeReader(utf16BeBomStream)) {
            char[] buffer = new char[READ_BUFFER_SIZE];
            int count = reader.read(buffer, 0, READ_BUFFER_SIZE);
            bh.consume(count);
            return count;
        }
    }

    /**
     * Benchmarks reading characters from a stream containing a UTF-16LE BOM.
     */
    @Benchmark
    public int benchmarkReadWithUtf16LeBom(Blackhole bh) throws IOException {
        try (UnicodeReader reader = new UnicodeReader(utf16LeBomStream)) {
            char[] buffer = new char[READ_BUFFER_SIZE];
            int count = reader.read(buffer, 0, READ_BUFFER_SIZE);
            bh.consume(count);
            return count;
        }
    }

    /**
     * Benchmarks reading characters from a stream without a BOM (defaults to UTF-8).
     */
    @Benchmark
    public int benchmarkReadWithoutBom(Blackhole bh) throws IOException {
        try (UnicodeReader reader = new UnicodeReader(noBomStream)) {
            char[] buffer = new char[READ_BUFFER_SIZE];
            int count = reader.read(buffer, 0, READ_BUFFER_SIZE);
            bh.consume(count);
            return count;
        }
    }

    /**
     * Benchmarks the close operation, which forces initialization if not already done.
     */
    @Benchmark
    public void benchmarkClose(Blackhole bh) throws IOException {
        // Use a fresh reader instance to ensure close() triggers init()
        try (UnicodeReader reader = new UnicodeReader(noBomStream)) {
            reader.close();
            bh.consume(reader.getEncoding());
        }
    }
}
