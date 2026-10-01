package bench.generated.c086;

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
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.utils.CloseShieldFilterInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.IOException;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class CloseShieldFilterInputStreamBenchmark {

    private byte[] payload;
    private byte[] buffer;

    @Setup(Level.Trial)
    public void setUp() {
        payload = new byte[1024];
        for (int i = 0; i < payload.length; i++) {
            payload[i] = (byte) (i & 0xFF);
        }
        buffer = new byte[256];
    }

    @Benchmark
    public int readSingleByte() throws IOException {
        InputStream in = new CloseShieldFilterInputStream(new ByteArrayInputStream(payload));
        return in.read();
    }

    @Benchmark
    public int readIntoBuffer() throws IOException {
        InputStream in = new CloseShieldFilterInputStream(new ByteArrayInputStream(payload));
        return in.read(buffer, 0, buffer.length);
    }

    @Benchmark
    public void closeOnly(Blackhole bh) throws IOException {
        InputStream in = new CloseShieldFilterInputStream(new ByteArrayInputStream(payload));
        in.close();
        bh.consume(in);
    }

    @Benchmark
    public int closeThenRead() throws IOException {
        InputStream in = new CloseShieldFilterInputStream(new ByteArrayInputStream(payload));
        in.close();
        return in.read();
    }
}
