package bench.generated.c075;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.compressors.zstandard.ZstdCompressorOutputStream;
import org.apache.commons.compress.compressors.zstandard.ZstdCompressorOutputStream.Builder;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ZstdCompressorOutputStreamBenchmark {

    private byte[] payload;

    @Setup(Level.Trial)
    public void setUp() {
        payload = new byte[1024];
        new Random(0).nextBytes(payload);
    }

    @Benchmark
    public byte[] compressDefault() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZstdCompressorOutputStream zout = new ZstdCompressorOutputStream(baos);
        zout.write(payload, 0, payload.length);
        zout.close();
        return baos.toByteArray();
    }

    @Benchmark
    public byte[] compressLevel() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZstdCompressorOutputStream zout = new ZstdCompressorOutputStream(baos, 5);
        zout.write(payload, 0, payload.length);
        zout.close();
        return baos.toByteArray();
    }

    @Benchmark
    public byte[] compressLevelCloseFlush() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZstdCompressorOutputStream zout = new ZstdCompressorOutputStream(baos, 5, true);
        zout.write(payload, 0, payload.length);
        zout.close();
        return baos.toByteArray();
    }

    @Benchmark
    public byte[] compressLevelCloseFlushChecksum() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZstdCompressorOutputStream zout = new ZstdCompressorOutputStream(baos, 5, true, true);
        zout.write(payload, 0, payload.length);
        zout.close();
        return baos.toByteArray();
    }

    @Benchmark
    public byte[] compressBuilderCustom() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Builder builder = ZstdCompressorOutputStream.builder()
                .setOutputStream(baos)
                .setLevel(7)
                .setChecksum(true)
                .setWorkers(0)
                .setStrategy(1);
        ZstdCompressorOutputStream zout = builder.get();
        zout.write(payload, 0, payload.length);
        zout.close();
        return baos.toByteArray();
    }
}
