package bench.generated.c087;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.utils.CountingInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class CountingInputStreamBenchmark {

    // Fixed payload for reading operations
    private byte[] payload;
    private InputStream inputStream;
    private CountingInputStream countingInputStream;

    // Configuration for the test data size
    private static final int PAYLOAD_SIZE = 1024 * 10; // 10 KB

    @Setup(Level.Trial)
    public void setup() throws IOException {
        // 1. Create a fixed payload
        this.payload = new byte[PAYLOAD_SIZE];
        // Fill payload with some data (e.g., sequential bytes)
        for (int i = 0; i < PAYLOAD_SIZE; i++) {
            payload[i] = (byte) (i % 256);
        }

        // 2. Create the underlying input stream
        this.inputStream = new ByteArrayInputStream(payload);

        // 3. Create the CountingInputStream wrapper
        this.countingInputStream = new CountingInputStream(this.inputStream);
    }

    @Benchmark
    public void benchmarkReadSingleByte(Blackhole bh) throws IOException {
        // Test the basic read() method
        int result = countingInputStream.read();
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkReadBuffer(Blackhole bh) throws IOException {
        // Test the read(byte[] b) method
        byte[] buffer = new byte[1024];
        int result = countingInputStream.read(buffer);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkReadSegment(Blackhole bh) throws IOException {
        // Test the read(byte[] b, int off, int len) method
        int offset = 512;
        int length = 1024;
        byte[] buffer = new byte[length];
        int result = countingInputStream.read(buffer, offset, length);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkGetBytesRead(Blackhole bh) {
        // Test the getBytesRead() method
        long bytesRead = countingInputStream.getBytesRead();
        bh.consume(bytesRead);
    }
}
