package bench.generated.c046;

import org.openjdk.jmh.annotations.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import org.apache.commons.compress.compressors.deflate.DeflateCompressorInputStream;
import org.apache.commons.compress.compressors.deflate.DeflateCompressorOutputStream;
import org.apache.commons.compress.compressors.deflate.DeflateParameters;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class DeflateCompressorInputStreamBenchmark {

    private byte[] compressedWithHeader;
    private byte[] compressedNoHeader;
    private byte[] payload;
    private int payloadLength;
    private byte[] readBuffer;
    private byte[] signature;

    @Setup(Level.Trial)
    public void setup() throws IOException {
        // Build a compressible payload (repeated pattern)
        String pattern = "This is a test payload that should compress well. ";
        byte[] patternBytes = pattern.getBytes(StandardCharsets.UTF_8);
        payloadLength = 10000;
        payload = new byte[payloadLength];
        for (int i = 0; i < payloadLength; i++) {
            payload[i] = patternBytes[i % patternBytes.length];
        }

        readBuffer = new byte[4096];

        // Compress with zlib header (default)
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (DeflateCompressorOutputStream out = new DeflateCompressorOutputStream(baos)) {
            out.write(payload);
        }
        compressedWithHeader = baos.toByteArray();

        // Compress without zlib header
        baos = new ByteArrayOutputStream();
        DeflateParameters params = new DeflateParameters();
        params.setWithZlibHeader(false);
        try (DeflateCompressorOutputStream out = new DeflateCompressorOutputStream(baos, params)) {
            out.write(payload);
        }
        compressedNoHeader = baos.toByteArray();

        // Prepare signature for matches()
        signature = new byte[] { compressedWithHeader[0], compressedWithHeader[1],
                                 compressedWithHeader[2], compressedWithHeader[3] };
    }

    // ---- single-byte read ----

    @Benchmark
    public int readSingleByteWithHeader() throws IOException {
        try (DeflateCompressorInputStream in = new DeflateCompressorInputStream(
                new ByteArrayInputStream(compressedWithHeader))) {
            return in.read();
        }
    }

    @Benchmark
    public int readSingleByteNoHeader() throws IOException {
        try (DeflateCompressorInputStream in = new DeflateCompressorInputStream(
                new ByteArrayInputStream(compressedNoHeader))) {
            return in.read();
        }
    }

    // ---- bulk read (fixed 4096 buffer) ----

    @Benchmark
    public int readBulkWithHeader() throws IOException {
        try (DeflateCompressorInputStream in = new DeflateCompressorInputStream(
                new ByteArrayInputStream(compressedWithHeader))) {
            return in.read(readBuffer, 0, readBuffer.length);
        }
    }

    @Benchmark
    public int readBulkNoHeader() throws IOException {
        try (DeflateCompressorInputStream in = new DeflateCompressorInputStream(
                new ByteArrayInputStream(compressedNoHeader))) {
            return in.read(readBuffer, 0, readBuffer.length);
        }
    }

    // ---- skip ----

    @Benchmark
    public long skipWithHeader() throws IOException {
        try (DeflateCompressorInputStream in = new DeflateCompressorInputStream(
                new ByteArrayInputStream(compressedWithHeader))) {
            return in.skip(payloadLength);
        }
    }

    @Benchmark
    public long skipNoHeader() throws IOException {
        try (DeflateCompressorInputStream in = new DeflateCompressorInputStream(
                new ByteArrayInputStream(compressedNoHeader))) {
            return in.skip(payloadLength);
        }
    }

    // ---- static signature check ----

    @Benchmark
    public boolean matchesSignature() {
        return DeflateCompressorInputStream.matches(signature, signature.length);
    }
}
