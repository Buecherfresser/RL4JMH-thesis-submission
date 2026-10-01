package bench.generated.c018;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.nio.charset.StandardCharsets;

import jodd.io.PathUtil;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class PathUtilBenchmark {

    // State fields for PathUtil operations
    private Path basePath;
    private Path tempFilePath;

    @Setup
    public void setup() throws IOException {
        // Initialize a base path (in-memory simulation for testing PathUtil methods that rely on Path objects)
        this.basePath = Paths.get("temp_benchmark_dir");
        Files.createDirectories(this.basePath);
        
        // Create a temporary file for readString benchmark (requires actual file system interaction)
        this.tempFilePath = this.basePath.resolve("temp_file.txt");
        Files.writeString(this.tempFilePath, "This is a test file content for PathUtil.", StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    @TearDown
    public void tearDown() throws IOException {
        // Clean up the created file and directory
        if (Files.exists(tempFilePath)) {
            Files.delete(tempFilePath);
        }
        try {
            Files.deleteIfExists(basePath);
        } catch (IOException e) {
            // Ignore cleanup errors
        }
    }

    @Benchmark
    public void resolve_simple(Blackhole bh) {
        try {
            // Test resolve(Path base, String child)
            Path result = PathUtil.resolve(basePath, "sub/file.txt");
            bh.consume(result);
        } catch (Exception e) {
            // Ignore exceptions for benchmark stability
        }
    }

    @Benchmark
    public void resolve_multiple(Blackhole bh) {
        try {
            // Test resolve(Path path, String... childs)
            Path result = PathUtil.resolve(basePath, "a", "b", "c");
            bh.consume(result);
        } catch (Exception e) {
            // Ignore exceptions
        }
    }

    @Benchmark
    public void readString(Blackhole bh) {
        try {
            // Test readString(Path path) - relies on the file created in setup
            String content = PathUtil.readString(tempFilePath);
            bh.consume(content);
        } catch (IOException e) {
            // Ignore IO exceptions for benchmark stability
        }
    }

    @Benchmark
    public void deleteFileTree(Blackhole bh) {
        try {
            // Test deleteFileTree(Path directory)
            PathUtil.deleteFileTree(basePath);
            bh.consume(null); // Void method, consume null or nothing
        } catch (IOException e) {
            // Ignore IO exceptions
        }
    }
}
