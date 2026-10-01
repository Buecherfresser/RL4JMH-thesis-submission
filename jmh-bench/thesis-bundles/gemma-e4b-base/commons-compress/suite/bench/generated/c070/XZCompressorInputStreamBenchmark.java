package bench.generated.c070;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.compressors.xz.XZCompressorInputStream;
import org.tukaani.xz.XZOutputStream;
import org.tukaani.xz.FilterOptions;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class XZCompressorInputStreamBenchmark {

    private XZCompressorInputStream xzStream;
    private byte[] compressedData;
    private static final int PAYLOAD_SIZE = 1024 * 10; // 10 KB uncompressed data

    @Setup(Level.Trial)
    public void setup() throws IOException {
        // 1. Create uncompressed data
        byte[] uncompressedData = new byte[PAYLOAD_SIZE];
        for (int i = 0; i < PAYLOAD_SIZE; i++) {
            uncompressedData[i] = (byte) (i % 256);
        }

        // 2. Compress data using tukaani.xz.XZOutputStream
        // Fix: Use the constructor that accepts FilterOptions[]
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             XZOutputStream xzos = new XZOutputStream(baos, new FilterOptions[0])) {
            
            xzos.write(uncompressedData);
            xzos.finish(); // Ensure all data is flushed and stream is finalized
            
            this.compressedData = baos.toByteArray();
        }

        // 3. Initialize the XZCompressorInputStream
        InputStream compressedInput = new ByteArrayInputStream(compressedData);
        this.xzStream = new XZCompressorInputStream(compressedInput);
    }

    @TearDown(Level.Trial)
    public void tearDown() throws IOException {
        if (xzStream != null) {
            xzStream.close();
        }
    }

    @Benchmark
    public int readSingleByte(Blackhole bh) throws IOException {
        int result = xzStream.read();
        bh.consume(result);
        return result;
    }

    @Benchmark
    public int readBuffered(Blackhole bh) throws IOException {
        byte[] buffer = new byte[4096];
        int result = xzStream.read(buffer, 0, buffer.length);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public long skipBytes(Blackhole bh) throws IOException {
        long skipped = xzStream.skip(1024);
        bh.consume(skipped);
        return skipped;
    }

    @Benchmark
    public int availableBytes(Blackhole bh) throws IOException {
        int available = xzStream.available();
        bh.consume(available);
        return available;
    }

    @Benchmark
    public long getCompressedCount(Blackhole bh) {
        long count = xzStream.getCompressedCount();
        bh.consume(count);
        return count;
    }
}
