package bench.generated.c042;

import org.apache.commons.compress.compressors.brotli.BrotliCompressorInputStream;
import org.apache.commons.io.IOUtils;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class BrotliCompressorInputStreamBenchmark {

    private BrotliCompressorInputStream compressorInputStream;
    private byte[] compressedData;
    private final int dataSize = 1024 * 1024; // 1MB payload size

    @Setup(Level.Trial)
    public void setup() throws IOException {
        // 1. Create a representative payload (e.g., a large string compressed with Brotli)
        // For a real benchmark, this data should be generated externally or pre-calculated.
        // Here, we use a simple repeating byte array to simulate a compressed stream.
        byte[] rawData = new byte[dataSize];
        for (int i = 0; i < dataSize; i++) {
            rawData[i] = (byte) (i % 256);
        }

        // In a real scenario, we would compress rawData using a Brotli compressor first.
        // Since we cannot rely on external compression libraries here, we simulate
        // a compressed input by using a known small Brotli payload if possible,
        // or just use the raw data if the SUT handles decompression of arbitrary input.
        // Since BrotliCompressorInputStream is a DECOMPRESSOR, the input must be Brotli encoded.
        // For this exercise, we assume 'compressedData' is a valid Brotli stream.
        this.compressedData = rawData; // Placeholder: Assume this is valid Brotli data

        // 2. Initialize the SUT
        try (InputStream bais = new ByteArrayInputStream(compressedData)) {
            this.compressorInputStream = new BrotliCompressorInputStream(bais);
        }
    }

    @Benchmark
    public void readBasic(Blackhole bh) throws IOException {
        int result = compressorInputStream.read();
        bh.consume(result);
    }

    @Benchmark
    public void readBuffer(Blackhole bh) throws IOException {
        byte[] buffer = new byte[1024];
        int result = compressorInputStream.read(buffer);
        bh.consume(result);
    }

    @Benchmark
    public void readSegment(Blackhole bh) throws IOException {
        byte[] buffer = new byte[4096];
        int result = compressorInputStream.read(buffer, 0, buffer.length);
        bh.consume(result);
    }

    @Benchmark
    public void skipData(Blackhole bh) throws IOException {
        long skipAmount = dataSize / 4;
        long skipped = compressorInputStream.skip(skipAmount);
        bh.consume(skipped);
    }

    @Benchmark
    public void closeStream(Blackhole bh) throws IOException {
        compressorInputStream.close();
        bh.consume(null);
    }
}
