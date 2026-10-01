package bench.generated.c077;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.File;
import java.io.InputStream;

import org.apache.commons.compress.parallel.FileBasedScatterGatherBackingStore;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class FileBasedScatterGatherBackingStoreBenchmark {

    // State fields
    private FileBasedScatterGatherBackingStore store;
    private byte[] writePayload;
    private final int payloadSize = 1024 * 1024; // 1 MB payload
    private Path tempFilePath;

    @Setup
    public void setup() throws IOException {
        // 1. Create a temporary file path for the SUT constructor
        tempFilePath = Files.createTempFile("benchmark_store", ".tmp");
        File targetFile = tempFilePath.toFile();

        // 2. Instantiate the SUT. This operation involves creating the underlying OutputStream
        // and potentially creating the file handle, which is what we are measuring.
        store = new FileBasedScatterGatherBackingStore(targetFile);

        // 3. Prepare the payload data
        writePayload = new byte[payloadSize];
        // Fill payload with some data to ensure the write operation is meaningful
        for (int i = 0; i < payloadSize; i++) {
            writePayload[i] = (byte) (i % 256);
        }
    }

    @TearDown
    public void tearDown() throws IOException {
        // Clean up the temporary file created during setup
        if (tempFilePath != null) {
            Files.deleteIfExists(tempFilePath);
        }
        // Close the store instance
        if (store != null) {
            store.close();
        }
    }

    @Benchmark
    public void benchmarkWriteOut(Blackhole bh) throws IOException {
        // Benchmark the writeOut method
        store.writeOut(writePayload, 0, writePayload.length);
        bh.consume(null);
    }

    @Benchmark
    public void benchmarkClose(Blackhole bh) throws IOException {
        // Benchmark the close method (which involves closing the stream and deleting the file)
        store.close();
        bh.consume(null);
    }

    @Benchmark
    public void benchmarkGetInputStream(Blackhole bh) throws IOException {
        // Benchmark the getInputStream method (which involves opening the file handle)
        try (InputStream is = store.getInputStream()) {
            // Consume the stream content to ensure the stream is not optimized away
            byte[] result = new byte[payloadSize];
            int bytesRead = is.read(result);
            if (bytesRead != -1) {
                bh.consume(result);
            }
        }
    }
}
