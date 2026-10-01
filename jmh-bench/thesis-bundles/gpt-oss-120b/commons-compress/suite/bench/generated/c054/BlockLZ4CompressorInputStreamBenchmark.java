package bench.generated.c054;

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
import java.util.concurrent.TimeUnit;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.apache.commons.compress.compressors.lz4.BlockLZ4CompressorInputStream;
import org.apache.commons.compress.compressors.lz4.BlockLZ4CompressorOutputStream;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class BlockLZ4CompressorInputStreamBenchmark {

    private byte[] originalPayload;
    private byte[] compressedPayload;

    @Setup
    public void setUp() throws IOException {
        // Build a deterministic payload (e.g., repeating pattern)
        int size = 1024; // 1 KiB
        originalPayload = new byte[size];
        for (int i = 0; i < size; i++) {
            originalPayload[i] = (byte) (i & 0xFF);
        }

        // Compress using the matching BlockLZ4CompressorOutputStream
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (BlockLZ4CompressorOutputStream lz4Out = new BlockLZ4CompressorOutputStream(baos)) {
            lz4Out.write(originalPayload);
            lz4Out.finish();
        }
        compressedPayload = baos.toByteArray();
    }

    private BlockLZ4CompressorInputStream newInputStream() throws IOException {
        return new BlockLZ4CompressorInputStream(new ByteArrayInputStream(compressedPayload));
    }

    @Benchmark
    public int readSmallBuffer() throws IOException {
        BlockLZ4CompressorInputStream in = newInputStream();
        byte[] buf = new byte[64];
        int read = in.read(buf, 0, buf.length);
        in.close();
        return read;
    }

    @Benchmark
    public int readMediumBuffer() throws IOException {
        BlockLZ4CompressorInputStream in = newInputStream();
        byte[] buf = new byte[256];
        int read = in.read(buf, 0, buf.length);
        in.close();
        return read;
    }

    @Benchmark
    public int readLargeBuffer() throws IOException {
        BlockLZ4CompressorInputStream in = newInputStream();
        byte[] buf = new byte[1024];
        int read = in.read(buf, 0, buf.length);
        in.close();
        return read;
    }

    @Benchmark
    public int readSingleByte() throws IOException {
        BlockLZ4CompressorInputStream in = newInputStream();
        int b = in.read();
        in.close();
        return b;
    }

    @Benchmark
    public int readIntoBlackhole(Blackhole bh) throws IOException {
        BlockLZ4CompressorInputStream in = newInputStream();
        byte[] buf = new byte[128];
        int read = in.read(buf, 0, buf.length);
        bh.consume(buf);
        in.close();
        return read;
    }
}
