package bench.generated.c080;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import org.apache.commons.compress.utils.BoundedInputStream;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class BoundedInputStreamBenchmark {

    private BoundedInputStream boundedInputStream;
    private byte[] payload;
    private final int READ_SIZE_SMALL = 1;
    private final int READ_SIZE_LARGE = 1024;

    @Setup(Level.Trial)
    public void setup() {
        // Create a representative payload (e.g., 1MB)
        int payloadSize = 1024 * 1024;
        payload = new byte[payloadSize];
        for (int i = 0; i < payloadSize; i++) {
            payload[i] = (byte) (i % 256);
        }

        // Create the underlying stream
        InputStream inputStream = new ByteArrayInputStream(payload);

        // Create the BoundedInputStream, limiting reads to the full payload size
        boundedInputStream = new BoundedInputStream(inputStream, payloadSize);
    }

    @Benchmark
    public void testGetBytesRemaining(Blackhole bh) {
        long remaining = boundedInputStream.getBytesRemaining();
        bh.consume(remaining);
    }

    @Benchmark
    public void testReadSmallChunk(Blackhole bh) throws Exception {
        // Read a small chunk
        byte[] buffer = new byte[READ_SIZE_SMALL];
        int bytesRead = boundedInputStream.read(buffer, 0, READ_SIZE_SMALL);
        bh.consume(bytesRead);
    }

    @Benchmark
    public void testReadLargeChunk(Blackhole bh) throws Exception {
        // Read a large chunk
        byte[] buffer = new byte[READ_SIZE_LARGE];
        int bytesRead = boundedInputStream.read(buffer, 0, READ_SIZE_LARGE);
        bh.consume(bytesRead);
    }
}
