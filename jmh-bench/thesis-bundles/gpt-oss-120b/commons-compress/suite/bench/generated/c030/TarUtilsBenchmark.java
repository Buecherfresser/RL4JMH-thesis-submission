package bench.generated.c030;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.archivers.tar.TarUtils;
import org.apache.commons.compress.archivers.tar.TarConstants;
import org.apache.commons.compress.archivers.tar.TarArchiveStructSparse;
import org.apache.commons.compress.archivers.zip.ZipEncoding;
import org.apache.commons.compress.archivers.zip.ZipEncodingHelper;
import java.nio.charset.StandardCharsets;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class TarUtilsBenchmark {

    private byte[] headerBuffer;
    private byte[] checksumFieldBuffer;
    private byte[] longOctalBuffer;
    private byte[] nameBuffer;
    private byte[] octalBuffer;
    private byte[] unsignedOctalBuffer;
    private byte[] booleanBuffer;
    private byte[] parseNameBuffer;
    private byte[] parseOctalBuffer;
    private byte[] parseOctalOrBinaryOctalBuffer;
    private byte[] parseOctalOrBinaryBinaryBuffer;
    private byte[] sparseBuffer;
    private String testName;
    private long smallValue;
    private long largeValue;
    private long checksumValue;
    private ZipEncoding fallbackEncoding;

    @Setup(Level.Trial)
    public void setUp() {
        // Header buffer (512 bytes typical tar header)
        headerBuffer = new byte[TarConstants.DEFAULT_RCDSIZE];
        for (int i = 0; i < headerBuffer.length; i++) {
            headerBuffer[i] = (byte) (i % 256);
        }

        // Compute checksum and write it into the header
        checksumValue = TarUtils.computeCheckSum(headerBuffer);
        checksumFieldBuffer = new byte[TarConstants.CHKSUMLEN + 2]; // space for NUL and space
        TarUtils.formatCheckSumOctalBytes(checksumValue, checksumFieldBuffer, 0, checksumFieldBuffer.length);
        System.arraycopy(checksumFieldBuffer, 0, headerBuffer, TarConstants.CHKSUM_OFFSET, checksumFieldBuffer.length);

        // Buffers for formatting long values
        longOctalBuffer = new byte[TarConstants.SIZELEN];

        // Buffers for name formatting
        nameBuffer = new byte[100];
        testName = "benchmark-test-file.txt";

        // Buffers for octal formatting
        octalBuffer = new byte[12];
        unsignedOctalBuffer = new byte[12];

        // Boolean buffer
        booleanBuffer = new byte[] { 1 };

        // Buffer for parseName (null‑terminated)
        parseNameBuffer = new byte[30];
        byte[] nameBytes = testName.getBytes(StandardCharsets.UTF_8);
        System.arraycopy(nameBytes, 0, parseNameBuffer, 0, Math.min(nameBytes.length, parseNameBuffer.length - 1));
        parseNameBuffer[Math.min(nameBytes.length, parseNameBuffer.length - 1)] = 0; // NUL terminator

        // Buffer for parseOctal (value 0755)
        parseOctalBuffer = new byte[12];
        String octalString = "0000000755 ";
        byte[] octalBytes = octalString.getBytes(StandardCharsets.US_ASCII);
        System.arraycopy(octalBytes, 0, parseOctalBuffer, 0, octalBytes.length);

        // Octal representation for parseOctalOrBinary (fits octal)
        parseOctalOrBinaryOctalBuffer = new byte[12];
        String octalVal = "0000000644 ";
        byte[] octalValBytes = octalVal.getBytes(StandardCharsets.US_ASCII);
        System.arraycopy(octalValBytes, 0, parseOctalOrBinaryOctalBuffer, 0, octalValBytes.length);

        // Binary representation for parseOctalOrBinary (high bit set)
        parseOctalOrBinaryBinaryBuffer = new byte[12];
        parseOctalOrBinaryBinaryBuffer[0] = (byte) 0x80;
        long binaryNumber = 12345L;
        for (int i = parseOctalOrBinaryBinaryBuffer.length - 1, shift = 0; i > 0; i--, shift += 8) {
            parseOctalOrBinaryBinaryBuffer[i] = (byte) ((binaryNumber >> shift) & 0xFF);
        }

        // Sparse buffer (offset and numbytes as octal)
        sparseBuffer = new byte[TarConstants.SPARSE_OFFSET_LEN + TarConstants.SPARSE_NUMBYTES_LEN];
        String offsetOct = "0000000010 ";
        String numbytesOct = "0000000040 ";
        byte[] offBytes = offsetOct.getBytes(StandardCharsets.US_ASCII);
        byte[] nbBytes = numbytesOct.getBytes(StandardCharsets.US_ASCII);
        System.arraycopy(offBytes, 0, sparseBuffer, 0, offBytes.length);
        System.arraycopy(nbBytes, 0, sparseBuffer, TarConstants.SPARSE_OFFSET_LEN, nbBytes.length);

        // Values for formatting tests
        smallValue = 12345L;
        largeValue = Long.MAX_VALUE;

        // Public fallback encoding (ASCII) for custom‑encoding benchmarks
        fallbackEncoding = ZipEncodingHelper.getZipEncoding(StandardCharsets.US_ASCII);
    }

    @Benchmark
    public long benchmarkComputeCheckSum() {
        return TarUtils.computeCheckSum(headerBuffer);
    }

    @Benchmark
    public int benchmarkFormatCheckSumOctetBytes() {
        return TarUtils.formatCheckSumOctalBytes(checksumValue, checksumFieldBuffer, 0, checksumFieldBuffer.length);
    }

    @Benchmark
    public int benchmarkFormatLongOctalBytes() {
        return TarUtils.formatLongOctalBytes(smallValue, longOctalBuffer, 0, longOctalBuffer.length);
    }

    @Benchmark
    public int benchmarkFormatLongOctalOrBinaryBytes_Octal() {
        return TarUtils.formatLongOctalOrBinaryBytes(smallValue, longOctalBuffer, 0, longOctalBuffer.length);
    }

    @Benchmark
    public int benchmarkFormatLongOctalOrBinaryBytes_Binary() {
        return TarUtils.formatLongOctalOrBinaryBytes(largeValue, longOctalBuffer, 0, longOctalBuffer.length);
    }

    @Benchmark
    public int benchmarkFormatNameBytes_DefaultEncoding() {
        return TarUtils.formatNameBytes(testName, nameBuffer, 0, nameBuffer.length);
    }

    @Benchmark
    public int benchmarkFormatNameBytes_CustomEncoding() {
        try {
            return TarUtils.formatNameBytes(testName, nameBuffer, 0, nameBuffer.length, fallbackEncoding);
        } catch (java.io.IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Benchmark
    public int benchmarkFormatOctalBytes() {
        return TarUtils.formatOctalBytes(smallValue, octalBuffer, 0, octalBuffer.length);
    }

    @Benchmark
    public void benchmarkFormatUnsignedOctalString(Blackhole bh) {
        TarUtils.formatUnsignedOctalString(smallValue, unsignedOctalBuffer, 0, unsignedOctalBuffer.length);
        bh.consume(unsignedOctalBuffer);
    }

    @Benchmark
    public boolean benchmarkParseBoolean() {
        return TarUtils.parseBoolean(booleanBuffer, 0);
    }

    @Benchmark
    public String benchmarkParseName_DefaultEncoding() {
        return TarUtils.parseName(parseNameBuffer, 0, parseNameBuffer.length);
    }

    @Benchmark
    public String benchmarkParseName_CustomEncoding() {
        try {
            return TarUtils.parseName(parseNameBuffer, 0, parseNameBuffer.length, fallbackEncoding);
        } catch (java.io.IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Benchmark
    public long benchmarkParseOctal() {
        return TarUtils.parseOctal(parseOctalBuffer, 0, parseOctalBuffer.length);
    }

    @Benchmark
    public long benchmarkParseOctalOrBinary_Octal() {
        return TarUtils.parseOctalOrBinary(parseOctalOrBinaryOctalBuffer, 0, parseOctalOrBinaryOctalBuffer.length);
    }

    @Benchmark
    public long benchmarkParseOctalOrBinary_Binary() {
        return TarUtils.parseOctalOrBinary(parseOctalOrBinaryBinaryBuffer, 0, parseOctalOrBinaryBinaryBuffer.length);
    }

    @Benchmark
    public TarArchiveStructSparse benchmarkParseSparse() {
        return TarUtils.parseSparse(sparseBuffer, 0);
    }

    @Benchmark
    public boolean benchmarkVerifyCheckSum() {
        return TarUtils.verifyCheckSum(headerBuffer);
    }
}
