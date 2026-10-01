package bench.generated.c041;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.compressors.FileNameUtil;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class FileNameUtilBenchmark {

    private FileNameUtil util;

    // Sample file names built in @Setup
    private String tarFileName;          // e.g., "archive.tar"
    private String tgzFileName;          // e.g., "archive.tgz"
    private String genericGzFileName;    // e.g., "archive.gz"
    private String noSuffixFileName;     // e.g., "readme"

    @Setup(Level.Trial)
    public void setUp() {
        Map<String, String> suffixMap = new HashMap<>();
        // common mappings
        suffixMap.put(".tgz", ".tar");
        suffixMap.put(".tbz2", ".tar");
        suffixMap.put(".txz", ".tar");
        suffixMap.put(".tz", ".tar");
        suffixMap.put(".gz", "");          // generic gzip suffix
        suffixMap.put("-z", "");           // another generic suffix
        suffixMap.put(".bz2", "");         // generic bzip2 suffix
        suffixMap.put(".xz", "");          // generic xz suffix
        suffixMap.put(".zip", "");         // generic zip suffix

        util = new FileNameUtil(Collections.unmodifiableMap(suffixMap), ".gz");

        tarFileName = "archive.tar";
        tgzFileName = "archive.tgz";
        genericGzFileName = "archive.gz";
        noSuffixFileName = "readme";
    }

    @Benchmark
    public String benchmarkGetCompressedFileName() {
        return util.getCompressedFileName(tarFileName);
    }

    @Benchmark
    public String benchmarkGetCompressedFilenameDeprecated() {
        return util.getCompressedFilename(tarFileName);
    }

    @Benchmark
    public String benchmarkGetUncompressedFileName() {
        return util.getUncompressedFileName(tgzFileName);
    }

    @Benchmark
    public String benchmarkGetUncompressedFilenameDeprecated() {
        return util.getUncompressedFilename(tgzFileName);
    }

    @Benchmark
    public boolean benchmarkIsCompressedFileName() {
        return util.isCompressedFileName(genericGzFileName);
    }

    @Benchmark
    public boolean benchmarkIsCompressedFilenameDeprecated() {
        return util.isCompressedFilename(genericGzFileName);
    }

    // Additional benchmark for a name without any known suffix to exercise the fallback path
    @Benchmark
    public String benchmarkGetCompressedFileNameNoSuffix() {
        return util.getCompressedFileName(noSuffixFileName);
    }

    @Benchmark
    public boolean benchmarkIsCompressedFileNameNegative() {
        return util.isCompressedFileName(noSuffixFileName);
    }
}
