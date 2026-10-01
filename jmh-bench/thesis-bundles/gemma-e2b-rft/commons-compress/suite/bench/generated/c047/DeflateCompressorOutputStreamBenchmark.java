package bench.generated.c047;

import org.apache.commons.compress.compressors.deflate.DeflateCompressorInputStream;
import org.apache.commons.compress.compressors.deflate.DeflateCompressorOutputStream;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.Deflater;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class DeflateCompressorOutputStreamBenchmark {

    private byte[] inputData;
    private byte[] compressedData;
    private static final int PAYLOAD_SIZE = 1024 * 1024 * 4; // 4 MB payload

    @Setup(Level.Trial)
    public void setup() throws IOException {
        // 1. Create a large, fixed input payload
        inputData = new byte[PAYLOAD_SIZE];
        // Fill with semi-random data to ensure meaningful compression
        for (int i = 0; i < PAYLOAD_SIZE; i++) {
            inputData[i] = (byte) (i % 256);
        }

        // 2. Pre-calculate the compressed data once (for decompression benchmark)
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             DeflateCompressorOutputStream dos = new DeflateCompressorOutputStream(baos)) {

            dos.write(inputData, 0, inputData.length);
            dos.finish();
            compressedData = baos.toByteArray();
        }
    }

    /**
     * Benchmark for compressing data using DeflateCompressorOutputStream.
     * This tests the write operation.
     */
    @Benchmark
    public void compressData(Blackhole bh) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             DeflateCompressorOutputStream dos = new DeflateCompressorOutputStream(baos)) {

            dos.write(inputData, 0, inputData.length);
            dos.finish();

            byte[] result = baos.toByteArray();
            bh.consume(result);
        }
    }

    /**
     * Benchmark for decompressing data using DeflateCompressorInputStream.
     * This tests the read operation (the inverse of compression).
     */
    @Benchmark
    public void decompressData(Blackhole bh) throws IOException {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(compressedData);
             DeflateCompressorInputStream dis = new DeflateCompressorInputStream(bais)) {

            byte[] result = dis.readAllBytes();
            bh.consume(result);
        }
    }
}
