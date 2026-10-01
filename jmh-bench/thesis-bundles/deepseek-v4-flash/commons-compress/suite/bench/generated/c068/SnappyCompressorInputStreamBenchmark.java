package bench.generated.c068;

import org.openjdk.jmh.annotations.*;
import java.util.concurrent.TimeUnit;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.apache.commons.compress.compressors.snappy.SnappyCompressorInputStream;
import org.apache.commons.compress.compressors.snappy.SnappyCompressorOutputStream;
import org.apache.commons.compress.utils.IOUtils;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class SnappyCompressorInputStreamBenchmark {

    private byte[] compressed;
    private byte[] chunkBuffer;

    @Setup(Level.Trial)
    public void setUp() throws IOException {
        String base = "The quick brown fox jumps over the lazy dog. ";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 2000; i++) {
            sb.append(base).append(i).append('\n');
        }
        byte[] payload = sb.toString().getBytes(StandardCharsets.UTF_8);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (SnappyCompressorOutputStream sos = new SnappyCompressorOutputStream(baos, payload.length)) {
            sos.write(payload);
        }
        compressed = baos.toByteArray();
        chunkBuffer = new byte[8192];
    }

    @Benchmark
    public byte[] readFully() throws IOException {
        try (SnappyCompressorInputStream in = new SnappyCompressorInputStream(new ByteArrayInputStream(compressed))) {
            return IOUtils.toByteArray(in);
        }
    }

    @Benchmark
    public byte[] readFullyLargeBlock() throws IOException {
        try (SnappyCompressorInputStream in = new SnappyCompressorInputStream(new ByteArrayInputStream(compressed), 65536)) {
            return IOUtils.toByteArray(in);
        }
    }

    @Benchmark
    public int readSingleByte() throws IOException {
        try (SnappyCompressorInputStream in = new SnappyCompressorInputStream(new ByteArrayInputStream(compressed))) {
            return in.read();
        }
    }

    @Benchmark
    public int readChunk() throws IOException {
        try (SnappyCompressorInputStream in = new SnappyCompressorInputStream(new ByteArrayInputStream(compressed))) {
            return in.read(chunkBuffer, 0, chunkBuffer.length);
        }
    }

    @Benchmark
    public int getSize() throws IOException {
        try (SnappyCompressorInputStream in = new SnappyCompressorInputStream(new ByteArrayInputStream(compressed))) {
            return in.getSize();
        }
    }

    @Benchmark
    public long getBytesRead() throws IOException {
        try (SnappyCompressorInputStream in = new SnappyCompressorInputStream(new ByteArrayInputStream(compressed))) {
            return in.getBytesRead();
        }
    }
}
