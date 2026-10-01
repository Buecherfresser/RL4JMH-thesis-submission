package bench.generated.c057;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.apache.commons.compress.compressors.lz4.FramedLZ4CompressorOutputStream;
import org.apache.commons.compress.compressors.lz4.FramedLZ4CompressorOutputStream.Parameters;
import org.apache.commons.compress.compressors.lz4.FramedLZ4CompressorOutputStream.BlockSize;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class FramedLZ4CompressorOutputStreamBenchmark {

    private byte[] inputData;
    private final int dataSize = 1024 * 1024 * 4; // 4MB input data

    // --- Setup ---
    @Setup
    public void setup() throws IOException {
        // Create a large, repeatable input payload
        this.inputData = new byte[dataSize];
        // Fill with some non-trivial data
        for (int i = 0; i < dataSize; i++) {
            inputData[i] = (byte) (i % 256);
        }
    }

    /**
     * Helper method to perform the compression benchmark cycle.
     * This encapsulates the stream creation, writing, and finishing process.
     *
     * @param params The parameters to use for the compressor stream.
     * @return The resulting compressed byte array.
     * @throws IOException If an IO error occurs during compression.
     */
    private byte[] compress(Parameters params) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (FramedLZ4CompressorOutputStream compressor = new FramedLZ4CompressorOutputStream(baos, params)) {
            compressor.write(inputData, 0, inputData.length);
            compressor.finish();
        }
        return baos.toByteArray();
    }

    // --- Benchmarks ---

    @Benchmark
    public void benchmark_DefaultParameters(Blackhole bh) throws IOException {
        Parameters params = Parameters.DEFAULT;
        byte[] compressedData = compress(params);
        bh.consume(compressedData);
    }

    @Benchmark
    public void benchmark_BlockSize_K64(Blackhole bh) throws IOException {
        Parameters params = new Parameters(BlockSize.K64);
        byte[] compressedData = compress(params);
        bh.consume(compressedData);
    }

    @Benchmark
    public void benchmark_BlockSize_K256(Blackhole bh) throws IOException {
        Parameters params = new Parameters(BlockSize.K256);
        byte[] compressedData = compress(params);
        bh.consume(compressedData);
    }

    @Benchmark
    public void benchmark_BlockSize_M1(Blackhole bh) throws IOException {
        Parameters params = new Parameters(BlockSize.M1);
        byte[] compressedData = compress(params);
        bh.consume(compressedData);
    }

    @Benchmark
    public void benchmark_BlockSize_M4(Blackhole bh) throws IOException {
        Parameters params = new Parameters(BlockSize.M4);
        byte[] compressedData = compress(params);
        bh.consume(compressedData);
    }

    @Benchmark
    public void benchmark_WithContentChecksum_Enabled(Blackhole bh) throws IOException {
        Parameters params = new Parameters(BlockSize.M4, true, false, false);
        byte[] compressedData = compress(params);
        bh.consume(compressedData);
    }

    @Benchmark
    public void benchmark_WithContentChecksum_Disabled(Blackhole bh) throws IOException {
        Parameters params = new Parameters(BlockSize.M4, false, false, false);
        byte[] compressedData = compress(params);
        bh.consume(compressedData);
    }

    @Benchmark
    public void benchmark_WithBlockChecksum_Enabled(Blackhole bh) throws IOException {
        Parameters params = new Parameters(BlockSize.M4, true, true, false);
        byte[] compressedData = compress(params);
        bh.consume(compressedData);
    }

    @Benchmark
    public void benchmark_WithBlockChecksum_Disabled(Blackhole bh) throws IOException {
        Parameters params = new Parameters(BlockSize.M4, true, false, false);
        byte[] compressedData = compress(params);
        bh.consume(compressedData);
    }

    @Benchmark
    public void benchmark_WithBlockDependency_Enabled(Blackhole bh) throws IOException {
        // Note: This tests the path where block dependency is enabled.
        Parameters params = new Parameters(BlockSize.M4, true, false, true);
        byte[] compressedData = compress(params);
        bh.consume(compressedData);
    }

    @Benchmark
    public void benchmark_WithBlockDependency_Disabled(Blackhole bh) throws IOException {
        // This tests the path where block dependency is explicitly disabled (should behave like standard compression)
        Parameters params = new Parameters(BlockSize.M4, true, false, false);
        byte[] compressedData = compress(params);
        bh.consume(compressedData);
    }
}
