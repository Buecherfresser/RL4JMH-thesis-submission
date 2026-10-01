package bench.generated.c018;

import jodd.io.PathUtil;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class PathUtilBenchmark {

    private Path baseDir;
    private Path testFile;
    private Path testDir;
    private String testContent;

    @Setup(Level.Trial)
    public void setup() throws IOException {
        // 1. Setup for Path resolution (CPU bound)
        // Inputs are simple strings/paths, no complex setup needed beyond defining them.

        // 2. Setup for readString (IO bound)
        // Create a temporary file and write content to it.
        testDir = Files.createTempDirectory("jodd_test_dir");
        testFile = Files.createFile(testDir.resolve("test.txt"));
        testContent = "This is a test line.\nAnother line for benchmarking.";
        Files.write(testFile, testContent.getBytes(StandardCharsets.UTF_8));

        // 3. Setup for deleteFileTree (IO bound)
        // Create a temporary directory structure to delete.
        baseDir = Files.createTempDirectory("jodd_delete_test_dir");
        Path subDir = Files.createDirectory(baseDir.resolve("subdir"));
        Path fileInSubDir = Files.createFile(subDir.resolve("file.txt"));
        Files.createFile(baseDir.resolve("root_file.txt"));
    }

    @TearDown(Level.Trial)
    public void tearDown() throws IOException {
        // Clean up all created temporary resources
        if (baseDir != null) {
            PathUtil.deleteFileTree(baseDir);
        }
        if (testDir != null) {
            PathUtil.deleteFileTree(testDir);
        }
    }

    // --- Benchmarks for Path.resolve ---

    @Benchmark
    public Path resolve_singleChild(Blackhole bh) {
        // Test resolve(Path base, String child)
        Path result = PathUtil.resolve(testDir, "sub/file.txt");
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Path resolve_multipleChildren(Blackhole bh) {
        // Test resolve(Path path, final String... childs)
        String[] children = new String[]{"a", "b", "c"};
        Path result = PathUtil.resolve(testDir, children);
        bh.consume(result);
        return result;
    }

    // --- Benchmarks for readString ---

    @Benchmark
    public String readString_singleFile(Blackhole bh) throws IOException {
        // Test readString(final Path path)
        String result = PathUtil.readString(testFile);
        bh.consume(result);
        return result;
    }

    // --- Benchmarks for deleteFileTree ---

    @Benchmark
    public void deleteFileTree_recursive(Blackhole bh) throws IOException {
        // Test deleteFileTree(final Path directory)
        // Note: This is destructive, but since it's in Level.Trial setup/teardown, it's safe.
        PathUtil.deleteFileTree(baseDir);
        bh.consume(true);
    }
}
