package bench.generated.c089;

import org.openjdk.jmh.annotations.*;
import org.apache.commons.compress.utils.FileNameUtils;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class FileNameUtilsBenchmark {

    private String fileNameWithExt;
    private String fileNameNoExt;
    private String fileNameWithDir;
    private Path pathWithExt;
    private Path pathNoExt;
    private Path pathWithDir;

    @Setup(Level.Trial)
    public void setup() {
        fileNameWithExt = "archive.tar.gz";
        fileNameNoExt = "README";
        fileNameWithDir = "/home/user/docs/report.pdf";
        pathWithExt = Paths.get("archive.tar.gz");
        pathNoExt = Paths.get("README");
        pathWithDir = Paths.get("/home/user/docs/report.pdf");
    }

    @Benchmark
    public String getBaseNameStringWithExt() {
        return FileNameUtils.getBaseName(fileNameWithExt);
    }

    @Benchmark
    public String getBaseNameStringNoExt() {
        return FileNameUtils.getBaseName(fileNameNoExt);
    }

    @Benchmark
    public String getBaseNameStringWithDir() {
        return FileNameUtils.getBaseName(fileNameWithDir);
    }

    @Benchmark
    public String getBaseNamePathWithExt() {
        return FileNameUtils.getBaseName(pathWithExt);
    }

    @Benchmark
    public String getBaseNamePathNoExt() {
        return FileNameUtils.getBaseName(pathNoExt);
    }

    @Benchmark
    public String getBaseNamePathWithDir() {
        return FileNameUtils.getBaseName(pathWithDir);
    }

    @Benchmark
    public String getExtensionStringWithExt() {
        return FileNameUtils.getExtension(fileNameWithExt);
    }

    @Benchmark
    public String getExtensionStringNoExt() {
        return FileNameUtils.getExtension(fileNameNoExt);
    }

    @Benchmark
    public String getExtensionStringWithDir() {
        return FileNameUtils.getExtension(fileNameWithDir);
    }

    @Benchmark
    public String getExtensionPathWithExt() {
        return FileNameUtils.getExtension(pathWithExt);
    }

    @Benchmark
    public String getExtensionPathNoExt() {
        return FileNameUtils.getExtension(pathNoExt);
    }

    @Benchmark
    public String getExtensionPathWithDir() {
        return FileNameUtils.getExtension(pathWithDir);
    }
}
