package bench.generated.c044;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class BZip2CompressorInputStreamBenchmark {

    private byte[] rawData;
    private byte[] compressedData;

    @Setup(Level.Trial)
    public void setUp() throws IOException {
        // Create a deterministic payload (~10 KiB)
        rawData = new byte[10 * 1024];
        for (int i = 0; i < rawData.length; i++) {
            rawData[i] = (byte) (i & 0xFF);
        }

        // Compress the payload using BZip2CompressorOutputStream (block size 9 = max)
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (BZip2CompressorOutputStream bzipOut = new BZip2CompressorOutputStream(baos, 9)) {
            bzipOut.write(rawData);
            bzipOut.finish();
        }
        compressedData = baos.toByteArray();
    }

    @Benchmark
    public int readSingleByte() throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(compressedData);
        BZip2CompressorInputStream in = new BZip2CompressorInputStream(bais);
        int result = in.read();
        in.close();
        return result;
    }

    @Benchmark
    public int readIntoBuffer() throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(compressedData);
        BZip2CompressorInputStream in = new BZip2CompressorInputStream(bais);
        byte[] buf = new byte[rawData.length];
        int read = in.read(buf, 0, buf.length);
        in.close();
        return read;
    }

    @Benchmark
    public long getCompressedCount() throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(compressedData);
        BZip2CompressorInputStream in = new BZip2CompressorInputStream(bais);
        // Advance the stream a little so the count is non‑zero
        in.read();
        long count = in.getCompressedCount();
        in.close();
        return count;
    }

    @Benchmark
    public boolean matchesSignature() {
        // Use the full byte array; the method only inspects the first three bytes
        return BZip2CompressorInputStream.matches(compressedData, compressedData.length);
    }

    @Benchmark
    public int readAllAndConsume(Blackhole bh) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(compressedData);
        BZip2CompressorInputStream in = new BZip2CompressorInputStream(bais);
        byte[] buf = new byte[4096];
        int total = 0;
        int n;
        while ((n = in.read(buf)) != -1) {
            total += n;
            bh.consume(buf);
        }
        in.close();
        return total;
    }
}
