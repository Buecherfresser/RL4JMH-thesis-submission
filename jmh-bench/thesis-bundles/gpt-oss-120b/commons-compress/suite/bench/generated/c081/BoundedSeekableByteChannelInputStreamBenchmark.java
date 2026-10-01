package bench.generated.c081;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.utils.BoundedSeekableByteChannelInputStream;
import java.nio.channels.SeekableByteChannel;
import java.nio.ByteBuffer;
import java.io.IOException;
import java.util.Random;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class BoundedSeekableByteChannelInputStreamBenchmark {

    private static final int PAYLOAD_SIZE = 256 * 1024; // 256 KiB
    private byte[] payload;
    private InMemorySeekableByteChannel channel;

    @Setup(Level.Trial)
    public void setUp() {
        payload = new byte[PAYLOAD_SIZE];
        new Random(0x1234).nextBytes(payload);
        channel = new InMemorySeekableByteChannel(payload);
    }

    @Benchmark
    public int benchmarkReadSingleByte() throws IOException {
        BoundedSeekableByteChannelInputStream stream =
                new BoundedSeekableByteChannelInputStream(0, payload.length, channel);
        return stream.read();
    }

    @Benchmark
    public int benchmarkReadIntoBuffer() throws IOException {
        BoundedSeekableByteChannelInputStream stream =
                new BoundedSeekableByteChannelInputStream(0, payload.length, channel);
        byte[] buf = new byte[payload.length];
        return stream.read(buf, 0, buf.length);
    }

    // Simple in‑memory SeekableByteChannel backed by a byte array.
    private static class InMemorySeekableByteChannel implements SeekableByteChannel {
        private final ByteBuffer buffer;
        private boolean open = true;

        InMemorySeekableByteChannel(byte[] data) {
            this.buffer = ByteBuffer.wrap(data);
        }

        @Override
        public int read(ByteBuffer dst) throws IOException {
            if (!open) {
                throw new IOException("Channel closed");
            }
            if (!buffer.hasRemaining()) {
                return -1;
            }
            int toRead = Math.min(dst.remaining(), buffer.remaining());
            // Slice the source buffer to transfer bytes
            ByteBuffer srcSlice = buffer.slice();
            srcSlice.limit(toRead);
            dst.put(srcSlice);
            buffer.position(buffer.position() + toRead);
            return toRead;
        }

        @Override
        public int write(ByteBuffer src) throws IOException {
            throw new UnsupportedOperationException("Write not supported");
        }

        @Override
        public long position() throws IOException {
            return buffer.position();
        }

        @Override
        public SeekableByteChannel position(long newPosition) throws IOException {
            if (newPosition < 0 || newPosition > buffer.limit()) {
                throw new IllegalArgumentException("Invalid position");
            }
            buffer.position((int) newPosition);
            return this;
        }

        @Override
        public long size() throws IOException {
            return buffer.limit();
        }

        @Override
        public SeekableByteChannel truncate(long size) throws IOException {
            throw new UnsupportedOperationException("Truncate not supported");
        }

        @Override
        public boolean isOpen() {
            return open;
        }

        @Override
        public void close() throws IOException {
            open = false;
        }
    }
}
