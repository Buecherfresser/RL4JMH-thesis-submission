package bench.generated.c092;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.utils.IOUtils;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.Channels;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class IOUtilsBenchmark {

    private byte[] data;
    private byte[] buffer;
    private ByteBuffer byteBuffer;

    @Setup(Level.Trial)
    public void setUp() {
        int size = 64 * 1024; // 64 KiB payload
        data = new byte[size];
        for (int i = 0; i < size; i++) {
            data[i] = (byte) (i & 0xFF);
        }
        buffer = new byte[size];
        byteBuffer = ByteBuffer.allocate(size);
    }

    @Benchmark
    public int readFullyInputStream() throws IOException {
        InputStream in = new ByteArrayInputStream(data);
        return IOUtils.readFully(in, buffer);
    }

    @Benchmark
    public int readFullyInputStreamOffset() throws IOException {
        InputStream in = new ByteArrayInputStream(data);
        return IOUtils.readFully(in, buffer, 0, buffer.length);
    }

    @Benchmark
    public void readFullyChannel(Blackhole bh) throws IOException {
        InputStream in = new ByteArrayInputStream(data);
        ReadableByteChannel channel = Channels.newChannel(in);
        byteBuffer.clear();
        IOUtils.readFully(channel, byteBuffer);
        bh.consume(byteBuffer.position());
        bh.consume(channel);
    }

    @Benchmark
    public byte[] readRangeInputStream() throws IOException {
        InputStream in = new ByteArrayInputStream(data);
        return IOUtils.readRange(in, data.length / 2);
    }

    @Benchmark
    public byte[] readRangeChannel() throws IOException {
        InputStream in = new ByteArrayInputStream(data);
        ReadableByteChannel channel = Channels.newChannel(in);
        return IOUtils.readRange(channel, data.length / 2);
    }

    @Benchmark
    public long skipInputStream() throws IOException {
        InputStream in = new ByteArrayInputStream(data);
        return IOUtils.skip(in, data.length / 2);
    }

    @Benchmark
    public byte[] toByteArray() throws IOException {
        InputStream in = new ByteArrayInputStream(data);
        return IOUtils.toByteArray(in);
    }
}
