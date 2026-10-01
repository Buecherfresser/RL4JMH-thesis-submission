package bench.generated.c056;

import org.apache.commons.compress.compressors.lz4.FramedLZ4CompressorInputStream;
import org.apache.commons.compress.compressors.lz4.FramedLZ4CompressorOutputStream;
import org.apache.commons.io.IOUtils;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class FramedLZ4CompressorInputStreamBenchmark {

    private byte[] compressedData;
    private InputStream inputStream;
    private ByteArrayInputStream bais;
    private ByteArrayOutputStream baos;

    private static final int PAYLOAD_SIZE = 4 * 1024 * 1024; // 4 MB payload
    private static final Random RANDOM = new Random();

    @Setup
    public void setup() throws IOException {
        // 1. Generate a large, random payload to simulate compressed data
        compressedData = new byte[PAYLOAD_SIZE];
        RANDOM.nextBytes(compressedData);

        // 2. Prepare input stream for decompression benchmarks
        bais = new ByteArrayInputStream(compressedData);
        inputStream = bais;

        // 3. Prepare output stream for compression benchmarks
        baos = new ByteArrayOutputStream();
    }

    // --- Decompression Benchmarks (Reading) ---

    @Benchmark
    public void read_single_byte_stop_after_first_frame(Blackhole bh) throws IOException {
        // Test reading a single byte, expecting it to stop after the first frame (decompressConcatenated = false)
        try (FramedLZ4CompressorInputStream stream = new FramedLZ4CompressorInputStream(bais, false)) {
            int result = stream.read();
            bh.consume(result);
        }
    }

    @Benchmark
    public void read_full_stream_decompress_concatenated(Blackhole bh) throws IOException {
        // Test reading the entire stream (decompressConcatenated = true)
        try (FramedLZ4CompressorInputStream stream = new FramedLZ4CompressorInputStream(bais, true)) {
            // Drain the entire content
            IOUtils.toByteArray(stream);
            bh.consume(true);
        }
    }

    @Benchmark
    public void read_large_chunk(Blackhole bh) throws IOException {
        // Test reading a large chunk of data
        try (FramedLZ4CompressorInputStream stream = new FramedLZ4CompressorInputStream(bais, true)) {
            byte[] buffer = new byte[8192];
            int bytesRead = stream.read(buffer, 0, buffer.length);
            bh.consume(bytesRead);
        }
    }

    // --- Compression Benchmarks (Writing) ---

    @Benchmark
    public void write_full_stream_compression(Blackhole bh) throws IOException {
        // Test writing the entire payload using FramedLZ4CompressorOutputStream
        try (FramedLZ4CompressorOutputStream compressor = new FramedLZ4CompressorOutputStream(baos)) {
            compressor.write(compressedData);
            bh.consume(baos.toByteArray().length);
        }
    }

    @Benchmark
    public void write_partial_stream_compression(Blackhole bh) throws IOException {
        // Test writing a partial chunk of data
        int partialSize = PAYLOAD_SIZE / 4;
        byte[] partialData = new byte[partialSize];
        RANDOM.nextBytes(partialData);

        try (FramedLZ4CompressorOutputStream compressor = new FramedLZ4CompressorOutputStream(baos)) {
            compressor.write(partialData);
            bh.consume(baos.size());
        }
    }

    @Benchmark
    public void write_small_stream_compression(Blackhole bh) throws IOException {
        // Test writing a very small stream
        byte[] smallData = new byte[1024];
        RANDOM.nextBytes(smallData);

        try (FramedLZ4CompressorOutputStream compressor = new FramedLZ4CompressorOutputStream(baos)) {
            compressor.write(smallData);
            bh.consume(baos.size());
        }
    }
}
