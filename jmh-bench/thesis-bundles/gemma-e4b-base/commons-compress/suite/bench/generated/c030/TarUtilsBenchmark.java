package bench.generated.c030;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.archivers.tar.TarUtils;
import org.apache.commons.compress.archivers.zip.ZipEncoding;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class TarUtilsBenchmark {

    // --- Input Data ---

    // 1. Header buffer for checksum/verification
    private byte[] sampleHeaderBuffer;
    private byte[] checksumVerificationBuffer;

    // 2. Name inputs
    private String sampleFileName;
    private byte[] sampleNameBytes;

    // 3. Octal/Binary inputs
    private byte[] sampleOctalBuffer;
    private byte[] sampleBinaryBuffer;

    // 4. PAX Sparse Map input (String format for string parsing tests)
    private String sampleSparseMapString;
    private byte[] sampleSparseMapBytes;

    // 5. Binary input for single sparse struct parsing (binary format)
    private byte[] sparseBinaryBuffer;

    // 6. Test constants/buffers for formatting/parsing
    private byte[] outputBuffer;
    private final int BUFFER_SIZE = 1024;

    @Setup(Level.Trial)
    public void setup() {
        // 1. Header buffer setup (simulating a tar header)
        sampleHeaderBuffer = new byte[100];
        Arrays.fill(sampleHeaderBuffer, (byte) 0xAA);
        // Set a specific checksum field value (e.g., 0x010203040506)
        sampleHeaderBuffer[120] = 0x01;
        sampleHeaderBuffer[121] = 0x02;
        sampleHeaderBuffer[122] = 0x03;
        sampleHeaderBuffer[123] = 0x04;
        sampleHeaderBuffer[124] = 0x05;
        sampleHeaderBuffer[125] = 0x06;

        // Buffer specifically for verification (where checksum field is replaced by spaces)
        checksumVerificationBuffer = Arrays.copyOf(sampleHeaderBuffer, sampleHeaderBuffer.length);
        // Replace checksum field with ASCII spaces (0x20)
        for (int i = 120; i < 126; i++) {
            checksumVerificationBuffer[i] = (byte) ' ';
        }

        // 2. Name inputs setup
        sampleFileName = "path/to/my/file_with_spaces.txt";
        sampleNameBytes = sampleFileName.getBytes(StandardCharsets.UTF_8);

        // 3. Octal/Binary inputs setup
        // Sample octal value (e.g., 12345)
        sampleOctalBuffer = new byte[10];
        // Using a known good octal string representation: "012345 "
        Arrays.fill(sampleOctalBuffer, (byte) '0');
        sampleOctalBuffer[1] = (byte) '1';
        sampleOctalBuffer[2] = (byte) '2';
        sampleOctalBuffer[3] = (byte) '3';
        sampleOctalBuffer[4] = (byte) '4';
        sampleOctalBuffer[5] = (byte) '5';
        sampleOctalBuffer[6] = (byte) ' '; // Trailing space

        // Sample binary value (e.g., a large number represented in binary)
        // 0xFF indicates negative, followed by 7 bytes of data (total length 8)
        // This length triggers the short binary path in parseOctalOrBinary
        sampleBinaryBuffer = new byte[8];
        sampleBinaryBuffer[0] = (byte) 0xFF;
        // Fill remaining 7 bytes with some data
        for (int i = 1; i < 8; i++) {
            sampleBinaryBuffer[i] = (byte) (i * 0x10);
        }

        // 4. PAX Sparse Map input setup (String format)
        sampleSparseMapString = "100,50,200,100";
        sampleSparseMapBytes = sampleSparseMapString.getBytes(StandardCharsets.UTF_8);

        // 5. Binary Sparse Map input setup (for public parseSparse method)
        // Constructing a buffer for a single sparse entry (Offset=100, Numbytes=50)
        // Assuming 8 bytes for offset and 8 bytes for numbytes (standard long size)
        ByteBuffer bb = ByteBuffer.allocate(16);
        bb.putLong(100L); // Offset
        bb.putLong(50L);  // Numbytes
        sparseBinaryBuffer = Arrays.copyOf(bb.array(), bb.capacity());

        // 6. Test buffers
        outputBuffer = new byte[BUFFER_SIZE];
    }

    // --- Checksum Benchmarks ---

    @Benchmark
    public long computeCheckSumBenchmark(Blackhole bh) {
        long checksum = TarUtils.computeCheckSum(sampleHeaderBuffer);
        bh.consume(checksum);
        return checksum;
    }

    @Benchmark
    public boolean verifyCheckSumBenchmark(Blackhole bh) {
        boolean result = TarUtils.verifyCheckSum(checksumVerificationBuffer);
        bh.consume(result);
        return result;
    }

    // --- Name Formatting and Parsing Benchmarks ---

    @Benchmark
    public int formatNameBytesDefaultEncodingBenchmark(Blackhole bh) throws java.io.IOException {
        int result = TarUtils.formatNameBytes(sampleFileName, outputBuffer, 0, BUFFER_SIZE);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public String parseNameDefaultEncodingBenchmark(Blackhole bh) throws java.io.IOException {
        // Use a subset of the name bytes
        byte[] nameSlice = Arrays.copyOf(sampleNameBytes, sampleNameBytes.length);
        String result = TarUtils.parseName(nameSlice, 0, nameSlice.length);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public int formatNameBytesSpecificEncodingBenchmark(Blackhole bh) throws java.io.IOException {
        // Using a dummy ZipEncoding for the specific encoding test
        ZipEncoding dummyEncoding = new ZipEncoding() {
            @Override
            public boolean canEncode(final String name) { return true; }
            @Override
            public String decode(final byte[] buffer) { return new String(buffer, StandardCharsets.UTF_8); }
            @Override
            public ByteBuffer encode(final String name) { return ByteBuffer.wrap(name.getBytes(StandardCharsets.UTF_8)); }
        };
        int result = TarUtils.formatNameBytes(sampleFileName, outputBuffer, 0, BUFFER_SIZE, dummyEncoding);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public String parseNameSpecificEncodingBenchmark(Blackhole bh) throws java.io.IOException {
        // Using a dummy ZipEncoding for the specific encoding test
        ZipEncoding dummyEncoding = new ZipEncoding() {
            @Override
            public boolean canEncode(final String name) { return true; }
            @Override
            public String decode(final byte[] buffer) { return new String(buffer, StandardCharsets.UTF_8); }
            @Override
            public ByteBuffer encode(final String name) { return ByteBuffer.wrap(name.getBytes(StandardCharsets.UTF_8)); }
        };
        byte[] nameSlice = Arrays.copyOf(sampleNameBytes, sampleNameBytes.length);
        String result = TarUtils.parseName(nameSlice, 0, nameSlice.length, dummyEncoding);
        bh.consume(result);
        return result;
    }

    // --- Octal and Binary Parsing Benchmarks ---

    @Benchmark
    public long parseOctalBenchmark(Blackhole bh) {
        long result = TarUtils.parseOctal(sampleOctalBuffer, 0, sampleOctalBuffer.length);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public long parseOctalOrBinaryBenchmark(Blackhole bh) {
        long result = TarUtils.parseOctalOrBinary(sampleOctalBuffer, 0, sampleOctalBuffer.length);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public long parseBinaryLongBenchmark(Blackhole bh) {
        // Use the public wrapper method parseOctalOrBinary.
        // Since sampleBinaryBuffer starts with 0xFF and has length 8 (< 9),
        // this triggers the binary path internally.
        long result = TarUtils.parseOctalOrBinary(sampleBinaryBuffer, 0, sampleBinaryBuffer.length);
        bh.consume(result);
        return result;
    }

    // --- Field Formatting Benchmarks ---

    @Benchmark
    public int formatLongOctalBytesBenchmark(Blackhole bh) {
        // Using a simple value for formatting
        long value = 98765L;
        int result = TarUtils.formatLongOctalBytes(value, outputBuffer, 0, BUFFER_SIZE);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public int formatLongOctalOrBinaryBytesBenchmark(Blackhole bh) {
        // Using a simple value for formatting
        long value = 12345L;
        int result = TarUtils.formatLongOctalOrBinaryBytes(value, outputBuffer, 0, BUFFER_SIZE);
        bh.consume(result);
        return result;
    }

    // --- PAX Sparse Header Benchmarks ---

    @Benchmark
    public org.apache.commons.compress.archivers.tar.TarArchiveStructSparse parseSparseBenchmark(Blackhole bh) {
        // Use the pre-built binary buffer containing one sparse struct
        org.apache.commons.compress.archivers.tar.TarArchiveStructSparse result = TarUtils.parseSparse(sparseBinaryBuffer, 0);
        bh.consume(result);
        return result;
    }
}
