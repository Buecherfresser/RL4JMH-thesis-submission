package bench.generated.c056;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.apache.commons.compress.compressors.lz4.FramedLZ4CompressorInputStream;
import org.apache.commons.compress.compressors.lz4.FramedLZ4CompressorOutputStream;
import org.apache.commons.compress.utils.IOUtils;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
@Threads(1)
public class FramedLZ4CompressorInputStreamBenchmark {

    private byte[] compressedRepeating;
    private byte[] compressedRandom;
    private byte[] compressedConcatenated;
    private byte[] signature;
    private byte[] invalidSignature;
    private byte[] buffer;

    @Setup(Level.Trial)
    public void setup() throws IOException {
        byte[] repeating = createRepeatingPayload();
        byte[] random = createRandomPayload();

        compressedRepeating = compress(repeating);
        compressedRandom = compress(random);

        compressedConcatenated = new byte[compressedRepeating.length * 2];
        System.arraycopy(compressedRepeating, 0, compressedConcatenated, 0, compressedRepeating.length);
        System.arraycopy(compressedRepeating, 0, compressedConcatenated, compressedRepeating.length, compressedRepeating.length);

        signature = Arrays.copyOf(compressedRepeating, 4);
        invalidSignature = new byte[] { 0, 1, 2, 3 };
        buffer = new byte[8 * 1024];
    }

    @Benchmark
    public boolean matchesValidSignature() {
        return FramedLZ4CompressorInputStream.matches(signature, signature.length);
    }

    @Benchmark
    public boolean matchesInvalidSignature() {
        return FramedLZ4CompressorInputStream.matches(invalidSignature, invalidSignature.length);
    }

    @Benchmark
    public long constructDefault() throws IOException {
        try (FramedLZ4CompressorInputStream in = new FramedLZ4CompressorInputStream(new ByteArrayInputStream(compressedRepeating))) {
            return in.getCompressedCount();
        }
    }

    @Benchmark
    public long constructConcatenated() throws IOException {
        try (FramedLZ4CompressorInputStream in = new FramedLZ4CompressorInputStream(new ByteArrayInputStream(compressedConcatenated), true)) {
            return in.getCompressedCount();
        }
    }

    @Benchmark
    public int readSingleByte() throws IOException {
        try (FramedLZ4CompressorInputStream in = new FramedLZ4CompressorInputStream(new ByteArrayInputStream(compressedRepeating))) {
            return in.read();
        }
    }

    @Benchmark
    public int readChunk() throws IOException {
        try (FramedLZ4CompressorInputStream in = new FramedLZ4CompressorInputStream(new ByteArrayInputStream(compressedRepeating))) {
            return in.read(buffer, 0, buffer.length);
        }
    }

    @Benchmark
    public byte[] decompressRepeating() throws IOException {
        try (FramedLZ4CompressorInputStream in = new FramedLZ4CompressorInputStream(new ByteArrayInputStream(compressedRepeating))) {
            return IOUtils.toByteArray(in);
        }
    }

    @Benchmark
    public byte[] decompressRandom() throws IOException {
        try (FramedLZ4CompressorInputStream in = new FramedLZ4CompressorInputStream(new ByteArrayInputStream(compressedRandom))) {
            return IOUtils.toByteArray(in);
        }
    }

    @Benchmark
    public byte[] decompressConcatenated() throws IOException {
        try (FramedLZ4CompressorInputStream in = new FramedLZ4CompressorInputStream(new ByteArrayInputStream(compressedConcatenated), true)) {
            return IOUtils.toByteArray(in);
        }
    }

    private byte[] createRepeatingPayload() {
        byte[] data = new byte[1024];
        Arrays.fill(data, (byte) 0x41); // 'A'
        return data;
    }

    private byte[] createRandomPayload() {
        byte[] data = new byte[1024];
        new Random(42).nextBytes(data);
        return data;
    }

    private byte[] compress(byte[] data) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (FramedLZ4CompressorOutputStream out = new FramedLZ4CompressorOutputStream(baos)) {
            out.write(data);
        }
        return baos.toByteArray();
    }
}
