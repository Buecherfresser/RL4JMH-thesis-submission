package bench.generated.c042;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.compressors.brotli.BrotliCompressorInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class BrotliCompressorInputStreamBenchmark {

    private byte[] compressedData;

    @Setup
    public void setUp() {
        // Minimal valid Brotli stream for empty input (single meta‑block header)
        compressedData = new byte[] { 0x0b };
    }

    @Benchmark
    public int readSingleByte(Blackhole bh) throws IOException {
        try (BrotliCompressorInputStream in = new BrotliCompressorInputStream(
                new ByteArrayInputStream(compressedData))) {
            int v = in.read();
            bh.consume(v);
            return v;
        }
    }

    @Benchmark
    public int readByteArray(Blackhole bh) throws IOException {
        try (BrotliCompressorInputStream in = new BrotliCompressorInputStream(
                new ByteArrayInputStream(compressedData))) {
            byte[] buf = new byte[1024];
            int n = in.read(buf);
            bh.consume(n);
            return n;
        }
    }

    @Benchmark
    public int readByteArrayPartial(Blackhole bh) throws IOException {
        try (BrotliCompressorInputStream in = new BrotliCompressorInputStream(
                new ByteArrayInputStream(compressedData))) {
            byte[] buf = new byte[2048];
            int n = in.read(buf, 100, 500);
            bh.consume(n);
            return n;
        }
    }

    @Benchmark
    public long getCompressedCount(Blackhole bh) throws IOException {
        try (BrotliCompressorInputStream in = new BrotliCompressorInputStream(
                new ByteArrayInputStream(compressedData))) {
            in.read();
            long cnt = in.getCompressedCount();
            bh.consume(cnt);
            return cnt;
        }
    }

    @Benchmark
    public int available(Blackhole bh) throws IOException {
        try (BrotliCompressorInputStream in = new BrotliCompressorInputStream(
                new ByteArrayInputStream(compressedData))) {
            int av = in.available();
            bh.consume(av);
            return av;
        }
    }

    @Benchmark
    public boolean markSupported(Blackhole bh) throws IOException {
        try (BrotliCompressorInputStream in = new BrotliCompressorInputStream(
                new ByteArrayInputStream(compressedData))) {
            boolean ms = in.markSupported();
            bh.consume(ms);
            return ms;
        }
    }

    @Benchmark
    public long skip(Blackhole bh) throws IOException {
        try (BrotliCompressorInputStream in = new BrotliCompressorInputStream(
                new ByteArrayInputStream(compressedData))) {
            long skipped = in.skip(10);
            bh.consume(skipped);
            return skipped;
        }
    }

    @Benchmark
    public String toStringBenchmark(Blackhole bh) throws IOException {
        try (BrotliCompressorInputStream in = new BrotliCompressorInputStream(
                new ByteArrayInputStream(compressedData))) {
            String s = in.toString();
            bh.consume(s);
            return s;
        }
    }
}
