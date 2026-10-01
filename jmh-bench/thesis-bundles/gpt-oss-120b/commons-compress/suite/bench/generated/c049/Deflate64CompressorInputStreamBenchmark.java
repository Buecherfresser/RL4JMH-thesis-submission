package bench.generated.c049;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.compressors.deflate64.Deflate64CompressorInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Deflate64CompressorInputStreamBenchmark {

    private byte[] compressedData;

    @Setup(Level.Trial)
    public void setUp() {
        // Minimal valid Deflate64 stream: empty stored block with BFINAL=1
        compressedData = new byte[] {
            0x01,               // BFINAL=1, BTYPE=00 (stored), padded to byte
            0x00, 0x00,         // LEN = 0
            (byte)0xFF, (byte)0xFF // NLEN = 0xFFFF (one's complement of LEN)
        };
    }

    @Benchmark
    public void decompressAll(Blackhole bh) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(compressedData);
        Deflate64CompressorInputStream in = new Deflate64CompressorInputStream(bais);
        byte[] buffer = new byte[8192];
        int n;
        while ((n = in.read(buffer)) != -1) {
            bh.consume(Arrays.copyOf(buffer, n));
        }
        bh.consume(in.getCompressedCount());
        in.close();
    }

    @Benchmark
    public void decompressSingleByte(Blackhole bh) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(compressedData);
        Deflate64CompressorInputStream in = new Deflate64CompressorInputStream(bais);
        int v;
        while ((v = in.read()) != -1) {
            bh.consume(v);
        }
        bh.consume(in.getCompressedCount());
        in.close();
    }

    @Benchmark
    public void decompressIntoBufferOnce(Blackhole bh) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(compressedData);
        Deflate64CompressorInputStream in = new Deflate64CompressorInputStream(bais);
        byte[] buf = new byte[1024];
        int n = in.read(buf, 0, buf.length);
        if (n > 0) {
            bh.consume(Arrays.copyOf(buf, n));
        }
        bh.consume(n);
        bh.consume(in.getCompressedCount());
        in.close();
    }
}
