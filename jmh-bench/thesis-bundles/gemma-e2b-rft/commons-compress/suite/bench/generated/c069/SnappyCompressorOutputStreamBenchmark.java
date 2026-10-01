package bench.generated.c069;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.apache.commons.compress.compressors.snappy.SnappyCompressorOutputStream;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class SnappyCompressorOutputStreamBenchmark {

    private static final int PAYLOAD_SIZE = 1024 * 1024; // 1MB payload
    private byte[] inputData;
    private byte[] compressedData;

    @Setup
    public void setup() throws IOException {
        // 1. Setup input data (fixed payload)
        inputData = new byte[PAYLOAD_SIZE];
        // Fill with some semi-random data to ensure compression is meaningful
        for (int i = 0; i < PAYLOAD_SIZE; i++) {
            inputData[i] = (byte) (i % 256);
        }

        // 2. Setup compressed data (pre-calculate the compressed state)
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             SnappyCompressorOutputStream compressor = new SnappyCompressorOutputStream(baos, inputData.length)) {
            
            compressor.write(inputData, 0, inputData.length);
            compressor.finish();
            compressedData = baos.toByteArray();
        }
    }

    // --- Compression Benchmarks ---

    @Benchmark
    public void benchmarkCompress_DefaultBlockSize(Blackhole bh) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             SnappyCompressorOutputStream compressor = new SnappyCompressorOutputStream(baos, inputData.length)) {
            
            compressor.write(inputData, 0, inputData.length);
            compressor.finish();
            byte[] result = baos.toByteArray();
            bh.consume(result);
        }
    }

    @Benchmark
    public void benchmarkCompress_CustomBlockSize(Blackhole bh) throws IOException {
        final int customBlockSize = 4096; // Example custom block size
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             SnappyCompressorOutputStream compressor = new SnappyCompressorOutputStream(baos, inputData.length, customBlockSize)) {
            
            compressor.write(inputData, 0, inputData.length);
            compressor.finish();
            byte[] result = baos.toByteArray();
            bh.consume(result);
        }
    }
}
