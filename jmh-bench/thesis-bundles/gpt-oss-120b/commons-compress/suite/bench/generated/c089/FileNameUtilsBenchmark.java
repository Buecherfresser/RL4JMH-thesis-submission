package bench.generated.c089;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.utils.FileNameUtils;
import java.nio.file.Path;
import java.nio.file.Paths;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class FileNameUtilsBenchmark {

    private Path pathWithExtension;
    private Path pathWithoutExtension;
    private String stringWithExtension;
    private String stringWithoutExtension;

    @Setup(Level.Trial)
    public void setUp() {
        // Path containing an extension
        pathWithExtension = Paths.get("/tmp/dir/subdir/file.txt");
        // Path without an extension
        pathWithoutExtension = Paths.get("/tmp/dir/subdir/file");
        // Corresponding string representations
        stringWithExtension = "/tmp/dir/subdir/file.txt";
        stringWithoutExtension = "/tmp/dir/subdir/file";
    }

    @Benchmark
    public String getBaseNamePath() {
        return FileNameUtils.getBaseName(pathWithExtension);
    }

    @Benchmark
    public String getBaseNamePathNoExt() {
        return FileNameUtils.getBaseName(pathWithoutExtension);
    }

    @Benchmark
    public String getBaseNameString() {
        return FileNameUtils.getBaseName(stringWithExtension);
    }

    @Benchmark
    public String getBaseNameStringNoExt() {
        return FileNameUtils.getBaseName(stringWithoutExtension);
    }

    @Benchmark
    public String getExtensionPath() {
        return FileNameUtils.getExtension(pathWithExtension);
    }

    @Benchmark
    public String getExtensionPathNoExt() {
        return FileNameUtils.getExtension(pathWithoutExtension);
    }

    @Benchmark
    public String getExtensionString() {
        return FileNameUtils.getExtension(stringWithExtension);
    }

    @Benchmark
    public String getExtensionStringNoExt() {
        return FileNameUtils.getExtension(stringWithoutExtension);
    }
}
