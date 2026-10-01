package bench.generated.c051;

import org.apache.commons.compress.compressors.CompressorOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class GzipCompressorOutputStreamBenchmark {

    private static final int PAYLOAD_SIZE = 1024 * 1024; // 1 MB payload
    private byte[] rawPayload;
    private byte[] compressedData;
    private ByteArrayOutputStream baos;

    @Setup
    public void setup() throws IOException {
        // 1. Generate a fixed, random payload
        rawPayload = new byte[PAYLOAD_SIZE];
        new Random().nextBytes(rawPayload);

        // 2. Generate the compressed data once
        baos = new ByteArrayOutputStream();
        try (GzipCompressorOutputStream gzipOut = new GzipCompressorOutputStream(baos)) {
            // Write the entire payload
            gzipOut.write(rawPayload);
            // Finish the compression process
            gzipOut.finish();
        }
        compressedData = baos.toByteArray();
    }

    /**
     * Benchmark 1: Compression (Writing data)
     * Tests the performance of writing a large payload through the GzipCompressorOutputStream.
     */
    @Benchmark
    public void testWriteFullPayload(Blackhole bh) throws IOException {
        // Setup: Create a fresh output stream for each run
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (GzipCompressorOutputStream gzipOut = new GzipCompressorOutputStream(outputStream)) {
            // Action: Write the fixed payload
            gzipOut.write(rawPayload);
            // Finalize the compression
            gzipOut.finish();
        }
        // Consume the result
        bh.consume(outputStream.toByteArray());
    }

    /**
     * Benchmark 2: Compression (Writing partial data)
     * Tests the performance of writing a segment of the payload.
     */
    @Benchmark
    public void testWritePartialPayload(Blackhole bh) throws IOException {
        // Setup: Create a fresh output stream for each run
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        int partialSize = PAYLOAD_SIZE / 4;
        byte[] partialData = new byte[partialSize];
        new Random().nextBytes(partialData);

        try (GzipCompressorOutputStream gzipOut = new GzipCompressorOutputStream(outputStream)) {
            // Action: Write a partial segment
            gzipOut.write(partialData);
            // Finish the compression (necessary to finalize headers/trailers for a valid stream)
            gzipOut.finish();
        }
        // Consume the result
        bh.consume(outputStream.toByteArray());
    }

    /**
     * Benchmark 3: Decompression (Reading compressed data)
     * Tests the performance of decompressing the pre-generated compressed data.
     */
    @Benchmark
    public void testDecompressFullData(Blackhole bh) throws IOException {
        // Setup: Create an input stream from the pre-generated compressed data
        ByteArrayInputStream bais = new ByteArrayInputStream(compressedData);

        // Action: Use the corresponding input stream to decompress
        try (GzipCompressorInputStream gzipIn = new GzipCompressorInputStream(bais)) {
            byte[] decompressed = new byte[PAYLOAD_SIZE];
            int bytesRead = gzipIn.read(decompressed);
            // Consume the resulting array
            bh.consume(decompressed);
        }
    }

    /**
     * Benchmark 4: Decompression (Reading partial data)
     * Tests the performance of decompressing a segment of the compressed data.
     */
    @Benchmark
    public void testDecompressPartialData(Blackhole bh) throws IOException {
        // Setup: Create an input stream from the pre-generated compressed data
        ByteArrayInputStream bais = new ByteArrayInputStream(compressedData);
        int partialSize = PAYLOAD_SIZE / 4;

        // Action: Use the corresponding input stream to decompress a partial segment
        try (GzipCompressorInputStream gzipIn = new GzipCompressorInputStream(bais)) {
            byte[] decompressed = new byte[partialSize];
            int bytesRead = gzipIn.read(decompressed);
            // Consume the resulting array
            bh.consume(decompressed);
        }
    }
}
