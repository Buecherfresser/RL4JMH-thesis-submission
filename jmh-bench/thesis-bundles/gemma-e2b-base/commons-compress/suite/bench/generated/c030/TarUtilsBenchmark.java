package bench.generated.c030;

import org.apache.commons.compress.archivers.tar.TarUtils;
import org.apache.commons.compress.archivers.tar.TarArchiveStructSparse;
import org.apache.commons.compress.archivers.zip.ZipEncoding;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class TarUtilsBenchmark {

    // --- Setup Data ---

    // Data for Checksum operations
    private byte[] headerBuffer;
    private byte[] validHeader;

    // Data for Long formatting operations
    private long positiveLong;
    private long negativeLong;
    private byte[] longFormatBuffer;

    // Data for Name formatting operations
    private String testName;
    private byte[] nameInputBuffer;

    // Data for Parsing operations
    private byte[] octalParseBuffer;
    private byte[] binaryParseBuffer;
    private byte[] sparseParseBuffer;

    @Setup
    public void setup() throws IOException {
        // 1. Checksum Setup
        headerBuffer = new byte[100];
        Arrays.fill(headerBuffer, (byte) 0xAA);
        validHeader = Arrays.copyOf(headerBuffer, headerBuffer.length);

        // 2. Long Formatting Setup
        positiveLong = 123456789L;
        negativeLong = -987654321L;
        longFormatBuffer = new byte[64];

        // 3. Name Formatting Setup
        testName = "TestFileNameWithSpaces_123";
        nameInputBuffer = testName.getBytes(StandardCharsets.US_ASCII);

        // 4. Parsing Setup
        // Octal: 10 (decimal) -> "12" (octal) + trailer
        octalParseBuffer = new byte[]{ (byte) '1', (byte) '2', (byte) ' ', (byte) 0 };
        // Binary: 10 (decimal) -> 0x0A
        binaryParseBuffer = new byte[]{ (byte) 0x0A, (byte) 0x00, (byte) 0x00, (byte) 0x00 };
        // Sparse: Mock sparse data (e.g., 2 entries)
        sparseParseBuffer = new byte[]{
            (byte) 0x01, (byte) 0x00, // Sparse Offset 1
            (byte) 0x00, (byte) 0x04, // Sparse NumBytes 4
            (byte) 0x02, (byte) 0x00, // Sparse Offset 2
            (byte) 0x00, (byte) 0x08  // Sparse NumBytes 8
        };
    }

    // --- Checksum Benchmarks ---

    @Benchmark
    public void computeCheckSum(Blackhole bh) {
        long result = TarUtils.computeCheckSum(validHeader);
        bh.consume(result);
    }

    @Benchmark
    public void verifyCheckSum(Blackhole bh) {
        boolean result = TarUtils.verifyCheckSum(validHeader);
        bh.consume(result);
    }

    // --- Long Formatting Benchmarks ---

    @Benchmark
    public void formatLongOctalBytes(Blackhole bh) {
        int result = TarUtils.formatLongOctalBytes(positiveLong, longFormatBuffer, 0, longFormatBuffer.length);
        bh.consume(result);
    }

    @Benchmark
    public void formatLongOctalOrBinaryBytes(Blackhole bh) {
        int result = TarUtils.formatLongOctalOrBinaryBytes(negativeLong, longFormatBuffer, 0, longFormatBuffer.length);
        bh.consume(result);
    }

    @Benchmark
    public void formatOctalBytes(Blackhole bh) {
        int result = TarUtils.formatOctalBytes(positiveLong, longFormatBuffer, 0, longFormatBuffer.length);
        bh.consume(result);
    }

    // --- Name Formatting Benchmarks ---

    @Benchmark
    public void formatNameBytes(Blackhole bh) throws IOException {
        // Test using default encoding (US_ASCII)
        int result = TarUtils.formatNameBytes(testName, nameInputBuffer, 0, nameInputBuffer.length);
        bh.consume(result);
    }

    // --- Parsing Benchmarks ---

    @Benchmark
    public void parseOctalOrBinary(Blackhole bh) {
        long result = TarUtils.parseOctalOrBinary(octalParseBuffer, 0, octalParseBuffer.length);
        bh.consume(result);
    }

    @Benchmark
    public void parseName(Blackhole bh) throws IOException {
        // Test parsing using default encoding
        String result = TarUtils.parseName(nameInputBuffer, 0, nameInputBuffer.length);
        bh.consume(result);
    }

    @Benchmark
    public void parseSparse(Blackhole bh) throws IOException {
        TarArchiveStructSparse result = TarUtils.parseSparse(sparseParseBuffer, 0);
        bh.consume(result);
    }
}
