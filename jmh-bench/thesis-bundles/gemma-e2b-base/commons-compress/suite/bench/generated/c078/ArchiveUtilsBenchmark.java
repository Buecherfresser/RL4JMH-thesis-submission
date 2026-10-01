package bench.generated.c078;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.utils.ArchiveUtils;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class ArchiveUtilsBenchmark {

    // --- State Fields for Inputs ---

    private byte[] zeroBuffer;
    private byte[] identicalBuffer;
    private byte[] differentBuffer;
    private byte[] bufferWithTrailingNulls;
    private byte[] partialBuffer1;
    private byte[] partialBuffer2;
    private String asciiString;
    private byte[] asciiBytes;
    private String sanitizedString;

    @Setup
    public void setup() {
        // 1. Setup byte arrays for comparison tests
        zeroBuffer = new byte[1024];
        Arrays.fill(zeroBuffer, (byte) 0);

        identicalBuffer = new byte[1024];
        Arrays.fill(identicalBuffer, (byte) 0xAA);

        differentBuffer = new byte[1024];
        Arrays.fill(differentBuffer, (byte) 0xBB);

        bufferWithTrailingNulls = new byte[1024];
        Arrays.fill(bufferWithTrailingNulls, (byte) 0xCC);
        // Add trailing nulls
        for (int i = 900; i < 1024; i++) {
            bufferWithTrailingNulls[i] = 0;
        }

        // 2. Setup byte arrays for offset/length tests
        partialBuffer1 = new byte[512];
        Arrays.fill(partialBuffer1, (byte) 0x11);
        partialBuffer2 = new byte[512];
        Arrays.fill(partialBuffer2, (byte) 0x11);
        // Make the end different
        partialBuffer2[500] = (byte) 0x22;

        // 3. Setup String/ASCII tests
        asciiString = "Test String 123";
        asciiBytes = ArchiveUtils.toAsciiBytes(asciiString);

        // 4. Setup Sanitization test
        // String containing non-printable characters (e.g., control characters)
        String inputWithControlChars = "Hello\u0001World\u001F";
        sanitizedString = ArchiveUtils.sanitize(inputWithControlChars);
    }

    // --- Benchmarks for isArrayZero ---

    @Benchmark
    public void benchmarkIsArrayZero(Blackhole bh) {
        bh.consume(ArchiveUtils.isArrayZero(zeroBuffer, 100));
    }

    // --- Benchmarks for isEqual (byte arrays) ---

    @Benchmark
    public void benchmarkIsEqual_Identical(Blackhole bh) {
        bh.consume(ArchiveUtils.isEqual(identicalBuffer, identicalBuffer));
    }

    @Benchmark
    public void benchmarkIsEqual_Different(Blackhole bh) {
        bh.consume(ArchiveUtils.isEqual(identicalBuffer, differentBuffer));
    }

    @Benchmark
    public void benchmarkIsEqual_WithTrailingNulls_True(Blackhole bh) {
        bh.consume(ArchiveUtils.isEqual(bufferWithTrailingNulls, bufferWithTrailingNulls, true));
    }

    @Benchmark
    public void benchmarkIsEqual_WithTrailingNulls_False(Blackhole bh) {
        bh.consume(ArchiveUtils.isEqual(bufferWithTrailingNulls, differentBuffer, true));
    }

    @Benchmark
    public void benchmarkIsEqual_OffsetLength(Blackhole bh) {
        bh.consume(ArchiveUtils.isEqual(partialBuffer1, 0, 512, partialBuffer2, 0, 512));
    }

    @Benchmark
    public void benchmarkIsEqual_OffsetLength_False(Blackhole bh) {
        bh.consume(ArchiveUtils.isEqual(partialBuffer1, 0, 512, partialBuffer2, 500, 512));
    }

    // --- Benchmarks for matchAsciiBuffer ---

    @Benchmark
    public void benchmarkMatchAsciiBuffer_Simple(Blackhole bh) {
        bh.consume(ArchiveUtils.matchAsciiBuffer(asciiString, asciiBytes));
    }

    @Benchmark
    public void benchmarkMatchAsciiBuffer_OffsetLength(Blackhole bh) {
        bh.consume(ArchiveUtils.matchAsciiBuffer(asciiString, asciiBytes, 0, asciiBytes.length));
    }

    // --- Benchmarks for sanitize ---

    @Benchmark
    public void benchmarkSanitize(Blackhole bh) {
        bh.consume(ArchiveUtils.sanitize(sanitizedString));
    }

    // --- Benchmarks for String/Byte Conversion ---

    @Benchmark
    public void benchmarkToAsciiBytes(Blackhole bh) {
        bh.consume(ArchiveUtils.toAsciiBytes(asciiString));
    }

    @Benchmark
    public void benchmarkToAsciiString_Full(Blackhole bh) {
        bh.consume(ArchiveUtils.toAsciiString(asciiBytes));
    }

    @Benchmark
    public void benchmarkToAsciiString_OffsetLength(Blackhole bh) {
        bh.consume(ArchiveUtils.toAsciiString(asciiBytes, 6, 10));
    }
}
