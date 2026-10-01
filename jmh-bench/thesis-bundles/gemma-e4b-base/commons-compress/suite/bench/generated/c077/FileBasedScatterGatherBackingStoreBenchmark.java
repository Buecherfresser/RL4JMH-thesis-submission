package bench.generated.c077;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.parallel.FileBasedScatterGatherBackingStore;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class FileBasedScatterGatherBackingStoreBenchmark {

    private FileBasedScatterGatherBackingStore backingStore;
    private Path tempFilePath;
    private byte[] testData;
    private static final int DATA_SIZE = 4096;

    @Setup(Level.Trial)
    public void setup() throws IOException, FileNotFoundException {
        // 1. Create a temporary file path
        tempFilePath = Files.createTempFile("jmh_backingstore_", ".tmp");

        // 2. Initialize the SUT. This requires file creation/opening.
        // We use the Path constructor.
        backingStore = new FileBasedScatterGatherBackingStore(tempFilePath);

        // 3. Prepare test data
        testData = new byte[DATA_SIZE];
        for (int i = 0; i < DATA_SIZE; i++) {
            testData[i] = (byte) (i % 256);
        }
    }

    @TearDown(Level.Trial)
    public void tearDown() throws IOException {
        if (backingStore != null) {
            // Ensure the store is closed to clean up resources
            try {
                backingStore.close();
            } catch (IOException e) {
                // Ignore cleanup errors
            }
        }
        // Delete the temporary file created during setup
        if (tempFilePath != null) {
            Files.deleteIfExists(tempFilePath);
        }
    }

    @Benchmark
    public void writeOutData(Blackhole bh) throws IOException {
        // Write a chunk of data to the backing store
        backingStore.writeOut(testData, 0, DATA_SIZE);
        bh.consume(true);
    }

    @Benchmark
    public void getInputStream(Blackhole bh) throws IOException {
        // Get the input stream from the backing store
        InputStream is = backingStore.getInputStream();
        // Consume the stream to prevent dead code elimination
        bh.consume(is);
    }

    @Benchmark
    public void closeForWriting(Blackhole bh) throws IOException {
        // Close the writing stream without deleting the underlying file
        backingStore.closeForWriting();
        bh.consume(null);
    }

    @Benchmark
    public void closeStore(Blackhole bh) throws IOException {
        // Close the entire backing store (which also deletes the file)
        backingStore.close();
        bh.consume(null);
    }
}
