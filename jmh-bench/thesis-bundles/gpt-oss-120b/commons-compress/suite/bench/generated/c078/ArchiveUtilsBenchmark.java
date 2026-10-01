package bench.generated.c078;

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
import java.nio.charset.StandardCharsets;
import org.apache.commons.compress.utils.ArchiveUtils;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveEntry;

@State(org.openjdk.jmh.annotations.Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ArchiveUtilsBenchmark {

    // Arrays for isArrayZero
    private byte[] zeroArray;
    private byte[] nonZeroArray;

    // Buffers for equality checks
    private byte[] bufferA;
    private byte[] bufferB;
    private byte[] bufferWithTrailingZeros;
    private byte[] bufferLongerNonZero;

    // ASCII conversion data
    private String asciiString;
    private byte[] asciiBytes;

    // String with control characters for sanitize
    private String stringWithControl;

    // ArchiveEntry for toString
    private ArchiveEntry zipEntry;

    @Setup
    public void setup() {
        // zeroArray: all zeros
        zeroArray = new byte[128];

        // nonZeroArray: all 1s
        nonZeroArray = new byte[128];
        for (int i = 0; i < nonZeroArray.length; i++) {
            nonZeroArray[i] = 1;
        }

        // bufferA and bufferB: identical content
        asciiString = "BenchmarkTest";
        asciiBytes = asciiString.getBytes(StandardCharsets.US_ASCII);
        bufferA = asciiBytes.clone();
        bufferB = asciiBytes.clone();

        // bufferWithTrailingZeros: bufferA + zeros
        bufferWithTrailingZeros = new byte[bufferA.length + 10];
        System.arraycopy(bufferA, 0, bufferWithTrailingZeros, 0, bufferA.length);
        // remaining bytes are already zero

        // bufferLongerNonZero: bufferA + non‑zero bytes
        bufferLongerNonZero = new byte[bufferA.length + 5];
        System.arraycopy(bufferA, 0, bufferLongerNonZero, 0, bufferA.length);
        for (int i = bufferA.length; i < bufferLongerNonZero.length; i++) {
            bufferLongerNonZero[i] = (byte) (i - bufferA.length + 1);
        }

        // string with control characters for sanitize
        stringWithControl = "Normal\u0000Char\u001F\u007F\uFFFFEnd";

        // ZipArchiveEntry for toString
        ZipArchiveEntry entry = new ZipArchiveEntry("test/file.txt");
        entry.setSize(12345L);
        entry.setUnixMode(0100644); // regular file permissions
        zipEntry = entry;
    }

    @Benchmark
    public boolean benchmarkIsArrayZeroTrue() {
        return ArchiveUtils.isArrayZero(zeroArray, zeroArray.length);
    }

    @Benchmark
    public boolean benchmarkIsArrayZeroFalse() {
        return ArchiveUtils.isArrayZero(nonZeroArray, nonZeroArray.length);
    }

    @Benchmark
    public boolean benchmarkIsEqualSimple() {
        return ArchiveUtils.isEqual(bufferA, bufferB);
    }

    @Benchmark
    public boolean benchmarkIsEqualDifferent() {
        return ArchiveUtils.isEqual(bufferA, nonZeroArray);
    }

    @Benchmark
    public boolean benchmarkIsEqualWithOffsets() {
        return ArchiveUtils.isEqual(bufferA, 0, bufferA.length, bufferB, 0, bufferB.length);
    }

    @Benchmark
    public boolean benchmarkIsEqualIgnoreTrailingNullsTrue() {
        return ArchiveUtils.isEqual(bufferA, 0, bufferA.length,
                                   bufferWithTrailingZeros, 0, bufferWithTrailingZeros.length,
                                   true);
    }

    @Benchmark
    public boolean benchmarkIsEqualIgnoreTrailingNullsFalse() {
        return ArchiveUtils.isEqual(bufferA, 0, bufferA.length,
                                   bufferLongerNonZero, 0, bufferLongerNonZero.length,
                                   true);
    }

    @Benchmark
    public boolean benchmarkIsEqualWithNull() {
        return ArchiveUtils.isEqualWithNull(bufferA, 0, bufferA.length,
                                            bufferWithTrailingZeros, 0, bufferWithTrailingZeros.length);
    }

    @Benchmark
    public boolean benchmarkMatchAsciiBufferFull() {
        return ArchiveUtils.matchAsciiBuffer(asciiString, asciiBytes);
    }

    @Benchmark
    public boolean benchmarkMatchAsciiBufferPartial() {
        return ArchiveUtils.matchAsciiBuffer(asciiString, asciiBytes, 0, asciiBytes.length);
    }

    @Benchmark
    public String benchmarkSanitize() {
        return ArchiveUtils.sanitize(stringWithControl);
    }

    @Benchmark
    public byte[] benchmarkToAsciiBytes() {
        return ArchiveUtils.toAsciiBytes(asciiString);
    }

    @Benchmark
    public String benchmarkToAsciiStringFull() {
        return ArchiveUtils.toAsciiString(asciiBytes);
    }

    @Benchmark
    public String benchmarkToAsciiStringPartial() {
        return ArchiveUtils.toAsciiString(asciiBytes, 0, asciiBytes.length);
    }

    @Benchmark
    public String benchmarkToStringArchiveEntry() {
        return ArchiveUtils.toString(zipEntry);
    }

    // Ensure Blackhole consumption for void benchmarks (none present)
    @Benchmark
    public void benchmarkConsumeBlackhole(Blackhole bh) {
        // Example of consuming a value to avoid dead‑code elimination
        boolean result = ArchiveUtils.isArrayZero(zeroArray, zeroArray.length);
        bh.consume(result);
    }
}
