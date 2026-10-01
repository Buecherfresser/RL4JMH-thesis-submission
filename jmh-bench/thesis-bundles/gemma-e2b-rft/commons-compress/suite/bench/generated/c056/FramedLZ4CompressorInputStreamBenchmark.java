package bench.generated.c056;

import org.apache.commons.compress.compressors.lz4.FramedLZ4CompressorInputStream;
import org.apache.commons.io.IOUtils;
import org.apache.commons.codec.digest.XXHash32;
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
public class FramedLZ4CompressorInputStreamBenchmark {

    private byte[] testPayload;
    private InputStream inputStream;
    private FramedLZ4CompressorInputStream decompressedStream;

    // Constants for payload generation
    private static final int SMALL_PAYLOAD_SIZE = 1024;
    private static final int LARGE_PAYLOAD_SIZE = 65536;

    @Setup
    public void setup() throws IOException {
        // --- Payload Generation ---
        // Since we cannot run a full LZ4 compressor here, we construct a byte array
        // that mimics the structure required by FramedLZ4CompressorInputStream
        // (starting with the LZ4 signature) to test the stream parsing logic.
        
        // Create a base payload structure: Signature + some dummy data
        byte[] signature = { 4, 0x22, 0x4d, 0x18 };
        
        if (SMALL_PAYLOAD_SIZE > 0) {
            testPayload = new byte[SMALL_PAYLOAD_SIZE];
            System.arraycopy(signature, 0, testPayload, 0, 4);
            // Fill the rest with dummy data
            for (int i = 4; i < SMALL_PAYLOAD_SIZE; i++) {
                testPayload[i] = (byte) (i % 256);
            }
        } else {
            testPayload = new byte[LARGE_PAYLOAD_SIZE];
            System.arraycopy(signature, 0, testPayload, 0, 4);
            for (int i = 4; i < LARGE_PAYLOAD_SIZE; i++) {
                testPayload[i] = (byte) (i % 256);
            }
        }

        // Initialize the input stream for reuse
        inputStream = new ByteArrayInputStream(testPayload);
        
        // Initialize the decompressed stream for reuse (if needed for specific tests)
        // We will initialize it inside the benchmarks to test different modes.
    }

    @Benchmark
    public void benchmarkReadSingleByte(Blackhole bh) throws IOException {
        // Test the basic read() method
        FramedLZ4CompressorInputStream stream = new FramedLZ4CompressorInputStream(inputStream);
        int result = stream.read();
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkDecompressFullStream(Blackhole bh) throws IOException {
        // Test decompression until the end (decompressConcatenated = true)
        FramedLZ4CompressorInputStream stream = new FramedLZ4CompressorInputStream(inputStream, true);
        
        // Drain the entire stream
        byte[] result = IOUtils.toByteArray(stream);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkDecompressFirstFrameOnly(Blackhole bh) throws IOException {
        // Test decompression stopping after the first frame (decompressConcatenated = false)
        FramedLZ4CompressorInputStream stream = new FramedLZ4CompressorInputStream(inputStream, false);
        
        // Drain the data read up to the first frame boundary
        byte[] result = IOUtils.toByteArray(stream);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkDecompressSmallPayload(Blackhole bh) throws IOException {
        // Test decompression performance on a small payload
        FramedLZ4CompressorInputStream stream = new FramedLZ4CompressorInputStream(new ByteArrayInputStream(testPayload), true);
        
        byte[] result = IOUtils.toByteArray(stream);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkDecompressLargePayload(Blackhole bh) throws IOException {
        // Test decompression performance on a large payload
        FramedLZ4CompressorInputStream stream = new FramedLZ4CompressorInputStream(new ByteArrayInputStream(testPayload), true);
        
        byte[] result = IOUtils.toByteArray(stream);
        bh.consume(result);
    }
    
    @Benchmark
    public void benchmarkMatchesStatic(Blackhole bh) {
        // Test the static utility method
        byte[] signature = { 4, 0x22, 0x4d, 0x18 };
        boolean result = FramedLZ4CompressorInputStream.matches(signature, 4);
        bh.consume(result);
    }
}
