package bench.generated.c060;

import org.apache.commons.compress.compressors.lzma.LZMACompressorInputStream;
import org.apache.commons.compress.compressors.lzma.LZMACompressorOutputStream;
import org.apache.commons.io.IOUtils;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class LZMACompressorInputStreamBenchmark {

    private byte[] compressedData;
    private final byte[] originalData = "This is a test string used for LZMA compression benchmarking.".getBytes();
    private final int bufferSize = 4096;

    @Setup(Level.Trial)
    public void setup() throws IOException {
        // 1. Compress the original data into a byte array
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             LZMACompressorOutputStream lzmaOut = new LZMACompressorOutputStream(baos)) {

            lzmaOut.write(originalData);
            lzmaOut.finish();
            compressedData = baos.toByteArray();
        }
    }

    /**
     * Benchmarks reading a single byte from the decompressed stream.
     */
    @Benchmark
    public int readSingleByte(Blackhole bh) throws IOException {
        // Recreate the stream for each invocation to ensure state is reset
        try (InputStream bais = new ByteArrayInputStream(compressedData);
             LZMACompressorInputStream lzmaIn = new LZMACompressorInputStream(bais)) {

            int result = lzmaIn.read();
            bh.consume(result);
            return result;
        }
    }

    /**
     * Benchmarks reading a bulk chunk of data from the decompressed stream.
     */
    @Benchmark
    public int readBulkData(Blackhole bh) throws IOException {
        // Recreate the stream for each invocation
        try (InputStream bais = new ByteArrayInputStream(compressedData);
             LZMACompressorInputStream lzmaIn = new LZMACompressorInputStream(bais)) {

            byte[] buffer = new byte[bufferSize];
            int result = lzmaIn.read(buffer, 0, bufferSize);
            bh.consume(result);
            return result;
        }
    }

    /**
     * Benchmarks checking the available bytes remaining in the stream.
     */
    @Benchmark
    public long availableBytes(Blackhole bh) throws IOException {
        // Recreate the stream for each invocation
        try (InputStream bais = new ByteArrayInputStream(compressedData);
             LZMACompressorInputStream lzmaIn = new LZMACompressorInputStream(bais)) {

            long result = lzmaIn.available();
            bh.consume(result);
            return result;
        }
    }

    /**
     * Benchmarks skipping a specified number of bytes in the stream.
     */
    @Benchmark
    public long skipBytes(Blackhole bh) throws IOException {
        // Recreate the stream for each invocation
        try (InputStream bais = new ByteArrayInputStream(compressedData);
             LZMACompressorInputStream lzmaIn = new LZMACompressorInputStream(bais)) {

            long result = lzmaIn.skip(100);
            bh.consume(result);
            return result;
        }
    }

    /**
     * Benchmarks retrieving the count of compressed bytes read so far.
     */
    @Benchmark
    public long getCompressedCount(Blackhole bh) throws IOException {
        // Recreate the stream for each invocation
        try (InputStream bais = new ByteArrayInputStream(compressedData);
             LZMACompressorInputStream lzmaIn = new LZMACompressorInputStream(bais)) {

            long result = lzmaIn.getCompressedCount();
            bh.consume(result);
            return result;
        }
    }
}
