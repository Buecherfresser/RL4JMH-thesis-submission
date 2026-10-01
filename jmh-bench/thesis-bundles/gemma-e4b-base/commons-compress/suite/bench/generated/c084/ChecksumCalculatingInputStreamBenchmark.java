package bench.generated.c084;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.utils.ChecksumCalculatingInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.zip.Checksum;
import java.util.zip.CRC32;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ChecksumCalculatingInputStreamBenchmark {

    private byte[] payload;
    private Checksum checksum;
    private InputStream inputStream;
    private ChecksumCalculatingInputStream sut;

    @Setup(Level.Trial)
    public void setup() {
        // 1. Create a representative payload (e.g., 1MB of data)
        int payloadSize = 1024 * 1024;
        payload = new byte[payloadSize];
        for (int i = 0; i < payloadSize; i++) {
            payload[i] = (byte) (i % 256);
        }

        // 2. Initialize the checksum object
        checksum = new CRC32();

        // 3. Initialize the input stream
        inputStream = new ByteArrayInputStream(payload);
    }

    @Setup(Level.Invocation)
    public void setupInvocation() {
        // Recreate the SUT and reset state for every invocation
        checksum.reset();
        inputStream = new ByteArrayInputStream(payload);
        sut = new ChecksumCalculatingInputStream(checksum, inputStream);
    }

    /**
     * Benchmarks the full read cycle of the ChecksumCalculatingInputStream
     * and the subsequent call to getValue().
     */
    @Benchmark
    public long benchmarkFullReadAndGetValue(Blackhole bh) throws java.io.IOException {
        // Read all data from the stream
        byte[] buffer = new byte[4096];
        int bytesRead;
        long totalBytesRead = 0;

        while ((bytesRead = sut.read(buffer, 0, buffer.length)) != -1) {
            totalBytesRead += bytesRead;
        }

        // Consume the result of getValue()
        long calculatedValue = sut.getValue();
        bh.consume(calculatedValue);
        return calculatedValue;
    }
}
