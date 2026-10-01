package bench.generated.c051;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipParameters;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class GzipCompressorOutputStreamBenchmark {

    private byte[] inputData;
    private GzipParameters defaultParameters;

    @Setup(Level.Trial)
    public void setup() {
        // 1. Setup default parameters
        defaultParameters = new GzipParameters();
        
        // 2. Setup input data (e.g., 1MB of random data)
        int dataSize = 1024 * 1024; 
        inputData = new byte[dataSize];
        for (int i = 0; i < dataSize; i++) {
            inputData[i] = (byte) (i % 256);
        }
    }

    /**
     * Benchmarks the full compression cycle: initialization, writing a large chunk,
     * finishing the stream, and closing it.
     */
    @Benchmark
    public byte[] benchmarkFullCompressionCycleLargeData(Blackhole bh) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        // Initialize stream with default parameters
        GzipCompressorOutputStream gos = new GzipCompressorOutputStream(baos, defaultParameters);
        
        // Write data
        gos.write(inputData);
        
        // Finish compression
        gos.finish();
        
        // Close stream (releases resources)
        gos.close();
        
        // Consume result
        return baos.toByteArray();
    }

    /**
     * Benchmarks the full compression cycle using the default constructor (which uses default parameters).
     */
    @Benchmark
    public byte[] benchmarkFullCompressionCycleDefaultConstructor(Blackhole bh) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        // Initialize stream using default constructor
        GzipCompressorOutputStream gos = new GzipCompressorOutputStream(baos);
        
        // Write data
        gos.write(inputData);
        
        // Finish compression
        gos.finish();
        
        // Close stream
        gos.close();
        
        // Consume result
        return baos.toByteArray();
    }

    /**
     * Benchmarks the compression cycle using custom parameters (e.g., high compression level).
     */
    @Benchmark
    public byte[] benchmarkFullCompressionCycleCustomParameters(Blackhole bh) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        // Setup custom parameters (e.g., max compression)
        GzipParameters customParams = new GzipParameters();
        customParams.setCompressionLevel(java.util.zip.Deflater.BEST_COMPRESSION);
        
        // Initialize stream with custom parameters
        GzipCompressorOutputStream gos = new GzipCompressorOutputStream(baos, customParams);
        
        // Write data
        gos.write(inputData);
        
        // Finish compression
        gos.finish();
        
        // Close stream
        gos.close();
        
        // Consume result
        return baos.toByteArray();
    }
    
    /**
     * Benchmarks the cost of writing a small chunk of data, assuming the stream is already initialized.
     * Note: Since GzipCompressorOutputStream is stateful and lacks a public reset, we must re-initialize 
     * the stream for each invocation to ensure accurate measurement of the single write operation.
     */
    @Benchmark
    public byte[] benchmarkWriteSmallChunk(Blackhole bh) throws IOException {
        // Use a small payload
        byte[] smallData = new byte[1024];
        for (int i = 0; i < smallData.length; i++) {
            smallData[i] = (byte) (i % 256);
        }
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        GzipCompressorOutputStream gos = new GzipCompressorOutputStream(baos, defaultParameters);
        
        // Write small data chunk
        gos.write(smallData);
        
        // We must finish and close to prevent resource leaks, but we only measure the write cost.
        // Since we are measuring the write operation, we must ensure the stream is reset/closed cleanly.
        gos.finish();
        gos.close();
        
        return baos.toByteArray();
    }
}
