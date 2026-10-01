package bench.generated.c060;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.compressors.lzma.LZMACompressorInputStream;
import org.apache.commons.compress.compressors.lzma.LZMACompressorOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class LZMACompressorInputStreamBenchmark {

    private byte[] compressedData;
    private byte[] originalData;

    @Setup(Level.Trial)
    public void setUp() throws IOException {
        // Prepare a deterministic payload
        originalData = new byte[1024];
        for (int i = 0; i < originalData.length; i++) {
            originalData[i] = (byte) (i & 0xFF);
        }

        // Compress the payload once for all benchmarks
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (LZMACompressorOutputStream lzmaOut = new LZMACompressorOutputStream(baos)) {
            lzmaOut.write(originalData);
        }
        compressedData = baos.toByteArray();
    }

    @Benchmark
    public int readSingleByte() throws IOException {
        try (LZMACompressorInputStream in = new LZMACompressorInputStream(new ByteArrayInputStream(compressedData))) {
            return in.read();
        }
    }

    @Benchmark
    public int readIntoBuffer() throws IOException {
        byte[] buf = new byte[256];
        try (LZMACompressorInputStream in = new LZMACompressorInputStream(new ByteArrayInputStream(compressedData))) {
            return in.read(buf, 0, buf.length);
        }
    }

    @Benchmark
    public int availableBytes() throws IOException {
        try (LZMACompressorInputStream in = new LZMACompressorInputStream(new ByteArrayInputStream(compressedData))) {
            return in.available();
        }
    }

    @Benchmark
    public long getCompressedCount() throws IOException {
        try (LZMACompressorInputStream in = new LZMACompressorInputStream(new ByteArrayInputStream(compressedData))) {
            // No read performed; count should be zero
            return in.getCompressedCount();
        }
    }

    @Benchmark
    public long skipBytes() throws IOException {
        try (LZMACompressorInputStream in = new LZMACompressorInputStream(new ByteArrayInputStream(compressedData))) {
            return in.skip(10);
        }
    }
}
