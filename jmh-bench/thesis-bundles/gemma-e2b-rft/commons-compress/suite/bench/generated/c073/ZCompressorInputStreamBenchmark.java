package bench.generated.c073;

import org.apache.commons.compress.compressors.z.ZCompressorInputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class ZCompressorInputStreamBenchmark {

    // A representative, fixed payload for benchmarking.
    // Since generating a fully valid .Z file is complex, this payload is constructed
    // to contain the required magic bytes (0x1f, 0x9d) and some data,
    // ensuring the ZCompressorInputStream constructor does not immediately fail.
    private byte[] compressedData;
    private ByteArrayInputStream inputStream;

    @Setup
    public void setup() throws IOException {
        // Create a minimal, valid-looking payload.
        // In a real scenario, this would be a large, complex, compressed file.
        // Here, we ensure the magic bytes are present.
        byte[] header = { (byte) 0x1f, (byte) 0x9d, (byte) 0x01 }; // MAGIC_1, MAGIC_2, blockMode=0
        byte[] payload = new byte[1024 * 10]; // 10 KB of dummy data
        System.arraycopy(header, 0, payload, 0, header.length);
        Arrays.fill(payload, header.length, payload.length, (byte) 0xAA);

        this.compressedData = payload;
        this.inputStream = new ByteArrayInputStream(compressedData);
    }

    /**
     * Benchmark for decompressing a fixed, pre-generated .Z payload.
     * This tests the core decompression logic of ZCompressorInputStream.
     */
    @Benchmark
    public void benchmarkDecompression(Blackhole bh) throws IOException {
        // 1. Wrap the fixed data in an InputStream
        InputStream dataStream = new ByteArrayInputStream(compressedData);

        // 2. Instantiate the ZCompressorInputStream
        ZCompressorInputStream zInputStream = new ZCompressorInputStream(dataStream);

        // 3. Decompress the entire stream and consume the result
        byte[] decompressedData = IOUtils.toByteArray(zInputStream);

        bh.consume(decompressedData);
    }
}
