package bench.generated.c083;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.utils.CRC32VerifyingInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.zip.CRC32;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class CRC32VerifyingInputStreamBenchmark {

    private InputStream inputStream;
    private long expectedCrc32;
    private long payloadSize;

    @Setup(Level.Trial)
    public void setup() {
        // 1. Create a fixed payload (e.g., 4KB)
        byte[] payload = new byte[4096];
        for (int i = 0; i < payload.length; i++) {
            payload[i] = (byte) (i % 256);
        }

        // 2. Calculate the actual CRC32 for the payload
        CRC32 crc = new CRC32();
        crc.update(payload);
        long actualCrc = crc.getValue();

        // 3. Set up the input stream and parameters
        this.inputStream = new ByteArrayInputStream(payload);
        this.payloadSize = payload.length;
        this.expectedCrc32 = actualCrc;
    }

    /**
     * Benchmarks the overhead of wrapping an InputStream with CRC32VerifyingInputStream
     * and reading the entire content.
     */
    @Benchmark
    public void benchmarkReadAndVerify(Blackhole bh) throws Exception {
        // Instantiate the SUT
        CRC32VerifyingInputStream verifyingStream = new CRC32VerifyingInputStream(
                inputStream,
                payloadSize,
                expectedCrc32
        );

        // Drain the stream completely to force CRC calculation and verification
        byte[] buffer = new byte[1024];
        int bytesRead;
        long totalBytesRead = 0;

        while ((bytesRead = verifyingStream.read(buffer)) != -1) {
            bh.consume(bytesRead);
            totalBytesRead += bytesRead;
        }
        
        // Ensure the stream is fully consumed and verification happens (though the constructor handles the setup)
        // We consume the final result/state change if possible, but here we rely on the loop draining the stream.
        bh.consume(totalBytesRead);
    }
}
