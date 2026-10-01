package bench.generated.c051;

import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipParameters;
import org.openjdk.jmh.annotations.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.util.concurrent.TimeUnit;
import java.util.zip.Deflater;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class GzipCompressorOutputStreamBenchmark {

    private byte[] payload;
    private byte[] halfPayload;

    @Setup(Level.Trial)
    public void setup() {
        payload = new byte[1024];
        for (int i = 0; i < payload.length; i++) {
            payload[i] = (byte) (i % 256);
        }
        halfPayload = new byte[512];
        System.arraycopy(payload, 0, halfPayload, 0, halfPayload.length);
    }

    @Benchmark
    public byte[] compressDefault() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (GzipCompressorOutputStream gz = new GzipCompressorOutputStream(baos)) {
            gz.write(payload);
        }
        return baos.toByteArray();
    }

    @Benchmark
    public byte[] compressBestCompression() throws IOException {
        GzipParameters params = new GzipParameters();
        params.setCompressionLevel(Deflater.BEST_COMPRESSION);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (GzipCompressorOutputStream gz = new GzipCompressorOutputStream(baos, params)) {
            gz.write(payload);
        }
        return baos.toByteArray();
    }

    @Benchmark
    public byte[] compressBestSpeed() throws IOException {
        GzipParameters params = new GzipParameters();
        params.setCompressionLevel(Deflater.BEST_SPEED);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (GzipCompressorOutputStream gz = new GzipCompressorOutputStream(baos, params)) {
            gz.write(payload);
        }
        return baos.toByteArray();
    }

    @Benchmark
    public byte[] compressWithHeader() throws IOException {
        GzipParameters params = new GzipParameters();
        params.setFileName("test.txt");
        params.setComment("benchmark comment");
        params.setModificationInstant(Instant.ofEpochSecond(1234567890L));
        params.setOperatingSystem(3); // Unix
        params.setHeaderCRC(true);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (GzipCompressorOutputStream gz = new GzipCompressorOutputStream(baos, params)) {
            gz.write(payload);
        }
        return baos.toByteArray();
    }

    @Benchmark
    public byte[] compressSubarray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (GzipCompressorOutputStream gz = new GzipCompressorOutputStream(baos)) {
            gz.write(payload, 0, halfPayload.length);
        }
        return baos.toByteArray();
    }
}
