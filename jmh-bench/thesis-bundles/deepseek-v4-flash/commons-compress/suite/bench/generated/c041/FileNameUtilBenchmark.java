package bench.generated.c041;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.compressors.FileNameUtil;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class FileNameUtilBenchmark {

    private FileNameUtil util;
    private String tarName;          // "package.tar"
    private String tgzName;          // "package.tgz"
    private String genericGzName;    // "archive.gz"
    private String plainName;        // "file.txt"
    private String noSuffixName;     // "file"
    private String upperName;        // "PACKAGE.TAR"
    private String unknownSuffix;    // "file.unknown"
    private String shortName;        // "a"
    private String svgzName;         // "image.svgz"
    private String zName;            // "data.z"

    @Setup(Level.Trial)
    public void setup() {
        Map<String, String> suffixMap = new HashMap<>();
        suffixMap.put(".tgz", ".tar");
        suffixMap.put(".svgz", ".svg");
        suffixMap.put(".gz", "");
        suffixMap.put(".z", "");
        suffixMap.put("-z", "");
        util = new FileNameUtil(suffixMap, ".gz");

        tarName = "package.tar";
        tgzName = "package.tgz";
        genericGzName = "archive.gz";
        plainName = "file.txt";
        noSuffixName = "file";
        upperName = "PACKAGE.TAR";
        unknownSuffix = "file.unknown";
        shortName = "a";
        svgzName = "image.svgz";
        zName = "data.z";
    }

    // --- getCompressedFileName ---

    @Benchmark
    public String compressedWithCustomSuffix() {
        return util.getCompressedFileName(tarName);
    }

    @Benchmark
    public String compressedWithoutCustomSuffix() {
        return util.getCompressedFileName(plainName);
    }

    @Benchmark
    public String compressedWithNoSuffix() {
        return util.getCompressedFileName(noSuffixName);
    }

    @Benchmark
    public String compressedWithUpperCase() {
        return util.getCompressedFileName(upperName);
    }

    @Benchmark
    public String compressedWithUnknownSuffix() {
        return util.getCompressedFileName(unknownSuffix);
    }

    @Benchmark
    public String compressedWithShortName() {
        return util.getCompressedFileName(shortName);
    }

    // --- getUncompressedFileName ---

    @Benchmark
    public String uncompressedWithCustomSuffix() {
        return util.getUncompressedFileName(tgzName);
    }

    @Benchmark
    public String uncompressedWithGenericSuffix() {
        return util.getUncompressedFileName(genericGzName);
    }

    @Benchmark
    public String uncompressedWithSvgzSuffix() {
        return util.getUncompressedFileName(svgzName);
    }

    @Benchmark
    public String uncompressedWithZSuffix() {
        return util.getUncompressedFileName(zName);
    }

    @Benchmark
    public String uncompressedWithNoSuffix() {
        return util.getUncompressedFileName(plainName);
    }

    @Benchmark
    public String uncompressedWithUpperCase() {
        return util.getUncompressedFileName(upperName);
    }

    // --- isCompressedFileName ---

    @Benchmark
    public boolean isCompressedTrue() {
        return util.isCompressedFileName(tgzName);
    }

    @Benchmark
    public boolean isCompressedFalse() {
        return util.isCompressedFileName(plainName);
    }

    @Benchmark
    public boolean isCompressedWithUnknownSuffix() {
        return util.isCompressedFileName(unknownSuffix);
    }

    @Benchmark
    public boolean isCompressedWithShortName() {
        return util.isCompressedFileName(shortName);
    }

    // --- Deprecated variants (delegate to new methods) ---

    @Benchmark
    public String deprecatedCompressed() {
        return util.getCompressedFilename(tarName);
    }

    @Benchmark
    public String deprecatedUncompressed() {
        return util.getUncompressedFilename(tgzName);
    }

    @Benchmark
    public boolean deprecatedIsCompressed() {
        return util.isCompressedFilename(tgzName);
    }
}
