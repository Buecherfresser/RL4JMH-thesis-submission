package bench.generated.c084;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.utils.ChecksumCalculatingInputStream;
import java.util.zip.Adler32;
import java.util.zip.Checksum;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.IOException;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ChecksumCalculatingInputStreamBenchmark {

    private byte[] payload;
    private static final int PAYLOAD_SIZE = 64 * 1024; // 64 KiB

    @Setup(Level.Trial)
    public void setUp() {
        payload = new byte[PAYLOAD_SIZE];
        for (int i = 0; i < payload.length; i++) {
            payload[i] = (byte) (i & 0xFF);
        }
    }

    private ChecksumCalculatingInputStream newStream() {
        Checksum checksum = new Adler32();
        InputStream base = new ByteArrayInputStream(payload);
        return new ChecksumCalculatingInputStream(checksum, base);
    }

    @Benchmark
    public long readSingleByte() throws IOException {
        ChecksumCalculatingInputStream in = newStream();
        while (in.read() != -1) {
            // consume byte
        }
        return in.getValue();
    }

    @Benchmark
    public long readByteArray() throws IOException {
        ChecksumCalculatingInputStream in = newStream();
        byte[] buffer = new byte[8192];
        while (in.read(buffer) != -1) {
            // consume buffer
        }
        return in.getValue();
    }

    @Benchmark
    public long readByteArrayWithOffset() throws IOException {
        ChecksumCalculatingInputStream in = newStream();
        byte[] buffer = new byte[8192];
        int offset = 0;
        int length = buffer.length;
        while (in.read(buffer, offset, length) != -1) {
            // consume buffer
        }
        return in.getValue();
    }

    @Benchmark
    public long readAndConsumeWithBlackhole(Blackhole bh) throws IOException {
        ChecksumCalculatingInputStream in = newStream();
        byte[] buffer = new byte[8192];
        int n;
        while ((n = in.read(buffer)) != -1) {
            bh.consume(buffer);
            bh.consume(n);
        }
        long checksum = in.getValue();
        bh.consume(checksum);
        return checksum;
    }
}
