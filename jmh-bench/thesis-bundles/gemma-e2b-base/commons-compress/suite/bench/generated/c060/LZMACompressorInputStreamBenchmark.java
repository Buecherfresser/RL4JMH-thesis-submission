package bench.generated.c060;

import org.apache.commons.compress.compressors.lzma.LZMACompressorInputStream;
import org.apache.commons.io.IOUtils;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class LZMACompressorInputStreamBenchmark {

    private InputStream compressedDataStream;
    private byte[] compressedPayload;
    private final int payloadSize = 1024 * 1024; // 1MB payload

    @Setup
    public void setup() throws IOException {
        // --- Input Generation ---
        // In a real scenario, this payload would be a valid LZMA compressed stream.
        // For benchmarking the stream mechanics, we use a large, fixed byte array.
        // We fill it with repeating data to ensure the stream processing is non-trivial.
        byte[] rawData = new byte[payloadSize];
        for (int i = 0; i < payloadSize; i++) {
            rawData[i] = (byte) (i % 256);
        }
        this.compressedPayload = rawData;
        this.compressedDataStream = new ByteArrayInputStream(compressedPayload);
    }

    @Benchmark
    public void benchmarkReadSingleByte(Blackhole bh) throws IOException {
        LZMACompressorInputStream stream = new LZMACompressorInputStream(compressedDataStream);
        int result = stream.read();
        bh.consume(result);
        stream.close();
    }

    @Benchmark
    public void benchmarkReadChunk(Blackhole bh) throws IOException {
        LZMACompressorInputStream stream = new LZMACompressorInputStream(compressedDataStream);
        byte[] buffer = new byte[4096];
        int bytesRead = stream.read(buffer, 0, buffer.length);
        bh.consume(bytesRead);
        stream.close();
    }

    @Benchmark
    public void benchmarkSkip(Blackhole bh) throws IOException {
        LZMACompressorInputStream stream = new LZMACompressorInputStream(compressedDataStream);
        long skipAmount = payloadSize / 4;
        long skipped = stream.skip(skipAmount);
        bh.consume(skipped);
        stream.close();
    }

    @Benchmark
    public void benchmarkFullRead(Blackhole bh) throws IOException {
        LZMACompressorInputStream stream = new LZMACompressorInputStream(compressedDataStream);
        byte[] result = IOUtils.toByteArray(stream);
        bh.consume(result);
        stream.close();
    }

    @Benchmark
    public void benchmarkGetCompressedCount(Blackhole bh) throws IOException {
        LZMACompressorInputStream stream = new LZMACompressorInputStream(compressedDataStream);
        long count = stream.getCompressedCount();
        bh.consume(count);
        stream.close();
    }
}
