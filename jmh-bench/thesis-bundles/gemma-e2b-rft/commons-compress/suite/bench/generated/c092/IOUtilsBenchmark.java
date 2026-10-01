package bench.generated.c092;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.output.NullOutputStream;
import org.apache.commons.compress.utils.IOUtils;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.Channels;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class IOUtilsBenchmark {

    private byte[] testData;
    private InputStream inputStream;
    private ByteArrayOutputStream outputStream;
    private ReadableByteChannel byteChannel;

    private static final int DATA_SIZE = 1024 * 1024 * 4; // 4MB payload

    @Setup(Level.Trial)
    public void setup() throws IOException {
        // 1. Prepare fixed payload
        testData = new byte[DATA_SIZE];
        // Fill with some data (e.g., sequential bytes)
        for (int i = 0; i < DATA_SIZE; i++) {
            testData[i] = (byte) (i % 256);
        }

        // 2. Prepare streams/channels for reuse
        inputStream = new ByteArrayInputStream(testData);
        outputStream = new ByteArrayOutputStream();
        byteChannel = null; // Initialize to null, will be set in specific benchmarks
    }

    @Benchmark
    public void benchmarkCopyFullStream(Blackhole bh) throws IOException {
        // Test IOUtils.copy(InputStream, OutputStream)
        IOUtils.copy(inputStream, outputStream);
        bh.consume(outputStream.toByteArray());
    }

    @Benchmark
    public void benchmarkCopyRange(Blackhole bh) throws IOException {
        // Test IOUtils.copyRange(InputStream, long len, OutputStream)
        long length = DATA_SIZE / 2;
        IOUtils.copyRange(inputStream, length, outputStream);
        bh.consume(outputStream.toByteArray());
    }

    @Benchmark
    public void benchmarkReadFully(Blackhole bh) throws IOException {
        // Test IOUtils.readFully(InputStream, byte[])
        byte[] buffer = new byte[DATA_SIZE];
        int bytesRead = IOUtils.readFully(inputStream, buffer);
        bh.consume(bytesRead);
    }

    @Benchmark
    public void benchmarkReadFullyPartial(Blackhole bh) throws IOException {
        // Test IOUtils.readFully(InputStream, byte[], offset, length)
        byte[] buffer = new byte[DATA_SIZE];
        int offset = DATA_SIZE / 4;
        int length = DATA_SIZE / 8;
        int bytesRead = IOUtils.readFully(inputStream, buffer, offset, length);
        bh.consume(bytesRead);
    }

    @Benchmark
    public void benchmarkReadRange(Blackhole bh) throws IOException {
        // Test IOUtils.readRange(InputStream, int length)
        int length = DATA_SIZE / 2;
        byte[] result = IOUtils.readRange(inputStream, length);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkSkip(Blackhole bh) throws IOException {
        // Test IOUtils.skip(InputStream, long toSkip)
        long skipAmount = DATA_SIZE / 4;
        long bytesSkipped = IOUtils.skip(inputStream, skipAmount);
        bh.consume(bytesSkipped);
    }

    @Benchmark
    public void benchmarkChannelReadRange(Blackhole bh) throws IOException {
        // Test IOUtils.readRange(ReadableByteChannel, int length)
        
        // Create a new stream for channel test to ensure proper channel setup.
        ByteArrayInputStream channelInput = new ByteArrayInputStream(testData);
        ReadableByteChannel channel = Channels.newChannel(channelInput);

        int length = DATA_SIZE / 4;
        byte[] result = IOUtils.readRange(channel, length);
        bh.consume(result);
    }
}
