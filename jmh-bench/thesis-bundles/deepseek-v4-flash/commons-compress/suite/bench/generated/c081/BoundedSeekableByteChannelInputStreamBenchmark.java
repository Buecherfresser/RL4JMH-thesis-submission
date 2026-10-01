package bench.generated.c081;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import org.apache.commons.compress.utils.BoundedSeekableByteChannelInputStream;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class BoundedSeekableByteChannelInputStreamBenchmark {

    private static final int PAYLOAD_SIZE = 1 << 20; // 1 MiB
    private static final int SUB_START = 1024;
    private static final int SUB_REMAINING = PAYLOAD_SIZE - 2048;

    private byte[] payload;
    private SeekableByteChannel channel;

    @Setup(Level.Trial)
    public void setup() throws IOException {
        payload = new byte[PAYLOAD_SIZE];
        // deterministic pseudo-random pattern
        for (int i = 0; i < payload.length; i++) {
            payload[i] = (byte) (i * 31 + 7);
        }
        channel = new ByteArraySeekableByteChannel(payload);
    }

    @Benchmark
    public byte[] readEntireRange() throws IOException {
        BoundedSeekableByteChannelInputStream stream =
                new BoundedSeekableByteChannelInputStream(0, payload.length, channel);
        byte[] buffer = new byte[8192];
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream(payload.length);
        int n;
        while ((n = stream.read(buffer, 0, buffer.length)) != -1) {
            out.write(buffer, 0, n);
        }
        stream.close();
        return out.toByteArray();
    }

    @Benchmark
    public byte[] readSubRange() throws IOException {
        BoundedSeekableByteChannelInputStream stream =
                new BoundedSeekableByteChannelInputStream(SUB_START, SUB_REMAINING, channel);
        byte[] buffer = new byte[8192];
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream(SUB_REMAINING);
        int n;
        while ((n = stream.read(buffer, 0, buffer.length)) != -1) {
            out.write(buffer, 0, n);
        }
        stream.close();
        return out.toByteArray();
    }

    @Benchmark
    public byte[] readWithSmallBuffer() throws IOException {
        BoundedSeekableByteChannelInputStream stream =
                new BoundedSeekableByteChannelInputStream(0, payload.length, channel);
        byte[] buffer = new byte[256];
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream(payload.length);
        int n;
        while ((n = stream.read(buffer, 0, buffer.length)) != -1) {
            out.write(buffer, 0, n);
        }
        stream.close();
        return out.toByteArray();
    }

    private static class ByteArraySeekableByteChannel implements SeekableByteChannel {
        private final byte[] data;
        private int position;
        private boolean open = true;

        ByteArraySeekableByteChannel(byte[] data) {
            this.data = data;
        }

        @Override
        public int read(ByteBuffer dst) throws IOException {
            if (position >= data.length) {
                return -1;
            }
            int remaining = Math.min(dst.remaining(), data.length - position);
            dst.put(data, position, remaining);
            position += remaining;
            return remaining;
        }

        @Override
        public int write(ByteBuffer src) throws IOException {
            throw new UnsupportedOperationException();
        }

        @Override
        public long position() throws IOException {
            return position;
        }

        @Override
        public SeekableByteChannel position(long newPosition) throws IOException {
            if (newPosition < 0 || newPosition > data.length) {
                throw new IllegalArgumentException("Invalid position: " + newPosition);
            }
            this.position = (int) newPosition;
            return this;
        }

        @Override
        public long size() throws IOException {
            return data.length;
        }

        @Override
        public SeekableByteChannel truncate(long size) throws IOException {
            throw new UnsupportedOperationException();
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
