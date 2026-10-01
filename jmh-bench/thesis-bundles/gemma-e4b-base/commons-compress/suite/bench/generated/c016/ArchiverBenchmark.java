package bench.generated.c016;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.archivers.examples.Archiver;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ArchiverBenchmark {

    private Path tempDir;
    private Path archiveTarget;
    private Archiver archiver;
    private static final String ARCHIVE_FORMAT = "tar";

    @Setup(Level.Trial)
    public void setup() throws IOException {
        archiver = new Archiver();
        
        // 1. Create a temporary root directory for input files
        tempDir = Files.createTempDirectory("archiver_input_");
        
        // 2. Create a subdirectory
        Path subDir = Files.createDirectory(tempDir.resolve("sub"));
        
        // 3. Create dummy files
        // File 1 in root
        Path file1 = Files.createFile(tempDir.resolve("file1.txt"));
        Files.write(file1, "Content of file 1.".getBytes());
        
        // File 2 in root
        Path file2 = Files.createFile(tempDir.resolve("file2.dat"));
        Files.write(file2, new byte[1024]); // 1KB file
        
        // File 3 in subdirectory
        Path file3 = Files.createFile(subDir.resolve("file3.log"));
        Files.write(file3, "Log entry.".getBytes());

        // 4. Create a temporary output file for the archive
        archiveTarget = Files.createTempFile("archiver_output_", ".tar");
    }

    @TearDown(Level.Trial)
    public void tearDown() throws IOException {
        // Clean up temporary files and directories
        if (tempDir != null) {
            Files.walk(tempDir)
                 .sorted(java.util.Comparator.reverseOrder())
                 .map(Path::toFile)
                 .forEach(java.io.File::delete);
        }
        if (archiveTarget != null) {
            Files.deleteIfExists(archiveTarget);
        }
    }

    /**
     * Benchmarks creating a TAR archive from a small, fixed directory structure.
     * This tests the core logic of file traversal and archive entry creation.
     */
    @Benchmark
    public void benchmarkTarArchiveCreation(Blackhole bh) throws IOException {
        // The Archiver.create method handles the stream creation and traversal internally.
        // We call it once per invocation.
        archiver.create(ARCHIVE_FORMAT, archiveTarget, tempDir);
        
        // Consume the result (the side effect is writing to archiveTarget, which we ensure exists)
        bh.consume(archiveTarget);
    }
}
