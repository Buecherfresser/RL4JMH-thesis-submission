package bench.generated.c077;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.io.InputStream;
import java.io.IOException;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.commons.compress.parallel.FileBasedScatterGatherBackingStore;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class FileBasedScatterGatherBackingStoreBenchmark {

    private byte[] data;
    private Path tempDir;
    private FileBasedScatterGatherBackingStore readStore;

    @Setup(Level.Trial)
    public void setUp() throws Exception {
        // Create a temporary directory for all benchmark files
        tempDir = Files.createTempDirectory("jmh-bench-fbsgbs");
        // Prepare a modest payload (e.g., 4 KiB)
        int size = 4 * 1024;
        data = new byte[size];
        for (int i = 0; i < size; i++) {
            data[i] = (byte) (i & 0xFF);
        }
        // Write the payload once so we can benchmark reading
        Path file = Files.createTempFile(tempDir, "read-store", ".tmp");
        readStore = new FileBasedScatterGatherBackingStore(file.toFile());
        readStore.writeOut(data, 0, data.length);
        readStore.closeForWriting(); // keep the file for reading
    }

    @TearDown(Level.Trial)
    public void tearDown() throws Exception {
        // Delete the temporary directory recursively
        if (tempDir != null && Files.exists(tempDir)) {
            Files.walk(tempDir)
                .sorted((p1, p2) -> p2.compareTo(p1)) // delete children first
                .forEach(p -> {
                    try {
                        Files.deleteIfExists(p);
                    } catch (IOException ignored) {
                    }
                });
        }
    }

    @Benchmark
    public int writeOutBenchmark() throws Exception {
        // Each invocation gets its own fresh file to avoid unbounded growth
        Path file = Files.createTempFile(tempDir, "write-store", ".tmp");
        FileBasedScatterGatherBackingStore store = new FileBasedScatterGatherBackingStore(file.toFile());
        store.writeOut(data, 0, data.length);
        store.closeForWriting();
        store.close(); // deletes the file
        return data.length;
    }

    @Benchmark
    public int readBenchmark() throws Exception {
        InputStream in = readStore.getInputStream();
        int total = 0;
        byte[] buffer = new byte[256];
        int n;
        while ((n = in.read(buffer)) != -1) {
            total += n;
        }
        in.close();
        return total;
    }

    @Benchmark
    public void closeForWritingBenchmark(Blackhole bh) throws Exception {
        // Fresh store per invocation; we only measure the closeForWriting call
        Path file = Files.createTempFile(tempDir, "close-store", ".tmp");
        FileBasedScatterGatherBackingStore store = new FileBasedScatterGatherBackingStore(file.toFile());
        store.writeOut(data, 0, data.length);
        store.closeForWriting();
        bh.consume(file);
        // Clean up the file manually
        Files.deleteIfExists(file);
    }
}
