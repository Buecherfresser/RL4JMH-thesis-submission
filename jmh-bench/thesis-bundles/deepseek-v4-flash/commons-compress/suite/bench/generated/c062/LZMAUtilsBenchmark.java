package bench.generated.c062;

import org.openjdk.jmh.annotations.*;
import org.apache.commons.compress.compressors.lzma.LZMAUtils;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class LZMAUtilsBenchmark {

    private String fileNameWithLzma;
    private String fileNameWithoutSuffix;
    private String fileNameWithDashLzma;
    private byte[] validSignature;
    private byte[] invalidSignature;
    private byte[] shortSignature;

    @Setup(Level.Trial)
    public void setup() {
        fileNameWithLzma = "example.txt.lzma";
        fileNameWithoutSuffix = "example.txt";
        fileNameWithDashLzma = "example.txt-lzma";
        validSignature = new byte[] { 0x5D, 0, 0 };
        invalidSignature = new byte[] { 0x5D, 0, 1 };
        shortSignature = new byte[] { 0x5D };
    }

    // --- getCompressedFileName (new API) ---

    @Benchmark
    public String getCompressedFileNameWithLzmaSuffix() {
        return LZMAUtils.getCompressedFileName(fileNameWithLzma);
    }

    @Benchmark
    public String getCompressedFileNameWithoutSuffix() {
        return LZMAUtils.getCompressedFileName(fileNameWithoutSuffix);
    }

    // --- getUncompressedFileName (new API) ---

    @Benchmark
    public String getUncompressedFileNameWithLzmaSuffix() {
        return LZMAUtils.getUncompressedFileName(fileNameWithLzma);
    }

    @Benchmark
    public String getUncompressedFileNameWithDashLzma() {
        return LZMAUtils.getUncompressedFileName(fileNameWithDashLzma);
    }

    @Benchmark
    public String getUncompressedFileNameWithoutSuffix() {
        return LZMAUtils.getUncompressedFileName(fileNameWithoutSuffix);
    }

    // --- isCompressedFileName (new API) ---

    @Benchmark
    public boolean isCompressedFileNameTrue() {
        return LZMAUtils.isCompressedFileName(fileNameWithLzma);
    }

    @Benchmark
    public boolean isCompressedFileNameFalse() {
        return LZMAUtils.isCompressedFileName(fileNameWithoutSuffix);
    }

    // --- matches ---

    @Benchmark
    public boolean matchesValidSignature() {
        return LZMAUtils.matches(validSignature, validSignature.length);
    }

    @Benchmark
    public boolean matchesInvalidSignature() {
        return LZMAUtils.matches(invalidSignature, invalidSignature.length);
    }

    @Benchmark
    public boolean matchesShortSignature() {
        return LZMAUtils.matches(shortSignature, shortSignature.length);
    }

    // --- availability ---

    @Benchmark
    public boolean isLZMACompressionAvailable() {
        return LZMAUtils.isLZMACompressionAvailable();
    }

    // --- deprecated methods (aliases) ---

    @Benchmark
    public String getCompressedFilenameDeprecated() {
        return LZMAUtils.getCompressedFilename(fileNameWithLzma);
    }

    @Benchmark
    public String getUncompressedFilenameDeprecated() {
        return LZMAUtils.getUncompressedFilename(fileNameWithLzma);
    }

    @Benchmark
    public boolean isCompressedFilenameDeprecated() {
        return LZMAUtils.isCompressedFilename(fileNameWithLzma);
    }
}
