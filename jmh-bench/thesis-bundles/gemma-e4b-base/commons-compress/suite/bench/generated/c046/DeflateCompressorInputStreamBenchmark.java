package bench.generated.c046;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.apache.commons.compress.compressors.deflate.DeflateCompressorInputStream;
import org.apache.commons.compress.compressors.deflate.DeflateParameters;
import org.apache.commons.compress.compressors.deflate.DeflateCompressorOutputStream;
import org.apache.commons.io.IOUtils;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class DeflateCompressorInputStreamBenchmark {

    private byte[] compressedData;
    private DeflateParameters defaultParameters;

    // State for read/skip benchmarks
    private ByteArrayInputStream rawInputStream;
    private DeflateCompressorInputStream decompressorStream;

    @Setup(Level.Trial)
    public void setupTrial() throws IOException {
        // 1. Generate a representative payload (e.g., 10KB of data)
        String originalData = "This is a test string used to generate compressed data for JMH benchmarking. "
                + "We repeat this many times to ensure a reasonable payload size. "
                + "Lorem ipsum dolor sit amet, consectetur adipiscing elit. "
                + "Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. "
                + "Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.";
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        // Use DeflateCompressorOutputStream to create the compressed input data
        try (DeflateCompressorOutputStream compressor = new DeflateCompressorOutputStream(baos)) {
            for (int i = 0; i < 100; i++) {
                compressor.write(originalData.getBytes());
            }
            compressor.finish();
        }
        
        this.compressedData = baos.toByteArray();
        
        // 2. Setup default parameters
        this.defaultParameters = new DeflateParameters();
    }

    @Setup(Level.Iteration)
    public void setupIteration() throws IOException {
        // Reset the raw input stream for each iteration
        this.rawInputStream = new ByteArrayInputStream(compressedData);
        
        // Create a fresh decompressor stream instance for each iteration
        this.decompressorStream = new DeflateCompressorInputStream(rawInputStream, defaultParameters);
    }

    // --- Benchmarks for Instance Methods ---

    @Benchmark
    public void benchmarkReadSingleByte(Blackhole bh) throws IOException {
        int result = decompressorStream.read();
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkReadBulkData(Blackhole bh) throws IOException {
        byte[] buffer = new byte[4096];
        int result = decompressorStream.read(buffer, 0, buffer.length);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkSkipData(Blackhole bh) throws IOException {
        long skipAmount = 1024;
        long skipped = decompressorStream.skip(skipAmount);
        bh.consume(skipped);
    }

    @Benchmark
    public void benchmarkAvailable(Blackhole bh) throws IOException {
        int available = decompressorStream.available();
        bh.consume(available);
    }

    @Benchmark
    public void benchmarkGetCompressedCount(Blackhole bh) {
        long count = decompressorStream.getCompressedCount();
        bh.consume(count);
    }

    @Benchmark
    public void benchmarkCloseStream(Blackhole bh) throws IOException {
        // Closing streams is often non-deterministic in timing, but we must test it.
        // We wrap it in a try-catch block to ensure the benchmark doesn't fail if the stream is already closed,
        // though in this setup, it should be fresh.
        try {
            decompressorStream.close();
        } catch (IOException e) {
            // Ignore expected IOExceptions during close if state is messy
        }
        bh.consume(true); // Consume a boolean to ensure the call happens
    }

    // --- Benchmark for Static Method ---

    @Benchmark
    public void benchmarkMatchesSignature(Blackhole bh) {
        // Test case 1: Valid zlib header signature
        byte[] validSignature = new byte[]{0x78, 0x01, 0x00, 0x00};
        boolean result1 = DeflateCompressorInputStream.matches(validSignature, 4);
        bh.consume(result1);

        // Test case 2: Invalid signature
        byte[] invalidSignature = new byte[]{0x00, 0x00, 0x00, 0x00};
        boolean result2 = DeflateCompressorInputStream.matches(invalidSignature, 4);
        bh.consume(result2);
    }
}
