package bench.generated.c072;

import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.apache.commons.compress.compressors.xz.XZCompressorInputStream;
import org.apache.commons.compress.compressors.xz.XZCompressorOutputStream;
import org.apache.commons.compress.compressors.xz.XZUtils;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class XZUtilsBenchmark {

    private byte[] inputData;
    private byte[] compressedData;
    private byte[] decompressedData;
    private final int DATA_SIZE = 1024 * 1024; // 1 MB payload

    @Setup
    public void setup() throws IOException {
        // 1. Create a fixed, non-final input payload
        inputData = new byte[DATA_SIZE];
        for (int i = 0; i < DATA_SIZE; i++) {
            inputData[i] = (byte) (i % 256);
        }

        // 2. Pre-compress the data once for decompression benchmarks
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             XZCompressorOutputStream xzOut = new XZCompressorOutputStream(baos)) {
            
            xzOut.write(inputData);
            xzOut.finish();
            compressedData = baos.toByteArray();
        }
    }

    @Benchmark
    public void compressData(Blackhole bh) throws IOException {
        // Benchmark: Compression (Write)
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             XZCompressorOutputStream xzOut = new XZCompressorOutputStream(baos)) {

            xzOut.write(inputData);
            xzOut.finish();
            byte[] result = baos.toByteArray();
            bh.consume(result);
        }
    }

    @Benchmark
    public void decompressData(Blackhole bh) throws IOException {
        // Benchmark: Decompression (Read)
        try (ByteArrayInputStream bais = new ByteArrayInputStream(compressedData);
             XZCompressorInputStream xzIn = new XZCompressorInputStream(bais)) {

            byte[] result = new byte[DATA_SIZE];
            int bytesRead = xzIn.read(result, 0, DATA_SIZE);
            bh.consume(result);
        }
    }

    @Benchmark
    public void checkMagicBytes(Blackhole bh) {
        // Benchmark: Utility method check
        byte[] magic = { (byte) 0xFD, '7', 'z', 'X', 'Z', '\0' };
        boolean result = XZUtils.matches(magic, magic.length);
        bh.consume(result);
    }

    @Benchmark
    public void getCompressedFilename(Blackhole bh) {
        // Benchmark: Filename mapping utility
        String fileName = "my_file.tar";
        String result = XZUtils.getCompressedFileName(fileName);
        bh.consume(result);
    }

    @Benchmark
    public void getUncompressedFilename(Blackhole bh) {
        // Benchmark: Filename mapping utility
        String fileName = "archive.txz";
        String result = XZUtils.getUncompressedFileName(fileName);
        bh.consume(result);
    }
}
