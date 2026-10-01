package bench.generated.c085;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.CRC32;
import java.util.zip.Checksum;
import org.apache.commons.compress.utils.ChecksumVerifyingInputStream;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ChecksumVerifyingInputStreamBenchmark {

    private byte[] payload;
    private long expectedChecksum;
    private int size;

    @Setup
    public void setUp() {
        size = 1024;
        payload = new byte[size];
        for (int i = 0; i < size; i++) {
            payload[i] = (byte) (i & 0xFF);
        }
        Checksum checksum = new CRC32();
        checksum.update(payload, 0, size);
        expectedChecksum = checksum.getValue();
    }

    @Benchmark
    public int readSingleByte() throws IOException {
        InputStream base = new ByteArrayInputStream(payload);
        Checksum checksum = new CRC32();
        ChecksumVerifyingInputStream cvs = new ChecksumVerifyingInputStream(checksum, base, size, expectedChecksum);
        return cvs.read();
    }

    @Benchmark
    public int readFullArray() throws IOException {
        InputStream base = new ByteArrayInputStream(payload);
        Checksum checksum = new CRC32();
        ChecksumVerifyingInputStream cvs = new ChecksumVerifyingInputStream(checksum, base, size, expectedChecksum);
        byte[] buffer = new byte[size];
        return cvs.read(buffer, 0, buffer.length);
    }

    @Benchmark
    public long getBytesRemaining() throws IOException {
        InputStream base = new ByteArrayInputStream(payload);
        Checksum checksum = new CRC32();
        ChecksumVerifyingInputStream cvs = new ChecksumVerifyingInputStream(checksum, base, size, expectedChecksum);
        return cvs.getBytesRemaining();
    }
}
