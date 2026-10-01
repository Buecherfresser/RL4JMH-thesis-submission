package bench.generated.c091;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.utils.FlushShieldFilterOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class FlushShieldFilterOutputStreamBenchmark {

    private byte[] payload;

    @Setup
    public void setUp() {
        payload = new byte[1024];
        new Random(12345L).nextBytes(payload);
    }

    @Benchmark
    public int writeByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        FlushShieldFilterOutputStream out = new FlushShieldFilterOutputStream(baos);
        out.write(payload);
        out.close();
        return baos.size();
    }

    @Benchmark
    public int writeSingleByte() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        FlushShieldFilterOutputStream out = new FlushShieldFilterOutputStream(baos);
        out.write(payload[0] & 0xFF);
        out.close();
        return baos.size();
    }

    @Benchmark
    public void flush(Blackhole bh) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        FlushShieldFilterOutputStream out = new FlushShieldFilterOutputStream(baos);
        out.flush();
        bh.consume(baos);
    }

    @Benchmark
    public void close(Blackhole bh) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        FlushShieldFilterOutputStream out = new FlushShieldFilterOutputStream(baos);
        out.close();
        bh.consume(baos);
    }
}
