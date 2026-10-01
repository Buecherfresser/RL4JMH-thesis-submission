package bench.generated.c060;

import org.apache.commons.compress.compressors.lzma.LZMACompressorInputStream;
import org.apache.commons.io.IOUtils;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class LZMACompressorInputStreamBenchmark {

    private LZMACompressorInputStream inputStream;
    private byte[] compressedData;
    private final int dataSize = 1024 * 1024 * 4; // 4 MB payload

    @Setup
    public void setup() throws IOException {
        // 1. Generate a large payload. In a real scenario, this would be a known LZMA compressed file.
        // For this benchmark, we use random data and rely on the LZMA stream implementation
        // to perform the decompression work.
        compressedData = new byte[dataSize];
        for (int i = 0; i < dataSize; i++) {
            compressedData[i] = (byte) (i % 256);
        }

        // 2. Create the input stream from the payload
        InputStream input = new ByteArrayInputStream(compressedData);
        
        // Initialize the subject under test
        this.inputStream = new LZMACompressorInputStream(input);
    }

    @Benchmark
    public void benchmarkReadSingleByte(Blackhole bh) throws IOException {
        int result = inputStream.read();
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkReadBlock(Blackhole bh) throws IOException {
        byte[] buffer = new byte[8192];
        int result = inputStream.read(buffer, 0, buffer.length);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkSkipLargeAmount(Blackhole bh) throws IOException {
        long skipAmount = dataSize / 4;
        long bytesSkipped = inputStream.skip(skipAmount);
        bh.consume(bytesSkipped);
    }

    @Benchmark
    public void benchmarkGetCompressedCount(Blackhole bh) {
        long count = inputStream.getCompressedCount();
        bh.consume(count);
    }

    @Benchmark
    public void benchmarkClose(Blackhole bh) throws IOException {
        inputStream.close();
        // Nothing to consume, just ensuring the operation runs
    }
}
