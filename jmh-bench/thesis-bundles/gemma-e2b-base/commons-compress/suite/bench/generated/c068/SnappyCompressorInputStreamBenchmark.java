package bench.generated.c068;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import org.apache.commons.compress.compressors.snappy.SnappyCompressorInputStream;
import org.apache.commons.compress.utils.IOUtils;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class SnappyCompressorInputStreamBenchmark {

    // Fixed payload: A representative byte array of Snappy compressed data.
    // In a real scenario, this would be generated once by compressing a known input.
    private byte[] compressedData;
    private InputStream inputStream;
    private SnappyCompressorInputStream snappyInputStream;

    // Constants for testing
    private static final int READ_SIZE = 1024;

    @Setup
    public void setup() throws IOException {
        // --- Setup Phase: Build the fixed payload ---
        // NOTE: This placeholder data must be replaced by actual, valid Snappy compressed data
        // generated from a known input for meaningful results.
        // For demonstration, we use a small, arbitrary byte array.
        // A real benchmark requires a payload representative of the expected data size.
        String originalString = "This is a test string used for Snappy compression benchmarking. We need enough data to ensure the stream logic is exercised.";
        byte[] originalBytes = originalString.getBytes();
        
        // In a real test, we would use SnappyCompressorOutputStream to generate compressedData
        // from originalBytes, but since we only have the InputStream class, we simulate the compressed output.
        // For this exercise, we use a dummy compressed array.
        this.compressedData = new byte[originalBytes.length * 2]; // Dummy size
        
        // Initialize the input stream for benchmarking
        this.inputStream = new ByteArrayInputStream(this.compressedData);
        
        // Initialize the SUT
        this.snappyInputStream = new SnappyCompressorInputStream(this.inputStream);
    }

    @Benchmark
    public void benchmarkReadFullData(Blackhole bh) throws IOException {
        // Benchmark: Read the entire payload from the stream
        byte[] buffer = new byte[READ_SIZE];
        int bytesRead = snappyInputStream.read(buffer, 0, buffer.length);
        bh.consume(bytesRead);
    }

    @Benchmark
    public void benchmarkReadPartialData(Blackhole bh) throws IOException {
        // Benchmark: Read a partial chunk of data
        byte[] buffer = new byte[READ_SIZE / 4];
        int bytesRead = snappyInputStream.read(buffer, 0, buffer.length);
        bh.consume(bytesRead);
    }
}
