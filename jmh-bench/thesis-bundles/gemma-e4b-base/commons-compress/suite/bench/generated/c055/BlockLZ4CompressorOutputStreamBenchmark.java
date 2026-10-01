package bench.generated.c055;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.compressors.lz4.BlockLZ4CompressorOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class BlockLZ4CompressorOutputStreamBenchmark {

    private byte[] inputData;
    private static final int DATA_SIZE = 1024 * 10; // 10 KB input data

    @Setup(Level.Trial)
    public void setup() {
        // Generate representative input data
        inputData = new byte[DATA_SIZE];
        Arrays.fill(inputData, (byte) 0xAA);
        // Add some variation to ensure compression happens
        for (int i = 0; i < DATA_SIZE; i++) {
            inputData[i] = (byte) (i % 256);
        }
    }

    /**
     * Benchmarks the core compression operation using write(byte[] data, int off, int len).
     * A fresh stream is created and closed for each invocation to ensure state isolation.
     */
    @Benchmark
    public void benchmarkWriteData(Blackhole bh) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        // Initialize the compressor stream
        BlockLZ4CompressorOutputStream compressor = new BlockLZ4CompressorOutputStream(baos);

        // Perform the core write operation
        compressor.write(inputData, 0, inputData.length);

        // Finish the compression stream
        compressor.finish();

        // Consume the resulting compressed bytes
        byte[] compressedData = baos.toByteArray();
        bh.consume(compressedData);
    }

    /**
     * Benchmarks the single byte write operation.
     */
    @Benchmark
    public void benchmarkWriteSingleByte(Blackhole bh) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        BlockLZ4CompressorOutputStream compressor = new BlockLZ4CompressorOutputStream(baos);

        // Write a single byte
        compressor.write(0x42);

        // Finish the compression stream
        compressor.finish();

        // Consume the resulting compressed bytes
        byte[] compressedData = baos.toByteArray();
        bh.consume(compressedData);
    }
}
