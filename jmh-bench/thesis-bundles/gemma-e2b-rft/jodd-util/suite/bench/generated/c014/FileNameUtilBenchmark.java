package bench.generated.c014;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.io.File;
import java.util.concurrent.TimeUnit;

import jodd.io.FileNameUtil;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class FileNameUtilBenchmark {

    // --- Input Fixtures ---
    // Complex path strings designed to test normalization logic (., .., mixed separators)
    private String complexWindowsPath;
    private String complexUnixPath;
    private String simplePath;
    private String pathWithExtension;
    private String pathWithDoubleDot;
    private String pathWithRelativeDot;
    private String pathWithTrailingSeparator;
    private String absoluteWindowsPath;
    private String absoluteUnixPath;
    private String pathWithUNC;

    // --- Setup ---
    @Setup
    public void setup() {
        // Windows style path: C:\foo\..\bar\file.txt
        this.complexWindowsPath = "C:\\foo\\..\\bar\\file.txt";
        // Unix style path: /foo/./bar/../baz
        this.complexUnixPath = "/foo/./bar/../baz";
        // Simple path
        this.simplePath = "a/b/c";
        // Path with extension
        this.pathWithExtension = "a/b/c.jpg";
        // Path with double dot
        this.pathWithDoubleDot = "/foo//./bar";
        // Path with relative dot
        this.pathWithRelativeDot = "foo/../bar";
        // Path with trailing separator
        this.pathWithTrailingSeparator = "a/b/c/";
        // Absolute Windows path
        this.absoluteWindowsPath = "C:\\a\\b\\c.txt";
        // Absolute Unix path
        this.absoluteUnixPath = "/a/b/c.txt";
        // UNC path
        this.pathWithUNC = "\\\\server\\foo\\bar\\file.txt";
    }

    // --- Normalization Benchmarks ---

    @Benchmark
    public void normalize_WindowsStyle(Blackhole bh) {
        String result = FileNameUtil.normalize(complexWindowsPath);
        bh.consume(result);
    }

    @Benchmark
    public void normalize_UnixStyle(Blackhole bh) {
        String result = FileNameUtil.normalize(complexUnixPath, true);
        bh.consume(result);
    }

    @Benchmark
    public void normalizeNoEndSeparator_WindowsStyle(Blackhole bh) {
        String result = FileNameUtil.normalizeNoEndSeparator(complexWindowsPath);
        bh.consume(result);
    }

    @Benchmark
    public void normalizeNoEndSeparator_UnixStyle(Blackhole bh) {
        String result = FileNameUtil.normalizeNoEndSeparator(complexUnixPath, true);
        bh.consume(result);
    }

    // --- Concatenation Benchmarks ---

    @Benchmark
    public void concat_WindowsSeparator(Blackhole bh) {
        String result = FileNameUtil.concat(complexWindowsPath, "new_file.txt", false);
        bh.consume(result);
    }

    @Benchmark
    public void concat_UnixSeparator(Blackhole bh) {
        String result = FileNameUtil.concat(complexUnixPath, "new_file.txt", true);
        bh.consume(result);
    }

    @Benchmark
    public void concat_BasePathNull(Blackhole bh) {
        String result = FileNameUtil.concat(null, "file.txt", false);
        bh.consume(result);
    }

    // --- Separator Conversion Benchmarks ---

    @Benchmark
    public void separatorsToUnix(Blackhole bh) {
        String result = FileNameUtil.separatorsToUnix(complexWindowsPath);
        bh.consume(result);
    }

    @Benchmark
    public void separatorsToWindows(Blackhole bh) {
        String result = FileNameUtil.separatorsToWindows(complexUnixPath);
        bh.consume(result);
    }

    @Benchmark
    public void separatorsToSystem(Blackhole bh) {
        String result = FileNameUtil.separatorsToSystem(complexWindowsPath);
        bh.consume(result);
    }

    // --- Prefix/Path Benchmarks ---

    @Benchmark
    public void getPrefix_AbsoluteWindows(Blackhole bh) {
        String prefix = FileNameUtil.getPrefix(absoluteWindowsPath);
        bh.consume(prefix);
    }

    @Benchmark
    public void getPath_ComplexUnix(Blackhole bh) {
        String path = FileNameUtil.getPath(complexUnixPath);
        bh.consume(path);
    }

    @Benchmark
    public void getFullPathNoEndSeparator_ComplexWindows(Blackhole bh) {
        String fullPath = FileNameUtil.getFullPathNoEndSeparator(complexWindowsPath);
        bh.consume(fullPath);
    }

    @Benchmark
    public void getName_PathWithExtension(Blackhole bh) {
        String name = FileNameUtil.getName(pathWithExtension);
        bh.consume(name);
    }

    @Benchmark
    public void getBaseName_PathWithExtension(Blackhole bh) {
        String baseName = FileNameUtil.getBaseName(pathWithExtension);
        bh.consume(baseName);
    }

    @Benchmark
    public void getExtension_PathWithExtension(Blackhole bh) {
        String extension = FileNameUtil.getExtension(pathWithExtension);
        bh.consume(extension);
    }

    @Benchmark
    public void removeExtension_PathWithExtension(Blackhole bh) {
        String result = FileNameUtil.removeExtension(pathWithExtension);
        bh.consume(result);
    }

    // --- Split Benchmarks ---

    @Benchmark
    public void split_ComplexPath(Blackhole bh) {
        String[] parts = FileNameUtil.split(complexWindowsPath);
        bh.consume(parts);
    }

    @Benchmark
    public void split_SimplePath(Blackhole bh) {
        String[] parts = FileNameUtil.split(simplePath);
        bh.consume(parts);
    }

    // --- Equality Benchmarks ---

    @Benchmark
    public void equals_CaseSensitive(Blackhole bh) {
        boolean result = FileNameUtil.equals(absoluteWindowsPath, "C:\\a\\b\\c.txt");
        bh.consume(result);
    }

    @Benchmark
    public void equalsOnSystem_CaseInsensitive(Blackhole bh) {
        boolean result = FileNameUtil.equalsOnSystem(absoluteWindowsPath, "c:\\a\\b\\c.txt");
        bh.consume(result);
    }
}
