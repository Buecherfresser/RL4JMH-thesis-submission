package bench.generated.c040;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorOutputStream;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Fork(1)
@Warmup(iterations = 2, time = 1)
@Measurement(iterations = 5, time = 2)
public class CompressorStreamFactoryBenchmark {

    private CompressorStreamFactory factory;
    private byte[] originalPayload;
    private byte[] compressedGzipPayload;
    private byte[] compressedLz4Payload;
    private byte[] compressedZstdPayload;

    private static final int PAYLOAD_SIZE = 1024 * 1024; // 1MB

    @Setup(Level.Trial)
    public void setup() throws IOException {
        factory = CompressorStreamFactory.getSingleton();
        originalPayload = new byte[PAYLOAD_SIZE];
        new Random().nextBytes(originalPayload);

        // Pre-compress payloads for decompression benchmarks
        compressedGzipPayload = compressPayload(originalPayload, CompressorStreamFactory.GZIP);
        compressedLz4Payload = compressPayload(originalPayload, CompressorStreamFactory.LZ4_FRAMED);
        compressedZstdPayload = compressPayload(originalPayload, CompressorStreamFactory.ZSTANDARD);
    }

    /** Helper method to perform compression outside of the benchmark loop */
    private byte[] compressPayload(byte[] data, String format) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (CompressorOutputStream cos = factory.createCompressorOutputStream(format, baos)) {
            cos.write(data);
            cos.finish();
        }
        return baos.toByteArray();
    }

    // --- Compression Benchmarks (Output Stream) ---

    @Benchmark
    public void compressGzip(Blackhole bh) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (CompressorOutputStream cos = factory.createCompressorOutputStream(CompressorStreamFactory.GZIP, baos)) {
            cos.write(originalPayload);
            cos.finish();
        }
        bh.consume(baos.toByteArray());
    }

    @Benchmark
    public void compressLz4Framed(Blackhole bh) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (CompressorOutputStream cos = factory.createCompressorOutputStream(CompressorStreamFactory.LZ4_FRAMED, baos)) {
            cos.write(originalPayload);
            cos.finish();
        }
        bh.consume(baos.toByteArray());
    }

    @Benchmark
    public void compressZstd(Blackhole bh) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (CompressorOutputStream cos = factory.createCompressorOutputStream(CompressorStreamFactory.ZSTANDARD, baos)) {
            cos.write(originalPayload);
            cos.finish();
        }
        bh.consume(baos.toByteArray());
    }

    @Benchmark
    public void compressBzip2(Blackhole bh) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (CompressorOutputStream cos = factory.createCompressorOutputStream(CompressorStreamFactory.BZIP2, baos)) {
            cos.write(originalPayload);
            cos.finish();
        }
        bh.consume(baos.toByteArray());
    }

    @Benchmark
    public void compressDeflate(Blackhole bh) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (CompressorOutputStream cos = factory.createCompressorOutputStream(CompressorStreamFactory.DEFLATE, baos)) {
            cos.write(originalPayload);
            cos.finish();
        }
        bh.consume(baos.toByteArray());
    }

    // --- Decompression Benchmarks (Input Stream) ---

    @Benchmark
    public void decompressGzip(Blackhole bh) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(compressedGzipPayload);
        try (CompressorInputStream cis = factory.createCompressorInputStream(CompressorStreamFactory.GZIP, bais)) {
            byte[] decompressed = new byte[PAYLOAD_SIZE];
            int totalRead = 0;
            int bytesRead;
            while ((bytesRead = cis.read(decompressed, totalRead, PAYLOAD_SIZE - totalRead)) != -1) {
                totalRead += bytesRead;
            }
            bh.consume(decompressed);
        }
    }

    @Benchmark
    public void decompressLz4Framed(Blackhole bh) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(compressedLz4Payload);
        try (CompressorInputStream cis = factory.createCompressorInputStream(CompressorStreamFactory.LZ4_FRAMED, bais)) {
            byte[] decompressed = new byte[PAYLOAD_SIZE];
            int totalRead = 0;
            int bytesRead;
            while ((bytesRead = cis.read(decompressed, totalRead, PAYLOAD_SIZE - totalRead)) != -1) {
                totalRead += bytesRead;
            }
            bh.consume(decompressed);
        }
    }

    @Benchmark
    public void decompressZstd(Blackhole bh) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(compressedZstdPayload);
        try (CompressorInputStream cis = factory.createCompressorInputStream(CompressorStreamFactory.ZSTANDARD, bais)) {
            byte[] decompressed = new byte[PAYLOAD_SIZE];
            int totalRead = 0;
            int bytesRead;
            while ((bytesRead = cis.read(decompressed, totalRead, PAYLOAD_SIZE - totalRead)) != -1) {
                totalRead += bytesRead;
            }
            bh.consume(decompressed);
        }
    }

    @Benchmark
    public void decompressBzip2(Blackhole bh) throws IOException {
        // Re-compressing for BZIP2 since it wasn't pre-calculated
        byte[] compressedData = compressPayload(originalPayload, CompressorStreamFactory.BZIP2);
        ByteArrayInputStream bais = new ByteArrayInputStream(compressedData);
        try (CompressorInputStream cis = factory.createCompressorInputStream(CompressorStreamFactory.BZIP2, bais)) {
            byte[] decompressed = new byte[PAYLOAD_SIZE];
            int totalRead = 0;
            int bytesRead;
            while ((bytesRead = cis.read(decompressed, totalRead, PAYLOAD_SIZE - totalRead)) != -1) {
                totalRead += bytesRead;
            }
            bh.consume(decompressed);
        }
    }
}
