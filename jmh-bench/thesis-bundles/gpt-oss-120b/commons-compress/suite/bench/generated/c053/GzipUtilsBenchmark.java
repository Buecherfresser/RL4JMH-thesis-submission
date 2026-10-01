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

    private String[] fileNames;
    private int index;

    @Setup(Level.Trial)
    public void setUp() {
        fileNames = new String[] {
                "example.txt",
                "archive.tar",
                "image.svgz",
                "document.cpgz",
                "presentation.wmz",
                "graphic.emz",
                "data.gz",
                "script.z",
                "library-gz",
                "module-z",
                "resource_z",
                "compressed.tgz",
                "compressed.taz",
                "plainfile"
        };
        index = 0;
    }

    private String nextFileName() {
        int i = index;
        index = (index + 1) % fileNames.length;
        return fileNames[i];
    }

    @Benchmark
    public String benchmarkGetCompressedFileName() {
        return GzipUtils.getCompressedFileName(nextFileName());
    }

    @Benchmark
    public String benchmarkGetCompressedFilenameDeprecated() {
        return GzipUtils.getCompressedFilename(nextFileName());
    }

    @Benchmark
    public String benchmarkGetUncompressedFileName() {
        return GzipUtils.getUncompressedFileName(nextFileName());
    }

    @Benchmark
    public String benchmarkGetUncompressedFilenameDeprecated() {
        return GzipUtils.getUncompressedFilename(nextFileName());
    }

    @Benchmark
    public boolean benchmarkIsCompressedFileName() {
        return GzipUtils.isCompressedFileName(nextFileName());
    }

    @Benchmark
    public boolean benchmarkIsCompressedFilenameDeprecated() {
        return GzipUtils.isCompressedFilename(nextFileName());
    }

    @Benchmark
    public void benchmarkIsCompressedFileNameConsume(Blackhole bh) {
        bh.consume(GzipUtils.isCompressedFileName(nextFileName()));
    }
}
