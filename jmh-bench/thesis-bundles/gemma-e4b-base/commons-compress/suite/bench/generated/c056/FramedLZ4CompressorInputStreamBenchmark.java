package bench.generated.c056;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.compressors.lz4.FramedLZ4CompressorInputStream;
import org.apache.commons.compress.compressors.lz4.FramedLZ4CompressorOutputStream;
import org.apache.commons.io.IOUtils;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class FramedLZ4CompressorInputStreamBenchmark {

    private byte[] compressedData;
    private FramedLZ4CompressorInputStream inputStream;
    private final byte[] buffer = new byte[4096];
    private final int payloadSize = 1024 * 10; // 10 KB uncompressed payload

    @Setup(Level.Trial)
    public void setup() throws IOException {
        // 1. Generate uncompressed data
        byte[] uncompressedData = new byte[payloadSize];
        new Random().nextBytes(uncompressedData);

        // 2. Compress data using FramedLZ4CompressorOutputStream
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (FramedLZ4CompressorOutputStream flz4Out = new FramedLZ4CompressorOutputStream(baos)) {
            flz4Out.write(uncompressedData);
            flz4Out.finish();
        }
        compressedData = baos.toByteArray();

        // 3. Initialize the SUT (FramedLZ4CompressorInputStream)
        ByteArrayInputStream bais = new ByteArrayInputStream(compressedData);
        inputStream = new FramedLZ4CompressorInputStream(bais);
    }

    @TearDown(Level.Trial)
    public void tearDown() throws IOException {
        if (inputStream != null) {
            inputStream.close();
        }
    }

    /**
     * Benchmarks reading a single chunk of data (4KB) from the stream.
     */
    @Benchmark
    public void readChunk(Blackhole bh) throws IOException {
        // Read a chunk of data
        int bytesRead = inputStream.read(buffer, 0, buffer.length);
        
        // Consume the result
        bh.consume(bytesRead);
    }

    /**
     * Benchmarks reading a single byte from the stream.
     */
    @Benchmark
    public void readSingleByte(Blackhole bh) throws IOException {
        // Read a single byte
        int byteRead = inputStream.read();
        
        // Consume the result
        bh.consume(byteRead);
    }

    /**
     * Benchmarks the full decompression process by draining the entire stream.
     */
    @Benchmark
    public void decompressFullPayload(Blackhole bh) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        // Drain the stream completely
        IOUtils.copy(inputStream, baos);
        
        // Consume the result (the decompressed bytes)
        bh.consume(baos.toByteArray());
    }
}
