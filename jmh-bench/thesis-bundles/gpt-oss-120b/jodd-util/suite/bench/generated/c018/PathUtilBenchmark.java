package bench.generated.c018;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import jodd.io.PathUtil;
import java.nio.file.Path;
import java.nio.file.Paths;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class PathUtilBenchmark {

    private Path basePath;
    private String child;
    private String[] children;

    @Setup(Level.Trial)
    public void setUp() {
        basePath = Paths.get("base", "dir");
        child = "subdir/file.txt";
        children = new String[] { "subdir", "file.txt", "extra" };
    }

    @Benchmark
    public Path resolveSingle() {
        return PathUtil.resolve(basePath, child);
    }

    @Benchmark
    public Path resolveMultiple() {
        return PathUtil.resolve(basePath, children);
    }
}
