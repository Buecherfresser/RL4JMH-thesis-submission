package bench.generated.c016;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.archivers.examples.Archiver;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Files;
import java.util.Collections;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ArchiverBenchmark {

    private Archiver archiver;
    private Path tempDir;
    private File tempDirFile;

    @Setup(Level.Trial)
    public void setUp() throws IOException {
        archiver = new Archiver();
        tempDir = Files.createTempDirectory("archiver-bench");
        tempDirFile = tempDir.toFile();

        // create a few small files
        Files.write(tempDir.resolve("file1.txt"), Collections.singletonList("Hello World"));
        Files.write(tempDir.resolve("file2.txt"), Collections.singletonList("Apache Commons Compress"));
        // create a subdirectory with a file
        Path subDir = Files.createDirectory(tempDir.resolve("sub"));
        Files.write(subDir.resolve("nested.txt"), Collections.singletonList("Nested file"));
    }

    @Benchmark
    public byte[] zipArchiveViaString(Blackhole bh) throws IOException, ArchiveException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        archiver.create(ArchiveStreamFactory.ZIP, baos, tempDirFile);
        byte[] result = baos.toByteArray();
        bh.consume(result);
        return result;
    }

    @Benchmark
    public byte[] zipArchiveViaArchiveOutputStream(Blackhole bh) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipArchiveOutputStream zipOut = new ZipArchiveOutputStream(baos)) {
            archiver.create((ArchiveOutputStream<?>) zipOut, tempDir);
        }
        byte[] result = baos.toByteArray();
        bh.consume(result);
        return result;
    }
}
