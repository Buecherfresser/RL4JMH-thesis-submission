package bench.generated.c072;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.compressors.xz.XZUtils;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class XZUtilsBenchmark {

    private String fileNameTar;
    private String fileNameTxz;
    private String fileNameXz;
    private String fileNameNoExt;
    private byte[] correctHeader;
    private byte[] wrongHeader;

    @Setup(Level.Trial)
    public void setUp() {
        fileNameTar = "package.tar";
        fileNameTxz = "package.txz";
        fileNameXz = "package.xz";
        fileNameNoExt = "file";
        correctHeader = new byte[]{(byte) 0xFD, '7', 'z', 'X', 'Z', 0};
        wrongHeader = new byte[]{0, 1, 2, 3, 4, 5};
    }

    @Benchmark
    public String benchmarkGetCompressedFileNameTar() {
        return XZUtils.getCompressedFileName(fileNameTar);
    }

    @Benchmark
    public String benchmarkGetCompressedFileNameXz() {
        return XZUtils.getCompressedFileName(fileNameXz);
    }

    @Benchmark
    public String benchmarkGetUncompressedFileNameTxz() {
        return XZUtils.getUncompressedFileName(fileNameTxz);
    }

    @Benchmark
    public String benchmarkGetUncompressedFileNameXz() {
        return XZUtils.getUncompressedFileName(fileNameXz);
    }

    @Benchmark
    public boolean benchmarkIsCompressedFileNameTxz() {
        return XZUtils.isCompressedFileName(fileNameTxz);
    }

    @Benchmark
    public boolean benchmarkIsCompressedFileNameXz() {
        return XZUtils.isCompressedFileName(fileNameXz);
    }

    @Benchmark
    public boolean benchmarkIsCompressedFileNameNoExt() {
        return XZUtils.isCompressedFileName(fileNameNoExt);
    }

    @Benchmark
    public boolean benchmarkIsXZCompressionAvailable() {
        return XZUtils.isXZCompressionAvailable();
    }

    @Benchmark
    public boolean benchmarkMatchesCorrect() {
        return XZUtils.matches(correctHeader, correctHeader.length);
    }

    @Benchmark
    public boolean benchmarkMatchesWrong() {
        return XZUtils.matches(wrongHeader, wrongHeader.length);
    }

    @Benchmark
    public void benchmarkSetCacheXZAvailablityTrue(Blackhole bh) {
        XZUtils.setCacheXZAvailablity(true);
        bh.consume(XZUtils.isXZCompressionAvailable());
    }

    @Benchmark
    public void benchmarkSetCacheXZAvailablityFalse(Blackhole bh) {
        XZUtils.setCacheXZAvailablity(false);
        bh.consume(XZUtils.isXZCompressionAvailable());
    }
}
