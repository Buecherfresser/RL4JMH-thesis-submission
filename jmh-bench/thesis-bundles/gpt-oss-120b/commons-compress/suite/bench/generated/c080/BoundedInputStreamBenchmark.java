package bench.generated.c080;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Warmup;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.utils.BoundedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class BoundedInputStreamBenchmark {

    private byte[] payload;
    private int chunkSize;

    @Setup
    public void setUp() {
        payload = new byte[64 * 1024];
        for (int i = 0; i < payload.length; i++) {
            payload[i] = (byte) (i & 0xFF);
        }
        chunkSize = 1024;
    }

    @Benchmark
    public int readSingleByte() throws IOException {
        ByteArrayInputStream base = new ByteArrayInputStream(payload);
        BoundedInputStream bis = new BoundedInputStream(base, payload.length);
        return bis.read();
    }

    @Benchmark
    public int readBulk() throws IOException {
        ByteArrayInputStream base = new ByteArrayInputStream(payload);
        BoundedInputStream bis = new BoundedInputStream(base, payload.length);
        byte[] buffer = new byte[chunkSize];
        return bis.read(buffer, 0, buffer.length);
    }

    @Benchmark
    public long getBytesRemaining() {
        ByteArrayInputStream base = new ByteArrayInputStream(payload);
        BoundedInputStream bis = new BoundedInputStream(base, payload.length);
        return bis.getBytesRemaining();
    }
}
