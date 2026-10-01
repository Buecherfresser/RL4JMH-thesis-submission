package bench.generated.c074;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.compressors.zstandard.ZstdCompressorInputStream;
import org.apache.commons.compress.compressors.zstandard.ZstdCompressorOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ZstdCompressorInputStreamBenchmark {

    private byte[] compressedData;
    private byte[] buffer;
    private ZstdCompressorInputStream nonConsumingStream;

    @Setup(Level.Trial)
    public void setUp() throws Exception {
        byte[] payload = new byte[65536];
        for (int i = 0; i < payload.length; i++) {
            payload[i] = (byte) (i * 31);
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZstdCompressorOutputStream zos = new ZstdCompressorOutputStream(baos)) {
            zos.write(payload);
        }
        compressedData = baos.toByteArray();
        buffer = new byte[8192];
        nonConsumingStream = new ZstdCompressorInputStream(new ByteArrayInputStream(compressedData));
    }

    @TearDown(Level.Trial)
    public void tearDown() throws Exception {
        nonConsumingStream.close();
    }

    @Benchmark
    public int readByte() throws Exception {
        try (ZstdCompressorInputStream in = new ZstdCompressorInputStream(new ByteArrayInputStream(compressedData))) {
            return in.read();
        }
    }

    @Benchmark
    public int readArray() throws Exception {
        try (ZstdCompressorInputStream in = new ZstdCompressorInputStream(new ByteArrayInputStream(compressedData))) {
            return in.read(buffer);
        }
    }

    @Benchmark
    public long skip() throws Exception {
        try (ZstdCompressorInputStream in = new ZstdCompressorInputStream(new ByteArrayInputStream(compressedData))) {
            return in.skip(1000);
        }
    }

    @Benchmark
    public int available() throws Exception {
        return nonConsumingStream.available();
    }

    @Benchmark
    public long getCompressedCount() throws Exception {
        return nonConsumingStream.getCompressedCount();
    }
}
