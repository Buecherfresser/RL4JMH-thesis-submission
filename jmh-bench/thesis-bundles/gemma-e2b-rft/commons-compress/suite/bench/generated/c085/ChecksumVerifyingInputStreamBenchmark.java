package bench.generated.c085;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.CRC32;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.utils.ChecksumVerifyingInputStream;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class ChecksumVerifyingInputStreamBenchmark {

    private ChecksumVerifyingInputStream verifyingInputStream;
    private final long payloadSize = 1024 * 1024; // 1MB payload
    private long expectedChecksum;

    @Setup
    public void setup() throws IOException {
        // 1. Create a representative payload (data)
        byte[] data = new byte[(int) payloadSize];
        for (int i = 0; i < payloadSize; i++) {
            data[i] = (byte) (i % 256);
        }

        // 2. Calculate the expected checksum using CRC32
        CRC32 crc = new CRC32();
        crc.update(data);
        expectedChecksum = crc.getValue();

        // 3. Wrap the data in an InputStream
        InputStream inputStream = new ByteArrayInputStream(data);

        // 4. Create the Checksum implementation (CRC32 implements java.util.zip.Checksum)
        java.util.zip.Checksum checksum = crc;

        // 5. Initialize the subject under test
        verifyingInputStream = new ChecksumVerifyingInputStream(
                checksum,
                inputStream,
                payloadSize,
                expectedChecksum
        );
    }

    @Benchmark
    public void benchmarkReadSingleByte(Blackhole bh) throws IOException {
        int result = verifyingInputStream.read();
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkReadBulk(Blackhole bh) throws IOException {
        byte[] buffer = new byte[4096];
        int bytesRead = verifyingInputStream.read(buffer, 0, buffer.length);
        bh.consume(bytesRead);
    }
}
