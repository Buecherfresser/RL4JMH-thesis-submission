package bench.generated.c018;

import jodd.io.PathUtil;
import org.openjdk.jmh.annotations.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class PathUtilBenchmark {

    private Path basePath;
    private String singleChild;
    private String[] multipleChildren;

    @Setup(Level.Trial)
    public void setup() {
        basePath = Paths.get("/some/base/directory");
        singleChild = "subdir/file.txt";
        multipleChildren = new String[] {"subdir1", "subdir2", "file.txt"};
    }

    @Benchmark
    public Path resolveSingle() {
        return PathUtil.resolve(basePath, singleChild);
    }

    @Benchmark
    public Path resolveMultiple() {
        return PathUtil.resolve(basePath, multipleChildren);
    }
}
