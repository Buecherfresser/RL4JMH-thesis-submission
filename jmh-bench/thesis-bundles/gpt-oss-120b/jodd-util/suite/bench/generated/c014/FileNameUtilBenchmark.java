package bench.generated.c014;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import jodd.io.FileNameUtil;
import java.io.File;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class FileNameUtilBenchmark {

    // Paths for various tests
    String unixPath;
    String windowsPath;
    String mixedPath;
    String basePath;
    String addPath;
    String pathWithExtension;
    String pathWithoutExtension;
    String homePath;
    String homePathWithSlash;
    String relativeBase;
    String relativeTarget;

    @Setup(Level.Trial)
    public void setup() {
        unixPath = "/foo/./bar/../baz/";
        windowsPath = "C:\\foo\\..\\bar\\baz.txt";
        mixedPath = "C:/foo/../bar/";
        basePath = "/foo/bar/";
        addPath = "baz/qux.txt";
        pathWithExtension = "/foo/bar/file.txt";
        pathWithoutExtension = "/foo/bar/file";
        homePath = "~";
        homePathWithSlash = "~/documents";
        relativeBase = "/foo/bar";
        relativeTarget = "/foo/bar/baz/qux.txt";
    }

    @Benchmark
    public String normalize() {
        return FileNameUtil.normalize(unixPath);
    }

    @Benchmark
    public String normalizeUnixSeparator() {
        return FileNameUtil.normalize(unixPath, true);
    }

    @Benchmark
    public String normalizeNoEndSeparator() {
        return FileNameUtil.normalizeNoEndSeparator(unixPath);
    }

    @Benchmark
    public String normalizeNoEndSeparatorUnix() {
        return FileNameUtil.normalizeNoEndSeparator(unixPath, true);
    }

    @Benchmark
    public String concat() {
        return FileNameUtil.concat(basePath, addPath);
    }

    @Benchmark
    public String concatUnix() {
        return FileNameUtil.concat(basePath, addPath, true);
    }

    @Benchmark
    public String separatorsToUnix() {
        return FileNameUtil.separatorsToUnix(windowsPath);
    }

    @Benchmark
    public String separatorsToWindows() {
        return FileNameUtil.separatorsToWindows(unixPath);
    }

    @Benchmark
    public String separatorsToSystem() {
        return FileNameUtil.separatorsToSystem(mixedPath);
    }

    @Benchmark
    public int getPrefixLengthUnix() {
        return FileNameUtil.getPrefixLength(unixPath);
    }

    @Benchmark
    public int indexOfLastSeparator() {
        return FileNameUtil.indexOfLastSeparator(unixPath);
    }

    @Benchmark
    public int indexOfExtension() {
        return FileNameUtil.indexOfExtension(pathWithExtension);
    }

    @Benchmark
    public boolean hasExtension() {
        return FileNameUtil.hasExtension(pathWithExtension);
    }

    @Benchmark
    public String getPrefix() {
        return FileNameUtil.getPrefix(windowsPath);
    }

    @Benchmark
    public String getPath() {
        return FileNameUtil.getPath(pathWithExtension);
    }

    @Benchmark
    public String getPathNoEndSeparator() {
        return FileNameUtil.getPathNoEndSeparator(pathWithExtension);
    }

    @Benchmark
    public String getFullPath() {
        return FileNameUtil.getFullPath(pathWithExtension);
    }

    @Benchmark
    public String getFullPathNoEndSeparator() {
        return FileNameUtil.getFullPathNoEndSeparator(pathWithExtension);
    }

    @Benchmark
    public String getName() {
        return FileNameUtil.getName(pathWithExtension);
    }

    @Benchmark
    public String getBaseName() {
        return FileNameUtil.getBaseName(pathWithExtension);
    }

    @Benchmark
    public String getExtension() {
        return FileNameUtil.getExtension(pathWithExtension);
    }

    @Benchmark
    public String removeExtension() {
        return FileNameUtil.removeExtension(pathWithExtension);
    }

    @Benchmark
    public boolean equals() {
        return FileNameUtil.equals(pathWithExtension, pathWithExtension);
    }

    @Benchmark
    public boolean equalsOnSystem() {
        return FileNameUtil.equalsOnSystem(pathWithExtension, pathWithExtension);
    }

    @Benchmark
    public String[] split() {
        return FileNameUtil.split(pathWithExtension);
    }

    @Benchmark
    public String resolveHome() {
        return FileNameUtil.resolveHome(homePath);
    }

    @Benchmark
    public String resolveHomeWithSlash() {
        return FileNameUtil.resolveHome(homePathWithSlash);
    }

    @Benchmark
    public String relativePath() {
        return FileNameUtil.relativePath(relativeTarget, relativeBase);
    }
}
