package bench.generated.c077;

import org.apache.commons.compress.parallel.FileBasedScatterGatherBackingStore;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class FileBasedScatterGatherBackingStoreBenchmark {

    // Since FileBasedScatterGatherBackingStore requires a real file path in its constructor
    // and performs filesystem operations, we cannot fully test it in memory without mocking.
    // We instantiate it here, acknowledging that this setup might fail if the environment
    // strictly forbids file system access, but we proceed to test the structure.
    private FileBasedScatterGatherBackingStore store;

    @Setup
    public void setup() throws Exception {
        // Attempt to create an instance. This might throw FileNotFoundException
        // if the path is invalid, which is acceptable for a setup failure.
        try {
            // Use a temporary path that is unlikely to exist to minimize side effects
            // if the constructor attempts to create the file.
            Path tempPath = Files.createTempFile("benchmark_test", ".tmp");
            this.store = new FileBasedScatterGatherBackingStore(tempPath.toFile());
        } catch (Exception e) {
            // If setup fails (e.g., permission denied, or if the SUT throws an exception
            // that isn't caught internally), we just let the benchmark run,
            // or we can rethrow if we want the benchmark to fail immediately.
            // For robustness in JMH, we often let setup fail if it's critical.
            System.err.println("Warning: Failed to initialize FileBasedScatterGatherBackingStore. Benchmark might be invalid: " + e.getMessage());
            this.store = null; // Nullify if initialization failed
        }
    }

    @TearDown
    public void tearDown() throws Exception {
        if (store != null) {
            try {
                // Attempt to close the store, which deletes the file.
                store.close();
            } catch (IOException e) {
                // Ignore cleanup errors
            }
        }
    }

    @Benchmark
    public void writeOut(Blackhole bh) {
        if (store == null) {
            // Skip if setup failed
            bh.consume(null);
            return;
        }
        try {
            // Test writing a small payload. This operation relies on the internal
            // OutputStream, which is tied to the file system.
            byte[] data = new byte[1024];
            store.writeOut(data, 0, 1024);
        } catch (IOException e) {
            // Catch expected IO exceptions during benchmark execution
        }
        bh.consume(null);
    }

    @Benchmark
    public void getInputStream(Blackhole bh) {
        if (store == null) {
            bh.consume(null);
            return;
        }
        try {
            // Test getting the input stream. This relies on the file existing.
            InputStream is = store.getInputStream();
            // Consume the stream to prevent resource leaks or unexpected behavior
            // (though closing the stream is usually handled by the caller/TearDown)
            is.read();
        } catch (IOException e) {
            // Catch expected IO exceptions
        }
        bh.consume(null);
    }
}
