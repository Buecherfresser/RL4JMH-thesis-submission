package bench.generated.c066;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.compressors.snappy.FramedSnappyCompressorInputStream;
import org.apache.commons.compress.compressors.snappy.FramedSnappyCompressorOutputStream;
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
public class FramedSnappyCompressorInputStreamBenchmark {

    private static final int PAYLOAD_SIZE = 64 * 1024; // 64 KiB

    private byte[] rawData;
    private byte[] compressedData;

    @Setup(Level.Trial)
    public void setUp() throws IOException {
        Random rnd = new Random(0x1234);
        rawData = new byte[PAYLOAD_SIZE];
        rnd.nextBytes(rawData);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (FramedSnappyCompressorOutputStream out = new FramedSnappyCompressorOutputStream(baos)) {
            out.write(rawData);
            out.finish();
        }
        compressedData = baos.toByteArray();
    }

    @Benchmark
    public int readSingleByte() throws IOException {
        FramedSnappyCompressorInputStream in = new FramedSnappyCompressorInputStream(
                new ByteArrayInputStream(compressedData));
        int result = in.read();
        in.close();
        return result;
    }

    @Benchmark
    public int readFullArray() throws IOException {
        byte[] buffer = new byte[rawData.length];
        FramedSnappyCompressorInputStream in = new FramedSnappyCompressorInputStream(
                new ByteArrayInputStream(compressedData));
        int read = in.read(buffer, 0, buffer.length);
        in.close();
        return read;
    }

    @Benchmark
    public int availableBytes() throws IOException {
        FramedSnappyCompressorInputStream in = new FramedSnappyCompressorInputStream(
                new ByteArrayInputStream(compressedData));
        int avail = in.available();
        in.close();
        return avail;
    }

    @Benchmark
    public long getCompressedCountAfterRead() throws IOException {
        FramedSnappyCompressorInputStream in = new FramedSnappyCompressorInputStream(
                new ByteArrayInputStream(compressedData));
        // read a small portion to advance the stream
        in.read();
        long count = in.getCompressedCount();
        in.close();
        return count;
    }

    @Benchmark
    public boolean matchesSignature() {
        return FramedSnappyCompressorInputStream.matches(compressedData, compressedData.length);
    }

    @Benchmark
    public void readAllAndConsume(Blackhole bh) throws IOException {
        byte[] buffer = new byte[8192];
        FramedSnappyCompressorInputStream in = new FramedSnappyCompressorInputStream(
                new ByteArrayInputStream(compressedData));
        int n;
        while ((n = in.read(buffer, 0, buffer.length)) != -1) {
            bh.consume(n);
        }
        in.close();
    }
}
