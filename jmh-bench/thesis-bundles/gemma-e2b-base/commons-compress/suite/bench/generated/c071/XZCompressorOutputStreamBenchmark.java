package bench.generated.c071;

import org.apache.commons.compress.compressors.xz.XZCompressorOutputStream;
import org.tukaani.xz.LZMA2Options;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class XZCompressorOutputStreamBenchmark {

    private static final int PAYLOAD_SIZE = 1024 * 1024; // 1 MB payload
    private byte[] inputPayload;
    private ByteArrayOutputStream outputStream;
    private XZCompressorOutputStream xzCompressorOutputStream;

    @Setup
    public void setup() throws IOException {
        // 1. Create a large, random input payload
        Random random = new Random(42);
        inputPayload = new byte[PAYLOAD_SIZE];
        random.nextBytes(inputPayload);

        // 2. Initialize the output stream buffer
        outputStream = new ByteArrayOutputStream();

        // 3. Initialize the XZCompressorOutputStream using the default builder
        // We use the constructor that takes an OutputStream
        xzCompressorOutputStream = new XZCompressorOutputStream(outputStream);
    }

    /**
     * Benchmark for compressing a fixed payload using XZCompressorOutputStream.
     * This tests the core write operation of the compressor.
     */
    @Benchmark
    public void compressData(Blackhole bh) throws IOException {
        // Reset the output stream for a clean measurement of compression time
        outputStream.reset();

        // Write the fixed payload into the compressor stream
        xzCompressorOutputStream.write(inputPayload, 0, inputPayload.length);

        // Finish the compression process (flushes the encoder)
        xzCompressorOutputStream.finish();

        // Consume the resulting compressed data
        byte[] compressedData = outputStream.toByteArray();
        bh.consume(compressedData);
    }
}
