package bench.generated.c083;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.utils.CRC32VerifyingInputStream;
import java.util.Random;
import java.util.zip.CRC32;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.IOException;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class CRC32VerifyingInputStreamBenchmark {

    private byte[] payload;
    private long expectedCrc;
    private int payloadSize;

    @Setup(Level.Trial)
    public void setUp() {
        payloadSize = 64 * 1024; // 64 KiB
        payload = new byte[payloadSize];
        Random rnd = new Random(12345L);
        rnd.nextBytes(payload);

        CRC32 crc = new CRC32();
        crc.update(payload, 0, payload.length);
        expectedCrc = crc.getValue();
    }

    @Benchmark
    public int readAllWithLongConstructor() throws IOException {
        InputStream base = new ByteArrayInputStream(payload);
        try (InputStream in = new CRC32VerifyingInputStream(base, payloadSize, expectedCrc)) {
            int total = 0;
            byte[] buf = new byte[8192];
            int n;
            while ((n = in.read(buf)) != -1) {
                total += n;
            }
            return total;
        }
    }

    @Benchmark
    public int readAllWithIntConstructor() throws IOException {
        InputStream base = new ByteArrayInputStream(payload);
        int expectedCrcInt = (int) expectedCrc;
        try (InputStream in = new CRC32VerifyingInputStream(base, payloadSize, expectedCrcInt)) {
            int total = 0;
            byte[] buf = new byte[8192];
            int n;
            while ((n = in.read(buf)) != -1) {
                total += n;
            }
            return total;
        }
    }

    @Benchmark
    public int readByteByByteLong() throws IOException {
        InputStream base = new ByteArrayInputStream(payload);
        try (InputStream in = new CRC32VerifyingInputStream(base, payloadSize, expectedCrc)) {
            int total = 0;
            while (in.read() != -1) {
                total += 1;
            }
            return total;
        }
    }

    @Benchmark
    public void consumeWithBlackhole(Blackhole bh) throws IOException {
        InputStream base = new ByteArrayInputStream(payload);
        try (InputStream in = new CRC32VerifyingInputStream(base, payloadSize, expectedCrc)) {
            byte[] buf = new byte[8192];
            int n;
            while ((n = in.read(buf)) != -1) {
                bh.consume(buf);
                bh.consume(n);
            }
        }
    }
}
