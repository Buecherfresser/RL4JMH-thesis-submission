package bench.generated.c083;

import org.apache.commons.compress.utils.CRC32VerifyingInputStream;
import org.openjdk.jmh.annotations.*;

import java.io.ByteArrayInputStream;
import java.util.concurrent.TimeUnit;
import java.util.zip.CRC32;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class CRC32VerifyingInputStreamBenchmark {

    private byte[] payload;
    private long expectedCrc;
    private int size;

    @Setup
    public void setup() {
        // 1 KiB of deterministic data
        size = 1024;
        payload = new byte[size];
        for (int i = 0; i < size; i++) {
            payload[i] = (byte) (i * 31 + 7);
        }
        CRC32 crc = new CRC32();
        crc.update(payload);
        expectedCrc = crc.getValue();
    }

    @Benchmark
    public int readBuffer() throws Exception {
        CRC32VerifyingInputStream in =
                new CRC32VerifyingInputStream(new ByteArrayInputStream(payload), size, expectedCrc);
        byte[] buffer = new byte[8192];
        int total = 0;
        int n;
        while ((n = in.read(buffer)) != -1) {
            total += n;
        }
        in.close();
        return total;
    }

    @Benchmark
    public int readByteByByte() throws Exception {
        CRC32VerifyingInputStream in =
                new CRC32VerifyingInputStream(new ByteArrayInputStream(payload), size, expectedCrc);
        int total = 0;
        int b;
        while ((b = in.read()) != -1) {
            total++;
        }
        in.close();
        return total;
    }
}
