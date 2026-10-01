package bench.generated.c092;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.util.concurrent.TimeUnit;

import org.apache.commons.compress.utils.IOUtils;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class IOUtilsBenchmark {

    private static final int PAYLOAD_SIZE = 1024 * 1024; // 1MB payload

    private InputStream inputStream;
    private byte[] buffer;
    private ByteBuffer byteBuffer;
    private ReadableByteChannel channel;

    @Setup(Level.Trial)
    public void setup() throws IOException {
        // 1. Create fixed payload
        byte[] payload = new byte[PAYLOAD_SIZE];
        for (int i = 0; i < PAYLOAD_SIZE; i++) {
            payload[i] = (byte) (i % 256);
        }

        // 2. Setup InputStream
        inputStream = new ByteArrayInputStream(payload);

        // 3. Setup byte array buffer
        buffer = new byte[PAYLOAD_SIZE];

        // 4. Setup ByteBuffer
        byteBuffer = ByteBuffer.wrap(payload);

        // 5. Setup ReadableByteChannel
        channel = new ReadableByteChannel() {
            private int position = 0;
            @Override
            public int read(ByteBuffer dst) throws IOException {
                if (position >= PAYLOAD_SIZE) {
                    return -1;
                }
                int remaining = PAYLOAD_SIZE - position;
                int toRead = Math.min(remaining, dst.remaining());
                byte[] temp = new byte[toRead];
                System.arraycopy(payload, position, temp, 0, toRead);
                dst.put(temp);
                position += toRead;
                return toRead;
            }
            @Override
            public boolean isOpen() { return true; }
            @Override
            public void close() throws IOException {}
        };
    }

    // --- Benchmarks for InputStream methods ---

    @Benchmark
    public long readFully_FullArray(Blackhole bh) throws IOException {
        // Reads all bytes into the buffer
        int bytesRead = IOUtils.readFully(inputStream, buffer);
        bh.consume(bytesRead);
        return bytesRead;
    }

    @Benchmark
    public long readFully_OffsetLength(Blackhole bh) throws IOException {
        // Reads a specific range
        int bytesRead = IOUtils.readFully(inputStream, buffer, 0, PAYLOAD_SIZE / 2);
        bh.consume(bytesRead);
        return bytesRead;
    }

    @Benchmark
    public long readRange_InputStream(Blackhole bh) throws IOException {
        // Reads a specific range and returns a new array
        byte[] result = IOUtils.readRange(inputStream, PAYLOAD_SIZE / 4);
        bh.consume(result);
        return result.length;
    }

    @Benchmark
    public long skip_InputStream(Blackhole bh) throws IOException {
        // Skips a large number of bytes
        long skipped = IOUtils.skip(inputStream, PAYLOAD_SIZE / 3);
        bh.consume(skipped);
        return skipped;
    }

    // --- Benchmarks for ReadableByteChannel methods ---

    @Benchmark
    public void readFully_Channel(Blackhole bh) throws IOException {
        // Reads all bytes from the channel into the buffer
        IOUtils.readFully(channel, byteBuffer);
        bh.consume(byteBuffer.position());
    }

    @Benchmark
    public void readRange_Channel(Blackhole bh) throws IOException {
        // Reads a specific range from the channel into a new array
        byte[] result = IOUtils.readRange(channel, PAYLOAD_SIZE / 5);
        bh.consume(result);
    }
}
