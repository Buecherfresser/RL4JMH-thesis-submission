package bench.generated.c068;

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
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.apache.commons.compress.compressors.CompressorOutputStream;
import org.apache.commons.compress.compressors.snappy.SnappyCompressorInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class SnappyCompressorInputStreamBenchmark {

    private byte[] original;
    private byte[] compressed;

    @Setup
    public void setUp() throws IOException {
        int size = 64 * 1024; // 64 KiB deterministic payload
        original = new byte[size];
        for (int i = 0; i < size; i++) {
            original[i] = (byte) (i & 0xFF);
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        CompressorOutputStream cos = new CompressorStreamFactory()
                .createCompressorOutputStream(CompressorStreamFactory.SNAPPY_RAW, baos);
        cos.write(original);
        cos.close();
        compressed = baos.toByteArray();
    }

    @Benchmark
    public int readSingleByte() throws IOException {
        try (SnappyCompressorInputStream in = new SnappyCompressorInputStream(
                new ByteArrayInputStream(compressed))) {
            return in.read();
        }
    }

    @Benchmark
    public byte[] readFullBuffer() throws IOException {
        SnappyCompressorInputStream in = new SnappyCompressorInputStream(
                new ByteArrayInputStream(compressed));
        byte[] out = new byte[original.length];
        int read = in.read(out, 0, out.length);
        in.close();
        // Ensure the read succeeded; JMH will consume the returned array
        return out;
    }

    @Benchmark
    public int readPartialBuffer() throws IOException {
        SnappyCompressorInputStream in = new SnappyCompressorInputStream(
                new ByteArrayInputStream(compressed));
        byte[] buf = new byte[1024];
        int read = in.read(buf, 0, buf.length);
        in.close();
        return read;
    }

    @Benchmark
    public int readWithOffset() throws IOException {
        SnappyCompressorInputStream in = new SnappyCompressorInputStream(
                new ByteArrayInputStream(compressed));
        byte[] buf = new byte[original.length + 10];
        int read = in.read(buf, 5, original.length);
        in.close();
        return read;
    }

    @Benchmark
    public int getSize() throws IOException {
        SnappyCompressorInputStream in = new SnappyCompressorInputStream(
                new ByteArrayInputStream(compressed));
        int size = in.getSize();
        in.close();
        return size;
    }
}
