package bench.generated.c085;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.utils.ChecksumVerifyingInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.zip.Checksum;
import java.util.zip.CRC32;
import java.util.Arrays;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ChecksumVerifyingInputStreamBenchmark {

    private byte[] payload;
    private Checksum checksum;
    private long expectedChecksum;
    private static final int PAYLOAD_SIZE = 1024 * 10; // 10 KB payload

    @Setup(Level.Trial)
    public void setup() throws IOException {
        // 1. Create payload
        payload = new byte[PAYLOAD_SIZE];
        Arrays.fill(payload, (byte) 0xAA);

        // 2. Calculate expected checksum
        checksum = new CRC32();
        checksum.update(payload);
        expectedChecksum = checksum.getValue();
    }

    /**
     * Benchmarks reading a single byte from the stream.
     * The stream is recreated for each invocation to ensure state isolation.
     */
    @Benchmark
    public int readSingleByte() throws IOException {
        // Setup for invocation: Create fresh stream
        ByteArrayInputStream bais = new ByteArrayInputStream(payload);
        ChecksumVerifyingInputStream cvis = new ChecksumVerifyingInputStream(
                checksum, bais, PAYLOAD_SIZE, expectedChecksum);

        int result = cvis.read();
        
        // Consume result
        return result;
    }

    /**
     * Benchmarks reading a bulk chunk of bytes from the stream.
     * The stream is recreated for each invocation to ensure state isolation.
     */
    @Benchmark
    public int readBulkBytes() throws IOException {
        // Setup for invocation: Create fresh stream
        ByteArrayInputStream bais = new ByteArrayInputStream(payload);
        ChecksumVerifyingInputStream cvis = new ChecksumVerifyingInputStream(
                checksum, bais, PAYLOAD_SIZE, expectedChecksum);

        byte[] buffer = new byte[1024];
        int result = cvis.read(buffer, 0, buffer.length);

        // Consume result
        return result;
    }

    /**
     * Benchmarks checking the remaining bytes count after a partial read.
     * The stream is recreated and partially consumed for each invocation.
     */
    @Benchmark
    public long getBytesRemainingAfterPartialRead() throws IOException {
        // Setup for invocation: Create fresh stream
        ByteArrayInputStream bais = new ByteArrayInputStream(payload);
        ChecksumVerifyingInputStream cvis = new ChecksumVerifyingInputStream(
                checksum, bais, PAYLOAD_SIZE, expectedChecksum);

        // Perform a partial read (e.g., read 100 bytes)
        byte[] buffer = new byte[100];
        cvis.read(buffer, 0, 100);

        // Measure the getter call
        long remaining = cvis.getBytesRemaining();
        
        // Consume result
        return remaining;
    }
}
