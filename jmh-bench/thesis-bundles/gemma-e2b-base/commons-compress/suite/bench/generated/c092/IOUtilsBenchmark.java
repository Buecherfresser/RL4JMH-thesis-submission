package bench.generated.c092;

import org.apache.commons.compress.utils.IOUtils;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class IOUtilsBenchmark {

    private static final int BUFFER_SIZE = 8192;
    private byte[] inputData;
    private InputStream inputStream;
    private ByteArrayOutputStream outputStream;
    private ReadableByteChannel channel;

    @Setup
    public void setup() throws IOException {
        // Create a large, fixed payload for testing
        int dataSize = 1024 * 1024 * 4; // 4MB
        inputData = new byte[dataSize];
        // Fill with some predictable data
        for (int i = 0; i < dataSize; i++) {
            inputData[i] = (byte) (i % 256);
        }

        inputStream = new ByteArrayInputStream(inputData);
        outputStream = new ByteArrayOutputStream();
        channel = Channels.newChannel(inputStream);
    }

    @Benchmark
    public void benchmarkCopyInputStreamToOutputStream(Blackhole bh) throws IOException {
        // Test IOUtils.copy(InputStream input, OutputStream output)
        long bytesCopied = IOUtils.copy(inputStream, outputStream);
        bh.consume(bytesCopied);
    }

    @Benchmark
    public void benchmarkReadFullyInputStream(Blackhole bh) throws IOException {
        // Test IOUtils.readFully(InputStream input, byte[] array)
        byte[] buffer = new byte[BUFFER_SIZE];
        int bytesRead = IOUtils.readFully(inputStream, buffer);
        bh.consume(bytesRead);
    }

    @Benchmark
    public void benchmarkReadRangeInputStream(Blackhole bh) throws IOException {
        // Test IOUtils.readRange(InputStream input, int length)
        int lengthToRead = BUFFER_SIZE / 2;
        byte[] result = IOUtils.readRange(inputStream, lengthToRead);
        bh.consume(result.length);
    }

    @Benchmark
    public void benchmarkSkipInputStream(Blackhole bh) throws IOException {
        // Test IOUtils.skip(InputStream input, long toSkip)
        long bytesSkipped = IOUtils.skip(inputStream, 1024 * 1024); // Skip 1MB
        bh.consume(bytesSkipped);
    }

    @Benchmark
    public void benchmarkReadRangeChannel(Blackhole bh) throws IOException {
        // Test IOUtils.readRange(ReadableByteChannel input, int length)
        int lengthToRead = BUFFER_SIZE;
        byte[] result = IOUtils.readRange(channel, lengthToRead);
        bh.consume(result.length);
    }
}
