package bench.generated.c068;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;

import org.apache.commons.compress.compressors.snappy.SnappyCompressorInputStream;
import org.apache.commons.compress.compressors.snappy.FramedSnappyCompressorOutputStream;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class SnappyCompressorInputStreamBenchmark {

    private byte[] compressedData;
    private byte[] originalData;

    /**
     * Generates a fixed payload of random data (10KB) and compresses it using Snappy
     * to create the input byte array for the benchmark.
     */
    @Setup(Level.Trial)
    public void setup() throws IOException {
        // 1. Generate original data (10 KB)
        int dataSize = 10240;
        originalData = new byte[dataSize];
        new Random().nextBytes(originalData);

        // 2. Compress data into a byte array using FramedSnappyCompressorOutputStream
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (FramedSnappyCompressorOutputStream fso = new FramedSnappyCompressorOutputStream(baos)) {
            fso.write(originalData);
            fso.finish();
        }
        compressedData = baos.toByteArray();
    }

    /**
     * Benchmarks the total time taken to decompress the entire stream.
     * The stream is recreated for every invocation to ensure state isolation.
     */
    @Benchmark
    public void benchmarkFullStreamDecompression(Blackhole bh) throws IOException {
        // Setup per invocation
        ByteArrayInputStream bais = new ByteArrayInputStream(compressedData);
        SnappyCompressorInputStream scis = new SnappyCompressorInputStream(bais);

        // Drain the stream completely
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = scis.read(buffer, 0, buffer.length)) != -1) {
            baos.write(buffer, 0, bytesRead);
        }
        scis.close();

        // Consume the result
        bh.consume(baos.toByteArray());
    }

    /**
     * Benchmarks reading a fixed chunk size (4KB) repeatedly until the stream ends.
     * This tests the performance of repeated read calls and state transitions.
     */
    @Benchmark
    public void benchmarkChunkedDecompression(Blackhole bh) throws IOException {
        // Setup per invocation
        ByteArrayInputStream bais = new ByteArrayInputStream(compressedData);
        SnappyCompressorInputStream scis = new SnappyCompressorInputStream(bais);

        // Read chunks until EOF
        byte[] buffer = new byte[4096];
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int bytesRead;
        while ((bytesRead = scis.read(buffer, 0, buffer.length)) != -1) {
            baos.write(buffer, 0, bytesRead);
        }
        scis.close();

        // Consume the result
        bh.consume(baos.toByteArray());
    }
}
