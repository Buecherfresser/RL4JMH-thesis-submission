package bench.generated.c074;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.compressors.zstandard.ZstdCompressorInputStream;
import org.apache.commons.compress.compressors.zstandard.ZstdCompressorOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ZstdCompressorInputStreamBenchmark {

    private byte[] rawData;
    private byte[] compressedData;

    @Setup
    public void setup() throws IOException {
        // deterministic payload (~64 KiB)
        int size = 64 * 1024;
        rawData = new byte[size];
        new Random(0x1234abcdL).nextBytes(rawData);

        // compress using the public ZstdCompressorOutputStream
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZstdCompressorOutputStream zos = new ZstdCompressorOutputStream(baos)) {
            zos.write(rawData);
        }
        compressedData = baos.toByteArray();
    }

    @Benchmark
    public byte[] readAll() throws IOException {
        ZstdCompressorInputStream zis = new ZstdCompressorInputStream(new ByteArrayInputStream(compressedData));
        byte[] out = new byte[rawData.length];
        int read = zis.read(out);
        zis.close();
        if (read != rawData.length) {
            throw new IllegalStateException("Unexpected read length: " + read);
        }
        return out;
    }

    @Benchmark
    public int available() throws IOException {
        ZstdCompressorInputStream zis = new ZstdCompressorInputStream(new ByteArrayInputStream(compressedData));
        int av = zis.available();
        zis.close();
        return av;
    }

    @Benchmark
    public long getCompressedCount() throws IOException {
        ZstdCompressorInputStream zis = new ZstdCompressorInputStream(new ByteArrayInputStream(compressedData));
        long cnt = zis.getCompressedCount();
        zis.close();
        return cnt;
    }

    @Benchmark
    public boolean markSupported() throws IOException {
        ZstdCompressorInputStream zis = new ZstdCompressorInputStream(new ByteArrayInputStream(compressedData));
        boolean ms = zis.markSupported();
        zis.close();
        return ms;
    }

    @Benchmark
    public long skipZero() throws IOException {
        ZstdCompressorInputStream zis = new ZstdCompressorInputStream(new ByteArrayInputStream(compressedData));
        long skipped = zis.skip(0);
        zis.close();
        return skipped;
    }
}
