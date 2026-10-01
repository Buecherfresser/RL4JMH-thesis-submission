package bench.generated.c090;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import org.apache.commons.io.IOUtils;
import org.apache.commons.compress.utils.FixedLengthBlockOutputStream;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class FixedLengthBlockOutputStreamBenchmark {

    private FixedLengthBlockOutputStream stream;
    private byte[] largePayload;
    private final int BLOCK_SIZE = 4096;
    private final int PAYLOAD_SIZE = 1024 * 1024 * 4; // 4 MB payload

    @Setup
    public void setup() throws IOException {
        // 1. Create a large payload
        largePayload = new byte[PAYLOAD_SIZE];
        // Fill payload with some data (e.g., alternating bytes)
        for (int i = 0; i < PAYLOAD_SIZE; i++) {
            largePayload[i] = (byte) (i % 256);
        }

        // 2. Create a destination stream (ByteArrayOutputStream)
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        // 3. Initialize the SUT
        stream = new FixedLengthBlockOutputStream(baos, BLOCK_SIZE);
    }

    @Benchmark
    public void writeBytesArray(Blackhole bh) throws IOException {
        // Write a chunk of data that spans multiple blocks
        int offset = 0;
        int length = 1024 * 1024; // 1 MB chunk
        stream.write(largePayload, offset, length);
        
        // Consume the result to prevent dead code elimination
        bh.consume(stream);
    }

    @Benchmark
    public void writeByteBuffer(Blackhole bh) throws IOException {
        // Create a ByteBuffer from a portion of the payload
        ByteBuffer buffer = ByteBuffer.wrap(largePayload, 0, BLOCK_SIZE * 2);
        buffer.limit(BLOCK_SIZE * 2);
        buffer.position(0);

        // Write the buffer
        int written = stream.write(buffer);
        
        bh.consume(written);
        bh.consume(stream);
    }

    @Benchmark
    public void writeSingleByte(Blackhole bh) throws IOException {
        // Write a single byte repeatedly to test flushing overhead
        for (int i = 0; i < 1000; i++) {
            stream.write(1);
        }
        bh.consume(stream);
    }

    @Benchmark
    public void closeStream(Blackhole bh) throws IOException {
        // Test the close operation, which should trigger a final flush
        stream.close();
        bh.consume(stream);
    }
}
