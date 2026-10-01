package bench.generated.c050;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.io.IOUtils;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Fork(1)
@Warmup(iterations = 2, time = 1)
@Measurement(iterations = 3, time = 1)
public class GzipCompressorInputStreamBenchmark {

    private byte[] compressedData;
    private static final int PAYLOAD_SIZE = 1024 * 1024; // 1MB payload

    /**
     * Setup runs once per benchmark run (Trial scope).
     * We pre-compress a large payload into memory.
     */
    @Setup(Level.Trial)
    public void setup() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        // Use GzipCompressorOutputStream to compress the payload
        try (GzipCompressorOutputStream gos = new GzipCompressorOutputStream(baos)) {
            byte[] payload = new byte[PAYLOAD_SIZE];
            // Fill payload with some data
            for (int i = 0; i < PAYLOAD_SIZE; i++) {
                payload[i] = (byte) (i % 256);
            }
            gos.write(payload);
            gos.finish();
        }
        
        this.compressedData = baos.toByteArray();
    }

    /**
     * Benchmark for reading the entire decompressed stream content.
     * This measures the throughput of the decompression process.
     */
    @Benchmark
    public void benchmarkFullDecompressionRead(Blackhole bh) throws IOException {
        // Create a fresh stream instance for each invocation to ensure clean state
        try (InputStream bais = new ByteArrayInputStream(compressedData);
             GzipCompressorInputStream gcis = new GzipCompressorInputStream(bais)) {
            
            // Read all decompressed data and consume it
            byte[] decompressed = IOUtils.toByteArray(gcis);
            bh.consume(decompressed);
        }
    }

    /**
     * Benchmark for reading a fixed chunk of data (e.g., 8KB buffer).
     * This measures sustained read performance.
     */
    @Benchmark
    public void benchmarkChunkedRead(Blackhole bh) throws IOException {
        final int bufferSize = 8192;
        
        try (InputStream bais = new ByteArrayInputStream(compressedData);
             GzipCompressorInputStream gcis = new GzipCompressorInputStream(bais)) {
            
            byte[] buffer = new byte[bufferSize];
            int bytesRead = 0;
            
            // Read until EOF
            while ((bytesRead = gcis.read(buffer, 0, bufferSize)) != -1) {
                bh.consume(bytesRead);
            }
        }
    }

    /**
     * Benchmark for reading a single byte.
     */
    @Benchmark
    public void benchmarkSingleByteRead(Blackhole bh) throws IOException {
        try (InputStream bais = new ByteArrayInputStream(compressedData);
             GzipCompressorInputStream gcis = new GzipCompressorInputStream(bais)) {
            
            int byteRead = gcis.read();
            bh.consume(byteRead);
        }
    }

    /**
     * Benchmark for retrieving the compressed size metadata.
     */
    @Benchmark
    public void benchmarkGetCompressedCount(Blackhole bh) throws IOException {
        try (InputStream bais = new ByteArrayInputStream(compressedData);
             GzipCompressorInputStream gcis = new GzipCompressorInputStream(bais)) {
            
            long count = gcis.getCompressedCount();
            bh.consume(count);
        }
    }

    /**
     * Benchmark for retrieving the GzipParameters metadata.
     */
    @Benchmark
    public void benchmarkGetMetaData(Blackhole bh) throws IOException {
        try (InputStream bais = new ByteArrayInputStream(compressedData);
             GzipCompressorInputStream gcis = new GzipCompressorInputStream(bais)) {
            
            // Note: getMetaData() might change if concatenated streams are used, 
            // but for a single member stream, it should be stable.
            org.apache.commons.compress.compressors.gzip.GzipParameters params = gcis.getMetaData();
            bh.consume(params);
        }
    }
}
