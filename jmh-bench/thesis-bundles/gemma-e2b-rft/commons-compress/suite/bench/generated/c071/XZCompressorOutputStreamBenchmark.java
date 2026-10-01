package bench.generated.c071;

import org.apache.commons.compress.compressors.xz.XZCompressorOutputStream;
import org.tukaani.xz.LZMA2Options;
import org.tukaani.xz.XZOutputStream;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class XZCompressorOutputStreamBenchmark {

    // Input data payload
    private byte[] inputData;
    private final int payloadSize = 1024 * 1024; // 1MB payload

    // State for compression stream instances
    private XZCompressorOutputStream compressorDefault;
    private XZCompressorOutputStream compressorHighPreset;

    @Setup
    public void setup() throws IOException {
        // 1. Build a fixed, large payload
        this.inputData = new byte[payloadSize];
        for (int i = 0; i < payloadSize; i++) {
            inputData[i] = (byte) (i % 256);
        }

        // 2. Initialize compressor instances
        // Default preset (default LZMA2Options)
        this.compressorDefault = XZCompressorOutputStream.builder().get();

        // High compression preset (Preset 9)
        this.compressorHighPreset = XZCompressorOutputStream.builder()
                .setLzma2Options(new LZMA2Options(9))
                .get();
    }

    /**
     * Benchmark for compressing data using the default XZ preset.
     */
    @Benchmark
    public void compressDefault(Blackhole bh) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ByteArrayInputStream bais = new ByteArrayInputStream(inputData);
             XZCompressorOutputStream xzOut = compressorDefault) {

            // Write the input data through the compressor stream
            bais.transferTo(xzOut);
            xzOut.finish();

            // Consume the resulting compressed data
            byte[] compressedData = baos.toByteArray();
            bh.consume(compressedData);
        }
    }

    /**
     * Benchmark for compressing data using a high compression preset (Preset 9).
     */
    @Benchmark
    public void compressHighPreset(Blackhole bh) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ByteArrayInputStream bais = new ByteArrayInputStream(inputData);
             XZCompressorOutputStream xzOut = compressorHighPreset) {

            // Write the input data through the compressor stream
            bais.transferTo(xzOut);
            xzOut.finish();

            // Consume the resulting compressed data
            byte[] compressedData = baos.toByteArray();
            bh.consume(compressedData);
        }
    }
}
