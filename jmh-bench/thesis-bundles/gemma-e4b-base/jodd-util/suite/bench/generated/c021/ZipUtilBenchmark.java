package bench.generated.c021;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.TimeUnit;
import jodd.io.ZipUtil;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ZipUtilBenchmark {

    private File sourceFile;
    private File gzipFile;
    private File zipFile;
    private File destDir;
    private byte[] bytePayload;

    @Setup(Level.Trial)
    public void setup() throws IOException {
        // 1. Create source file
        Path sourcePath = Files.createTempFile("source", ".txt");
        Files.write(sourcePath, "This is a test payload for compression and archiving.".getBytes());
        sourceFile = sourcePath.toFile();

        // 2. Create GZIP compressed file
        gzipFile = ZipUtil.gzip(sourceFile);

        // 3. Create ZIP archive containing the source file
        zipFile = ZipUtil.zip(sourceFile);

        // 4. Create destination directory for extraction
        destDir = Files.createTempDirectory("unzip_dest").toFile();

        // 5. Create byte payload
        bytePayload = "A longer byte array payload for stream testing.".getBytes();
    }

    @TearDown(Level.Trial)
    public void teardown() {
        // Clean up files and directories
        if (sourceFile != null && sourceFile.exists()) {
            sourceFile.delete();
        }
        if (gzipFile != null && gzipFile.exists()) {
            gzipFile.delete();
        }
        if (zipFile != null && zipFile.exists()) {
            zipFile.delete();
        }
        if (destDir != null && destDir.exists()) {
            // Recursively delete directory contents
            try {
                Files.walk(destDir.toPath())
                        .sorted(java.util.Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete);
            } catch (IOException e) {
                // Ignore cleanup errors
            }
        }
    }

    // --- ZLIB Benchmarks ---

    @Benchmark
    public File zlib_file_compression(Blackhole bh) throws IOException {
        // Compresses sourceFile into zlib archive
        File result = ZipUtil.zlib(sourceFile);
        bh.consume(result);
        return result;
    }

    // --- GZIP Benchmarks ---

    @Benchmark
    public File gzip_file_compression_string(Blackhole bh) throws IOException {
        // Compresses sourceFile into gzip archive using string path
        File result = ZipUtil.gzip(sourceFile.getAbsolutePath());
        bh.consume(result);
        return result;
    }

    @Benchmark
    public File ungzip_file_decompression(Blackhole bh) throws IOException {
        // Decompresses gzipFile
        File result = ZipUtil.ungzip(gzipFile);
        bh.consume(result);
        return result;
    }

    // --- ZIP Benchmarks ---

    @Benchmark
    public File zip_file_archiving_string(Blackhole bh) throws IOException {
        // Zips sourceFile into zip archive using string path
        File result = ZipUtil.zip(sourceFile.getAbsolutePath());
        bh.consume(result);
        return result;
    }

    @Benchmark
    public List<String> listZip_content_listing(Blackhole bh) throws IOException {
        // Lists contents of the zip archive
        List<String> entries = ZipUtil.listZip(zipFile);
        bh.consume(entries);
        return entries;
    }

    @Benchmark
    public void unzip_file_extraction_patterns(Blackhole bh) throws IOException {
        // Extracts zip file content to the target directory, matching all entries
        ZipUtil.unzip(zipFile, destDir, null);
        bh.consume(true); // Consume the void result
    }

    @Benchmark
    public void unzip_file_extraction_specific_pattern(Blackhole bh) throws IOException {
        // Extracts zip file content, matching a specific pattern
        String[] patterns = new String[]{sourceFile.getName()};
        ZipUtil.unzip(zipFile, destDir, patterns);
        bh.consume(true); // Consume the void result
    }

    // --- ZIP Stream Benchmarks ---

    @Benchmark
    public void addToZip_byte_content(Blackhole bh) throws IOException {
        // Adds byte content into the zip as a file
        // Note: This requires a ZipOutputStream, which must be created and closed within the benchmark
        try (java.util.zip.ZipOutputStream zos = new java.util.zip.ZipOutputStream(Files.newOutputStream(Files.createTempFile("temp_out", ".zip")))) {
            ZipUtil.addToZip(zos, bytePayload, "test/data.bin", "Test Data");
            bh.consume(true);
        }
    }

    @Benchmark
    public void addToZip_file_content_recursive(Blackhole bh) throws IOException {
        // Adds a file (sourceFile) to the zip stream recursively
        // Note: This requires a ZipOutputStream, which must be created and closed within the benchmark
        try (java.util.zip.ZipOutputStream zos = new java.util.zip.ZipOutputStream(Files.newOutputStream(Files.createTempFile("temp_out", ".zip")))) {
            // Since sourceFile is a single file, recursive is technically irrelevant but tests the path logic
            ZipUtil.addToZip(zos, sourceFile, "test/file.txt", null, true);
            bh.consume(true);
        }
    }

    @Benchmark
    public void addFolderToZip_entry(Blackhole bh) throws IOException {
        // Adds a folder entry to the zip stream
        // Note: This requires a ZipOutputStream, which must be created and closed within the benchmark
        try (java.util.zip.ZipOutputStream zos = new java.util.zip.ZipOutputStream(Files.newOutputStream(Files.createTempFile("temp_out", ".zip")))) {
            ZipUtil.addFolderToZip(zos, "test/folder/", "Test Folder");
            bh.consume(true);
        }
    }
}
