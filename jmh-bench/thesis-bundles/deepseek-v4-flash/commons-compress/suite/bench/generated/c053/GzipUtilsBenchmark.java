package bench.generated.c053;

import org.openjdk.jmh.annotations.*;
import org.apache.commons.compress.compressors.gzip.GzipUtils;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class GzipUtilsBenchmark {

    private String tarName;
    private String tgzName;
    private String gzName;
    private String plainName;
    private String svgzName;
    private String svgName;
    private String wmzName;
    private String wmfName;
    private String noSuffix;

    @Setup(Level.Trial)
    public void setUp() {
        tarName = "package.tar";
        tgzName = "package.tgz";
        gzName = "file.gz";
        plainName = "file";
        svgzName = "image.svgz";
        svgName = "image.svg";
        wmzName = "image.wmz";
        wmfName = "image.wmf";
        noSuffix = "readme";
    }

    // getCompressedFileName (new)
    @Benchmark
    public String getCompressedFileName_tar() {
        return GzipUtils.getCompressedFileName(tarName);
    }

    @Benchmark
    public String getCompressedFileName_plain() {
        return GzipUtils.getCompressedFileName(plainName);
    }

    @Benchmark
    public String getCompressedFileName_svg() {
        return GzipUtils.getCompressedFileName(svgName);
    }

    // getCompressedFilename (deprecated)
    @Benchmark
    public String getCompressedFilename_tar() {
        return GzipUtils.getCompressedFilename(tarName);
    }

    // getUncompressedFileName (new)
    @Benchmark
    public String getUncompressedFileName_tgz() {
        return GzipUtils.getUncompressedFileName(tgzName);
    }

    @Benchmark
    public String getUncompressedFileName_gz() {
        return GzipUtils.getUncompressedFileName(gzName);
    }

    @Benchmark
    public String getUncompressedFileName_svgz() {
        return GzipUtils.getUncompressedFileName(svgzName);
    }

    @Benchmark
    public String getUncompressedFileName_wmz() {
        return GzipUtils.getUncompressedFileName(wmzName);
    }

    // getUncompressedFilename (deprecated)
    @Benchmark
    public String getUncompressedFilename_tgz() {
        return GzipUtils.getUncompressedFilename(tgzName);
    }

    // isCompressedFileName (new)
    @Benchmark
    public boolean isCompressedFileName_tgz() {
        return GzipUtils.isCompressedFileName(tgzName);
    }

    @Benchmark
    public boolean isCompressedFileName_plain() {
        return GzipUtils.isCompressedFileName(plainName);
    }

    @Benchmark
    public boolean isCompressedFileName_noSuffix() {
        return GzipUtils.isCompressedFileName(noSuffix);
    }

    // isCompressedFilename (deprecated)
    @Benchmark
    public boolean isCompressedFilename_tgz() {
        return GzipUtils.isCompressedFilename(tgzName);
    }
}
