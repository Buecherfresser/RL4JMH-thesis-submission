package bench.generated.c047;

import org.apache.commons.compress.compressors.CompressorOutputStream;
import org.apache.commons.compress.compressors.deflate.DeflateCompressorInputStream;
import org.apache.commons.compress.compressors.deflate.DeflateCompressorOutputStream;
import org.apache.commons.compress.compressors.deflate.DeflateParameters;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.zip.Deflater;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class DeflateCompressorOutputStreamBenchmark {

    // Payload size for testing (e.g., 1MB of arbitrary data)
    private static final int PAYLOAD_SIZE = 1024 * 1024; // 1 MB
    private byte[] inputData;
    private byte[] compressedData;

    // State for compression tests
    private ByteArrayOutputStream baos;
    private DeflateCompressorOutputStream compressorStream;

    // State for decompression tests
    private ByteArrayInputStream bais;
    private DeflateCompressorInputStream decompressorStream;
    private byte[] decompressedData;

    @Setup(Level.Trial)
    public void setup() throws IOException {
        // 1. Build a fixed, non-final payload for compression testing
        inputData = new byte[PAYLOAD_SIZE];
        for (int i = 0; i < PAYLOAD_SIZE; i++) {
            inputData[i] = (byte) (i % 256);
        }

        // 2. Compression Setup (DeflateCompressorOutputStream)
        baos = new ByteArrayOutputStream();
        // Using default parameters for simplicity, or custom parameters if needed
        compressorStream = new DeflateCompressorOutputStream(baos);

        // 3. Perform initial compression to generate the payload for decompression tests
        compressorStream.write(inputData, 0, inputData.length);
        compressorStream.finish();
        compressedData = baos.toByteArray();

        // 4. Decompression Setup (DeflateCompressorInputStream)
        bais = new ByteArrayInputStream(compressedData);
        decompressorStream = new DeflateCompressorInputStream(bais);

        // 5. Perform initial decompression to generate the result for verification
        decompressedData = new byte[compressedData.length];
        int readBytes = bais.read(decompressedData);
        if (readBytes != compressedData.length) {
            throw new IllegalStateException("Decompression failed to read full payload.");
        }
    }

    @Benchmark
    public void compressData(Blackhole bh) throws IOException {
        // Reset state for this invocation
        baos = new ByteArrayOutputStream();
        compressorStream = new DeflateCompressorOutputStream(baos);

        // Perform the write operation
        compressorStream.write(inputData, 0, inputData.length);
        compressorStream.finish();

        // Consume the result (the compressed byte array)
        bh.consume(baos.toByteArray());
    }

    @Benchmark
    public void decompressData(Blackhole bh) throws IOException {
        // Reset state for this invocation
        bais = new ByteArrayInputStream(compressedData);
        decompressorStream = new DeflateCompressorInputStream(bais);

        // Perform the read operation
        byte[] result = new byte[compressedData.length];
        int readBytes = decompressorStream.read(result);

        // Consume the result (the decompressed byte array)
        bh.consume(result);
    }
}
