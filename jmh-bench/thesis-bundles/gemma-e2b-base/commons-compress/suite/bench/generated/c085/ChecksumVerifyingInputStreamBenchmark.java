package bench.generated.c085;

import org.apache.commons.compress.utils.ChecksumVerifyingInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.zip.CRC32;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class ChecksumVerifyingInputStreamBenchmark {

    private ByteArrayInputStream inputStream;
    private CRC32 verifyingChecksum;
    private byte[] payload;
    private long payloadSize;
    private long expectedChecksum;
    private ChecksumVerifyingInputStream verifyingInputStream;

    private static final int PAYLOAD_SIZE = 1024 * 1024; // 1 MB

    @Setup
    public void setup() throws IOException {
        // 1. Create a fixed payload
        payload = new byte[PAYLOAD_SIZE];
        // Fill payload with some data (e.g., sequential bytes)
        for (int i = 0; i < PAYLOAD_SIZE; i++) {
            payload[i] = (byte) (i % 256);
        }

        // 2. Calculate the expected checksum (using CRC32 as a standard Checksum implementation)
        CRC32 crc = new CRC32();
        crc.update(payload);
        expectedChecksum = crc.getValue();
        verifyingChecksum = crc;

        // 3. Create the input stream
        inputStream = new ByteArrayInputStream(payload);

        // 4. Initialize the ChecksumVerifyingInputStream
        verifyingInputStream = new ChecksumVerifyingInputStream(
                verifyingChecksum,
                inputStream,
                PAYLOAD_SIZE,
                expectedChecksum
        );
    }

    @Benchmark
    public void readSingleByte(Blackhole bh) throws IOException {
        int result = verifyingInputStream.read();
        bh.consume(result);
    }

    @Benchmark
    public void readBulk(Blackhole bh) throws IOException {
        byte[] buffer = new byte[1024];
        int result = verifyingInputStream.read(buffer, 0, buffer.length);
        bh.consume(result);
    }
}
