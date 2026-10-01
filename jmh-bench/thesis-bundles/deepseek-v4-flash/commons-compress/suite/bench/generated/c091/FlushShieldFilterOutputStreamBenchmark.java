package bench.generated.c091;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.apache.commons.compress.utils.FlushShieldFilterOutputStream;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class FlushShieldFilterOutputStreamBenchmark {

    private byte[] payload;
    private FlushShieldFilterOutputStream flushStream;

    @Setup(Level.Trial)
    public void setup() {
        payload = new byte[1024];
        for (int i = 0; i < payload.length; i++) {
            payload[i] = (byte) i;
        }
        flushStream = new FlushShieldFilterOutputStream(new ByteArrayOutputStream());
    }

    @Benchmark
    public void flush(Blackhole bh) throws IOException {
        flushStream.flush();
        bh.consume(flushStream);
    }

    @Benchmark
    public int write() throws IOException {
        FlushShieldFilterOutputStream out = new FlushShieldFilterOutputStream(new ByteArrayOutputStream());
        out.write(payload, 0, payload.length);
        return payload.length;
    }
}
