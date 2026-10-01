package bench.generated.c087;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.utils.CountingInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class CountingInputStreamBenchmark {

    private byte[] payload;
    private int payloadSize;

    @Setup
    public void setUp() {
        payloadSize = 4096; // 4 KiB payload
        payload = new byte[payloadSize];
        for (int i = 0; i < payloadSize; i++) {
            payload[i] = (byte) (i & 0xFF);
        }
    }

    @Benchmark
    public int readSingleByte() throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(payload);
        CountingInputStream cis = new CountingInputStream(bais);
        return cis.read();
    }

    @Benchmark
    public int readFullBuffer() throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(payload);
        CountingInputStream cis = new CountingInputStream(bais);
        byte[] buffer = new byte[payloadSize];
        return cis.read(buffer);
    }

    @Benchmark
    public int readPartialBuffer() throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(payload);
        CountingInputStream cis = new CountingInputStream(bais);
        int partialSize = payloadSize / 2;
        byte[] buffer = new byte[partialSize];
        return cis.read(buffer, 0, partialSize);
    }

    @Benchmark
    public int readZeroLength() throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(payload);
        CountingInputStream cis = new CountingInputStream(bais);
        byte[] buffer = new byte[10];
        return cis.read(buffer, 0, 0);
    }

    @Benchmark
    public long getBytesReadEmpty() {
        ByteArrayInputStream bais = new ByteArrayInputStream(payload);
        CountingInputStream cis = new CountingInputStream(bais);
        return cis.getBytesRead();
    }

    @Benchmark
    public void readAndConsumeWithBlackhole(Blackhole bh) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(payload);
        CountingInputStream cis = new CountingInputStream(bais);
        byte[] buffer = new byte[256];
        int read = cis.read(buffer);
        bh.consume(read);
        bh.consume(cis.getBytesRead());
    }
}
