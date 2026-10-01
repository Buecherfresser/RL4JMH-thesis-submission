package bench.generated.c077;

import org.apache.commons.compress.parallel.FileBasedScatterGatherBackingStore;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class FileBasedScatterGatherBackingStoreBenchmark {

    private FileBasedScatterGatherBackingStore store;
    private byte[] writeData;
    private final int bufferSize = 1024 * 1024; // 1 MB

    @Setup(Level.Trial)
    public void setupTrial() throws IOException {
        // 1. Prepare input data
        writeData = new byte[bufferSize];
        // Fill data with non-zero values to ensure actual writing occurs
        for (int i = 0; i < bufferSize; i++) {
            writeData[i] = (byte) (i % 256);
        }

        // 2. Prepare the backing store instance
        // We must use a temporary file path to satisfy the constructor requirement.
        Path tempPath = Files.createTempFile("benchmark_store", ".tmp");
        File targetFile = tempPath.toFile();

        try {
            store = new FileBasedScatterGatherBackingStore(targetFile);
        } catch (IOException e) {
            throw new RuntimeException("Failed to setup FileBasedScatterGatherBackingStore", e);
        }
    }

    @TearDown(Level.Trial)
    public void tearDownTrial() throws IOException {
        if (store != null) {
            // Rely on store.close() to handle stream closing and file deletion
            store.close();
        }
    }

    @Benchmark
    public void benchmarkWriteOut(Blackhole bh) throws IOException {
        // Call the method exactly once per invocation
        store.writeOut(writeData, 0, writeData.length);
        bh.consume(null);
    }
}
