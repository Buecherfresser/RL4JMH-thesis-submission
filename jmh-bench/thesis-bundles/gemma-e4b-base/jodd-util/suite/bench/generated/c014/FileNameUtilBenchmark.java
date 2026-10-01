package bench.generated.c014;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import jodd.io.FileNameUtil;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class FileNameUtilBenchmark {

    // --- Input Fixtures ---
    private String simpleRelativePath;
    private String windowsAbsolutePath;
    private String unixAbsolutePath;
    private String mixedSeparatorPath;
    private String complexPath; // Includes dots and complex prefixes
    private String tildePath;
    private String pathForConcatBase;
    private String pathForConcatToAdd;
    private String pathForEquals1;
    private String pathForEquals2;
    private String pathForSplit;
    private String pathForHome;
    private String pathForRelative;
    private String pathForRelativeTarget;

    @Setup(Level.Trial)
    public void setup() {
        // 1. Simple relative path
        simpleRelativePath = "file.txt";

        // 2. Windows absolute path
        windowsAbsolutePath = "C:\\Users\\Public\\Documents\\report.pdf";

        // 3. Unix absolute path
        unixAbsolutePath = "/home/user/documents/image.png";

        // 4. Mixed separators path
        mixedSeparatorPath = "data/logs\\archive.zip";

        // 5. Complex path (dots, mixed separators, UNC/tilde)
        complexPath = "//server/share/data/../sub/./file.v1.0.txt";
        
        // 6. Tilde path
        tildePath = "~/project/src/main.java";

        // 7. Paths for concatenation
        pathForConcatBase = "/base/path/";
        pathForConcatToAdd = "file_to_add.log";
        
        // 8. Paths for comparison
        pathForEquals1 = "File.txt";
        pathForEquals2 = "file.txt"; // Case difference for system check
        
        // 9. Path for splitting
        pathForSplit = "/a/b/c.txt";
        
        // 10. Path for home resolution
        pathForHome = "~/test/path";
        
        // 11. Paths for relative path calculation
        pathForRelative = "/base/dir/";
        pathForRelativeTarget = "/base/dir/sub/target.txt";
    }

    // =================================================================================
    // Normalization Benchmarks
    // =================================================================================

    @Benchmark
    public String normalize_simpleRelativePath(Blackhole bh) {
        String result = FileNameUtil.normalize(simpleRelativePath);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public String normalize_windowsAbsolute(Blackhole bh) {
        String result = FileNameUtil.normalize(windowsAbsolutePath);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public String normalize_unixAbsolute(Blackhole bh) {
        String result = FileNameUtil.normalize(unixAbsolutePath);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public String normalize_mixedSeparators(Blackhole bh) {
        String result = FileNameUtil.normalize(mixedSeparatorPath);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public String normalize_complexPath(Blackhole bh) {
        String result = FileNameUtil.normalize(complexPath);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public String normalize_tildePath(Blackhole bh) {
        String result = FileNameUtil.normalize(tildePath);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public String normalizeNoEndSeparator_simpleRelative(Blackhole bh) {
        String result = FileNameUtil.normalizeNoEndSeparator(simpleRelativePath);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public String normalizeNoEndSeparator_unixAbsolute(Blackhole bh) {
        String result = FileNameUtil.normalizeNoEndSeparator(unixAbsolutePath);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public String normalizeNoEndSeparator_complexPath(Blackhole bh) {
        String result = FileNameUtil.normalizeNoEndSeparator(complexPath);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public String normalizeNoEndSeparator_mixedSeparators(Blackhole bh) {
        String result = FileNameUtil.normalizeNoEndSeparator(mixedSeparatorPath);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public String normalize_unixSeparatorExplicit(Blackhole bh) {
        String result = FileNameUtil.normalize(simpleRelativePath, true);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public String normalize_windowsSeparatorExplicit(Blackhole bh) {
        String result = FileNameUtil.normalize(simpleRelativePath, false);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public String normalizeNoEndSeparator_unixSeparatorExplicit(Blackhole bh) {
        String result = FileNameUtil.normalizeNoEndSeparator(simpleRelativePath, true);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public String normalizeNoEndSeparator_windowsSeparatorExplicit(Blackhole bh) {
        String result = FileNameUtil.normalizeNoEndSeparator(simpleRelativePath, false);
        bh.consume(result);
        return result;
    }

    // =================================================================================
    // Concatenation Benchmarks
    // =================================================================================

    @Benchmark
    public String concat_systemSeparator(Blackhole bh) {
        String result = FileNameUtil.concat(pathForConcatBase, pathForConcatToAdd);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public String concat_unixSeparatorExplicit(Blackhole bh) {
        String result = FileNameUtil.concat(pathForConcatBase, pathForConcatToAdd, true);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public String concat_windowsSeparatorExplicit(Blackhole bh) {
        String result = FileNameUtil.concat(pathForConcatBase, pathForConcatToAdd, false);
        bh.consume(result);
        return result;
    }

    // =================================================================================
    // Separator Conversion Benchmarks
    // =================================================================================

    @Benchmark
    public String separatorsToUnix_mixed(Blackhole bh) {
        String result = FileNameUtil.separatorsToUnix(mixedSeparatorPath);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public String separatorsToWindows_mixed(Blackhole bh) {
        String result = FileNameUtil.separatorsToWindows(mixedSeparatorPath);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public String separatorsToSystem_mixed(Blackhole bh) {
        String result = FileNameUtil.separatorsToSystem(mixedSeparatorPath);
        bh.consume(result);
        return result;
    }

    // =================================================================================
    // Prefix/Path Extraction Benchmarks
    // =================================================================================

    @Benchmark
    public int getPrefixLength_simple(Blackhole bh) {
        int result = FileNameUtil.getPrefixLength(simpleRelativePath);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public int getPrefixLength_windowsAbsolute(Blackhole bh) {
        int result = FileNameUtil.getPrefixLength(windowsAbsolutePath);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public int getPrefixLength_tildePath(Blackhole bh) {
        int result = FileNameUtil.getPrefixLength(tildePath);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public int getPrefixLength_complexPath(Blackhole bh) {
        int result = FileNameUtil.getPrefixLength(complexPath);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public int indexOfLastSeparator_mixed(Blackhole bh) {
        int result = FileNameUtil.indexOfLastSeparator(mixedSeparatorPath);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public int indexOfLastSeparator_unixAbsolute(Blackhole bh) {
        int result = FileNameUtil.indexOfLastSeparator(unixAbsolutePath);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public int indexOfExtension_simple(Blackhole bh) {
        int result = FileNameUtil.indexOfExtension(simpleRelativePath);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public int indexOfExtension_complexPath(Blackhole bh) {
        int result = FileNameUtil.indexOfExtension(complexPath);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public boolean hasExtension_simple(Blackhole bh) {
        boolean result = FileNameUtil.hasExtension(simpleRelativePath);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public String getPrefix_windowsAbsolute(Blackhole bh) {
        String result = FileNameUtil.getPrefix(windowsAbsolutePath);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public String getPrefix_tildePath(Blackhole bh) {
        String result = FileNameUtil.getPrefix(tildePath);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public String getPath_simple(Blackhole bh) {
        String result = FileNameUtil.getPath(simpleRelativePath);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public String getPathNoEndSeparator_unixAbsolute(Blackhole bh) {
        String result = FileNameUtil.getPathNoEndSeparator(unixAbsolutePath);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public String getFullPath_windowsAbsolute(Blackhole bh) {
        String result = FileNameUtil.getFullPath(windowsAbsolutePath);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public String getFullPathNoEndSeparator_complexPath(Blackhole bh) {
        String result = FileNameUtil.getFullPathNoEndSeparator(complexPath);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public String getName_simple(Blackhole bh) {
        String result = FileNameUtil.getName(simpleRelativePath);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public String getBaseName_simple(Blackhole bh) {
        String result = FileNameUtil.getBaseName(simpleRelativePath);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public String getExtension_simple(Blackhole bh) {
        String result = FileNameUtil.getExtension(simpleRelativePath);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public String removeExtension_simple(Blackhole bh) {
        String result = FileNameUtil.removeExtension(simpleRelativePath);
        bh.consume(result);
        return result;
    }

    // =================================================================================
    // Equality and Splitting Benchmarks
    // =================================================================================

    @Benchmark
    public boolean equals_exact(Blackhole bh) {
        boolean result = FileNameUtil.equals(pathForEquals1, pathForEquals2);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public boolean equalsOnSystem_caseInsensitive(Blackhole bh) {
        boolean result = FileNameUtil.equalsOnSystem(pathForEquals1, pathForEquals2);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public String[] split_simple(Blackhole bh) {
        String[] result = FileNameUtil.split(pathForSplit);
        bh.consume(result);
        return result;
    }

    // =================================================================================
    // Home and Relative Path Benchmarks
    // =================================================================================

    @Benchmark
    public String resolveHome_tildePath(Blackhole bh) {
        String result = FileNameUtil.resolveHome(pathForHome);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public String relativePath_standard(Blackhole bh) {
        String result = FileNameUtil.relativePath(pathForRelativeTarget, pathForRelative);
        bh.consume(result);
        return result;
    }
}
