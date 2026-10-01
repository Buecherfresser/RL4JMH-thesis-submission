package bench.generated.c034;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.EntryStreamOffsets;
import org.apache.commons.compress.utils.ByteUtils;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.LinkOption;
import java.nio.file.attribute.FileTime;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class ZipArchiveEntryBenchmark {

    // Fixed payload data to simulate archive content
    private byte[] archivePayload;
    private ZipArchiveEntry entry;
    private byte[] extraFieldData;

    @Setup
    public void setup() throws IOException {
        // 1. Create a representative payload (e.g., 10KB of dummy data)
        int payloadSize = 1024 * 10;
        this.archivePayload = new byte[payloadSize];
        Arrays.fill(this.archivePayload, (byte) 0xAA);

        // 2. Create a representative extra field data
        this.extraFieldData = new byte[512];
        Arrays.fill(this.extraFieldData, (byte) 0xBB);

        // 3. Create a base entry using the payload
        // We use a dummy File for construction since the constructor requires one.
        Path dummyPath = Paths.get("dummy_file.txt");
        Files.createFile(dummyPath);
        this.entry = new ZipArchiveEntry(dummyPath, "test_file.zip");
    }

    @Benchmark
    public void benchmarkGetSize(Blackhole bh) {
        bh.consume(entry.getSize());
    }

    @Benchmark
    public void benchmarkGetExtraFields(Blackhole bh) {
        // Test getting parseable fields
        bh.consume(entry.getExtraFields());
    }

    @Benchmark
    public void benchmarkGetExtraFieldsIncludingUnparseable(Blackhole bh) {
        // Test getting all fields, including unparseable ones
        bh.consume(entry.getExtraFields(true));
    }

    @Benchmark
    public void benchmarkSetExtra(Blackhole bh) {
        // Test setting extra data (simulating writing/parsing)
        try {
            entry.setExtra(extraFieldData);
        } catch (RuntimeException e) {
            // Ignore runtime exceptions for benchmarking purposes if they are expected in certain modes
        }
        bh.consume(entry.getExtraFields());
    }

    @Benchmark
    public void benchmarkSetCentralDirectoryExtra(Blackhole bh) {
        // Test setting central directory data
        entry.setCentralDirectoryExtra(extraFieldData);
        bh.consume(entry.getCentralDirectoryExtra());
    }

    @Benchmark
    public void benchmarkClone(Blackhole bh) {
        // Test cloning functionality
        Object clonedEntry = entry.clone();
        bh.consume(clonedEntry);
    }

    @Benchmark
    public void benchmarkIsDirectory(Blackhole bh) {
        // Test directory check (based on name ending with '/')
        bh.consume(entry.isDirectory());
    }

    @Benchmark
    public void benchmarkGetCommentSource(Blackhole bh) {
        // Test comment source retrieval
        bh.consume(entry.getCommentSource());
    }
}
