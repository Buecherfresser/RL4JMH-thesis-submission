package bench.generated.c053;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.compressors.gzip.GzipUtils;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class GzipUtilsBenchmark {

    private String fileNameWithTar;
    private String fileNameWithSvg;
    private String fileNameGeneric;
    private String fileNameNoSuffix;

    @Setup(Level.Trial)
    public void setup() {
        // Inputs for testing compression mapping
        fileNameWithTar = "archive.tar";
        fileNameWithSvg = "image.svg";
        fileNameGeneric = "data.txt";
        fileNameNoSuffix = "file";
    }

    @Benchmark
    public void testGetCompressedFileName_Tar(Blackhole bh) {
        String result = GzipUtils.getCompressedFileName(fileNameWithTar);
        bh.consume(result);
    }

    @Benchmark
    public void testGetCompressedFileName_Svg(Blackhole bh) {
        String result = GzipUtils.getCompressedFileName(fileNameWithSvg);
        bh.consume(result);
    }

    @Benchmark
    public void testGetCompressedFileName_Generic(Blackhole bh) {
        String result = GzipUtils.getCompressedFileName(fileNameGeneric);
        bh.consume(result);
    }

    @Benchmark
    public void testGetCompressedFileName_NoSuffix(Blackhole bh) {
        String result = GzipUtils.getCompressedFileName(fileNameNoSuffix);
        bh.consume(result);
    }

    @Benchmark
    public void testGetUncompressedFileName_Tgz(Blackhole bh) {
        // Test case where input is compressed (e.g., archive.tgz -> archive.tar)
        String compressedName = "archive.tgz";
        String result = GzipUtils.getUncompressedFileName(compressedName);
        bh.consume(result);
    }

    @Benchmark
    public void testGetUncompressedFileName_Svgz(Blackhole bh) {
        // Test case where input is compressed (e.g., image.svgz -> image.svg)
        String compressedName = "image.svgz";
        String result = GzipUtils.getUncompressedFileName(compressedName);
        bh.consume(result);
    }

    @Benchmark
    public void testGetUncompressedFileName_GenericGz(Blackhole bh) {
        // Test case where input is generic compressed (e.g., data.txt.gz -> data.txt)
        String compressedName = "data.txt.gz";
        String result = GzipUtils.getUncompressedFileName(compressedName);
        bh.consume(result);
    }

    @Benchmark
    public void testGetUncompressedFileName_NoSuffix(Blackhole bh) {
        // Test case where input is not compressed
        String uncompressedName = "file.txt";
        String result = GzipUtils.getUncompressedFileName(uncompressedName);
        bh.consume(result);
    }

    @Benchmark
    public void testIsCompressedFileName_Tar(Blackhole bh) {
        // isCompressedFileName returns boolean, which is consumed correctly by Blackhole
        boolean result = GzipUtils.isCompressedFileName(fileNameWithTar);
        bh.consume(result);
    }

    @Benchmark
    public void testIsCompressedFileName_Svg(Blackhole bh) {
        boolean result = GzipUtils.isCompressedFileName(fileNameWithSvg);
        bh.consume(result);
    }

    @Benchmark
    public void testIsCompressedFileName_Generic(Blackhole bh) {
        boolean result = GzipUtils.isCompressedFileName(fileNameGeneric);
        bh.consume(result);
    }

    @Benchmark
    public void testIsCompressedFileName_NoSuffix(Blackhole bh) {
        boolean result = GzipUtils.isCompressedFileName(fileNameNoSuffix);
        bh.consume(result);
    }
}
