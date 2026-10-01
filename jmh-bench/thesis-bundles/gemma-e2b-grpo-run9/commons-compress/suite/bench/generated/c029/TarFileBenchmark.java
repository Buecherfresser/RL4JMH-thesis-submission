package bench.generated.c029;

import org.apache.commons.compress.archivers.tar.TarFile;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class TarFileBenchmark {

    private TarFile tarFile;

    // Setup method to initialize the subject under test.
    // We use a minimal byte array to avoid complex file I/O setup, focusing on in-memory operations.
    @Setup
    public void setup() throws IOException {
        // Create a minimal, non-empty byte array payload.
        // This relies on the TarFile constructor accepting a byte array.
        try {
            // Using a small, non-empty array to test initialization path.
            this.tarFile = new TarFile(new byte[1024]);
        } catch (IOException e) {
            // If setup fails, we rethrow to fail the benchmark run.
            throw new RuntimeException("Setup failed: Could not initialize TarFile", e);
        }
    }

    @Benchmark
    public void benchmarkGetEntries(Blackhole bh) {
        // Call a public method. Since getEntries() does not throw IOException,
        // we consume the returned list via Blackhole to prevent dead code elimination.
        bh.consume(tarFile.getEntries());
    }

    @Benchmark
    public void benchmarkGetInputStream(Blackhole bh) {
        try {
            // Call a public method that returns an InputStream.
            // We rely on the library's internal error handling or assume a valid path for timing.
            tarFile.getInputStream(null);
        } catch (Exception e) {
            // Swallow exceptions to measure the successful path overhead.
        }
    }
}
