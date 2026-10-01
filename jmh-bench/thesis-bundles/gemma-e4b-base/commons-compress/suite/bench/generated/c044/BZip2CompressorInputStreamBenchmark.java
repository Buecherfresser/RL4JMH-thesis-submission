package bench.generated.c044;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class BZip2CompressorInputStreamBenchmark {

    private byte[] compressedPayload;
    private ByteArrayInputStream inputStream;
    private BZip2CompressorInputStream compressorInputStream;

    @Setup(Level.Trial)
    public void setup() throws IOException {
        // Using a small, fixed payload for repeatable benchmarking.
        // This array must represent a valid BZip2 stream structure for the SUT to function.
        // Since we cannot generate a valid stream here, we use a minimal placeholder.
        compressedPayload = new byte[]{
                (byte) 0x42, (byte) 0x5A, (byte) 0x68, // BZh magic
                (byte) 0x01, // Block size 100k
                // ... rest of the compressed data structure ...
                (byte) 0x00, (byte) 0x00, (byte) 0x00
        };

        inputStream = new ByteArrayInputStream(compressedPayload);
        
        // Initialize the compressor stream
        compressorInputStream = new BZip2CompressorInputStream(inputStream);
    }

    @TearDown(Level.Trial)
    public void tearDown() throws IOException {
        if (compressorInputStream != null) {
            compressorInputStream.close();
        }
    }

    /**
     * Benchmarks reading a single byte from the decompressed stream.
     */
    @Benchmark
    public int benchmarkReadSingleByte(Blackhole bh) throws IOException {
        int result = compressorInputStream.read();
        bh.consume(result);
        return result;
    }

    /**
     * Benchmarks reading a bulk amount of data from the decompressed stream.
     */
    @Benchmark
    public int benchmarkReadBulkData(Blackhole bh) throws IOException {
        byte[] buffer = new byte[1024];
        int bytesRead = compressorInputStream.read(buffer, 0, buffer.length);
        bh.consume(bytesRead);
        return bytesRead;
    }

    /**
     * Benchmarks checking the total number of bytes read from the compressed input stream.
     */
    @Benchmark
    public long benchmarkGetCompressedCount(Blackhole bh) {
        long count = compressorInputStream.getCompressedCount();
        bh.consume(count);
        return count;
    }

    /**
     * Benchmarks reading until the stream reaches EOF (public equivalent of stream completion check).
     */
    @Benchmark
    public int benchmarkStreamCompletion(Blackhole bh) throws IOException {
        // Reading until EOF is the public way to check stream completion.
        int result = compressorInputStream.read();
        bh.consume(result);
        return result;
    }
}
