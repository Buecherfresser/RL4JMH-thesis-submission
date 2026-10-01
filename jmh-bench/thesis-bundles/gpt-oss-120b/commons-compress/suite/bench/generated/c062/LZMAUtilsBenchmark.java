package bench.generated.c062;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.compressors.lzma.LZMAUtils;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class LZMAUtilsBenchmark {

    @State(Scope.Benchmark)
    public static class BenchmarkState {
        String fileNamePlain;
        String fileNameLzma;
        String fileNameNoSuffix;
        byte[] matchingSignature;
        byte[] nonMatchingSignature;

        @Setup(Level.Trial)
        public void setUp() {
            fileNamePlain = "example.txt";
            fileNameLzma = "example.txt.lzma";
            fileNameNoSuffix = "example";
            matchingSignature = new byte[] { (byte) 0x5D, 0, 0 };
            nonMatchingSignature = new byte[] { (byte) 0x00, (byte) 0xFF, (byte) 0xAA };
        }
    }

    @Benchmark
    public String benchmarkGetCompressedFileName(BenchmarkState state) {
        return LZMAUtils.getCompressedFileName(state.fileNamePlain);
    }

    @Benchmark
    public String benchmarkGetCompressedFilenameDeprecated(BenchmarkState state) {
        return LZMAUtils.getCompressedFilename(state.fileNamePlain);
    }

    @Benchmark
    public String benchmarkGetUncompressedFileName(BenchmarkState state) {
        return LZMAUtils.getUncompressedFileName(state.fileNameLzma);
    }

    @Benchmark
    public String benchmarkGetUncompressedFilenameDeprecated(BenchmarkState state) {
        return LZMAUtils.getUncompressedFilename(state.fileNameLzma);
    }

    @Benchmark
    public boolean benchmarkIsCompressedFileNameTrue(BenchmarkState state) {
        return LZMAUtils.isCompressedFileName(state.fileNameLzma);
    }

    @Benchmark
    public boolean benchmarkIsCompressedFileNameFalse(BenchmarkState state) {
        return LZMAUtils.isCompressedFileName(state.fileNamePlain);
    }

    @Benchmark
    public boolean benchmarkIsCompressedFilenameDeprecatedTrue(BenchmarkState state) {
        return LZMAUtils.isCompressedFilename(state.fileNameLzma);
    }

    @Benchmark
    public boolean benchmarkIsCompressedFilenameDeprecatedFalse(BenchmarkState state) {
        return LZMAUtils.isCompressedFilename(state.fileNamePlain);
    }

    @Benchmark
    public boolean benchmarkMatchesTrue(BenchmarkState state) {
        return LZMAUtils.matches(state.matchingSignature, state.matchingSignature.length);
    }

    @Benchmark
    public boolean benchmarkMatchesFalse(BenchmarkState state) {
        return LZMAUtils.matches(state.nonMatchingSignature, state.nonMatchingSignature.length);
    }

    @Benchmark
    public boolean benchmarkIsLZMACompressionAvailable() {
        return LZMAUtils.isLZMACompressionAvailable();
    }

    @Benchmark
    public void benchmarkSetCacheLZMAAvailablityTrue(Blackhole bh) {
        LZMAUtils.setCacheLZMAAvailablity(true);
        bh.consume(true);
    }

    @Benchmark
    public void benchmarkSetCacheLZMAAvailablityFalse(Blackhole bh) {
        LZMAUtils.setCacheLZMAAvailablity(false);
        bh.consume(false);
    }
}
