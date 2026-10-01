package bench.generated.c064;

import org.apache.commons.compress.compressors.pack200.Pack200CompressorOutputStream;
import org.apache.commons.compress.java.util.jar.Pack200;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class Pack200CompressorOutputStreamBenchmark {

    private byte[] inputData;
    private ByteArrayOutputStream outputStream;
    private Pack200CompressorOutputStream compressorOutputStream;

    // Define a fixed payload size for benchmarking
    private static final int PAYLOAD_SIZE = 1024 * 1024; // 1 MB

    @Setup
    public void setup() throws IOException {
        // 1. Build fixed input data
        this.inputData = new byte[PAYLOAD_SIZE];
        for (int i = 0; i < PAYLOAD_SIZE; i++) {
            inputData[i] = (byte) (i % 256);
        }

        // 2. Initialize output stream buffer
        this.outputStream = new ByteArrayOutputStream();

        // 3. Initialize the compressor stream (using default strategy)
        this.compressorOutputStream = new Pack200CompressorOutputStream(outputStream);
    }

    /**
     * Benchmark for compressing a fixed input payload using Pack200CompressorOutputStream.
     * Measures the time taken to write the input data and finish the compression process.
     */
    @Benchmark
    public void compressData(Blackhole bh) throws IOException {
        // Write the entire input data to the compressor stream
        for (int i = 0; i < inputData.length; i++) {
            compressorOutputStream.write(inputData[i]);
        }

        // Finalize the compression process
        compressorOutputStream.finish();

        // Consume the resulting compressed output to prevent dead code elimination
        byte[] compressedResult = outputStream.toByteArray();
        bh.consume(compressedResult);
    }
}
