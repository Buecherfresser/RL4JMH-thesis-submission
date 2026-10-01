package bench.generated.c014;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

import jodd.io.FileNameUtil;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class FileNameUtilBenchmark {

    // --- Setup Fixtures ---
    private String complexWindowsPath;
    private String complexUnixPath;
    private String simpleFilename;
    private String pathWithExtension;
    private String pathWithDoubleDot;
    private String pathWithPrefix;

    @Setup
    public void setup() {
        // Complex Windows path: C:\dev\project\file.txt
        complexWindowsPath = "C:\\dev\\project\\file.txt";
        // Complex Unix path: /home/user/data/config.ini
        complexUnixPath = "/home/user/data/config.ini";
        // Simple filename: simplefile.log
        simpleFilename = "simplefile.log";
        // Path with extension: /path/to/document.pdf
        pathWithExtension = "/path/to/document.pdf";
        // Path with double dot: /a/b/../c/./d.txt
        pathWithDoubleDot = "/a/b/../c/./d.txt";
        // Path with prefix: C:\
        pathWithPrefix = "C:\\";
    }

    // --- Normalization Benchmarks ---

    @Benchmark
    public void normalize_Windows(Blackhole bh) {
        String result = FileNameUtil.normalize(complexWindowsPath);
        bh.consume(result);
    }

    @Benchmark
    public void normalize_Unix(Blackhole bh) {
        String result = FileNameUtil.normalize(complexUnixPath, true);
        bh.consume(result);
    }

    @Benchmark
    public void normalizeNoEndSeparator_Windows(Blackhole bh) {
        String result = FileNameUtil.normalizeNoEndSeparator(complexWindowsPath);
        bh.consume(result);
    }

    @Benchmark
    public void normalizeNoEndSeparator_Unix(Blackhole bh) {
        String result = FileNameUtil.normalizeNoEndSeparator(complexUnixPath, true);
        bh.consume(result);
    }

    // --- Path Extraction Benchmarks ---

    @Benchmark
    public void getPrefix_Windows(Blackhole bh) {
        String result = FileNameUtil.getPrefix(complexWindowsPath);
        bh.consume(result);
    }

    @Benchmark
    public void getPrefix_Unix(Blackhole bh) {
        String result = FileNameUtil.getPrefix(complexUnixPath);
        bh.consume(result);
    }

    @Benchmark
    public void getPath_Windows(Blackhole bh) {
        String result = FileNameUtil.getPath(complexWindowsPath);
        bh.consume(result);
    }

    @Benchmark
    public void getPathNoEndSeparator_Unix(Blackhole bh) {
        String result = FileNameUtil.getPathNoEndSeparator(complexUnixPath);
        bh.consume(result);
    }

    @Benchmark
    public void getFullPath_Windows(Blackhole bh) {
        String result = FileNameUtil.getFullPath(complexWindowsPath);
        bh.consume(result);
    }

    @Benchmark
    public void getFullPathNoEndSeparator_Unix(Blackhole bh) {
        String result = FileNameUtil.getFullPathNoEndSeparator(complexUnixPath);
        bh.consume(result);
    }

    @Benchmark
    public void getName_Windows(Blackhole bh) {
        String result = FileNameUtil.getName(complexWindowsPath);
        bh.consume(result);
    }

    @Benchmark
    public void getBaseName_Windows(Blackhole bh) {
        String result = FileNameUtil.getBaseName(complexWindowsPath);
        bh.consume(result);
    }

    @Benchmark
    public void getExtension_Windows(Blackhole bh) {
        String result = FileNameUtil.getExtension(complexWindowsPath);
        bh.consume(result);
    }

    @Benchmark
    public void removeExtension_Windows(Blackhole bh) {
        String result = FileNameUtil.removeExtension(complexWindowsPath);
        bh.consume(result);
    }

    // --- Concatenation Benchmarks ---

    @Benchmark
    public void concat_Windows(Blackhole bh) {
        String result = FileNameUtil.concat(complexWindowsPath, simpleFilename);
        bh.consume(result);
    }

    @Benchmark
    public void concat_Unix(Blackhole bh) {
        String result = FileNameUtil.concat(complexUnixPath, simpleFilename, true);
        bh.consume(result);
    }

    @Benchmark
    public void concat_RelativePath(Blackhole bh) {
        String result = FileNameUtil.relativePath(pathWithExtension, complexWindowsPath);
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

    // --- Split Benchmarks ---

    @Benchmark
    public void split_ComplexPath(Blackhole bh) {
        String[] result = FileNameUtil.split(pathWithDoubleDot);
        bh.consume(result);
    }

    @Benchmark
    public void split_SimpleFilename(Blackhole bh) {
        String[] result = FileNameUtil.split(simpleFilename);
        bh.consume(result);
    }

    // --- Utility Benchmarks ---

    @Benchmark
    public void equals_True(Blackhole bh) {
        boolean result = FileNameUtil.equals(simpleFilename, simpleFilename);
        bh.consume(result);
    }

    @Benchmark
    public void equals_False(Blackhole bh) {
        boolean result = FileNameUtil.equals(simpleFilename, "differentfile.log");
        bh.consume(result);
    }
}
