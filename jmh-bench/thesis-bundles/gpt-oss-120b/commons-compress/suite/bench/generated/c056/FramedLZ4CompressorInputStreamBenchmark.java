package bench.generated.c056;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.compressors.lz4.FramedLZ4CompressorInputStream;
import org.apache.commons.compress.compressors.lz4.FramedLZ4CompressorOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;

@State(org.openjdk.jmh.annotations.Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class FramedLZ4CompressorInputStreamBenchmark {

    private byte[] originalData;
    private byte[] compressedSingle;
    private byte[] compressedConcat;
    private int originalLength;

    @Setup
    public void setup() throws IOException {
        originalLength = 64 * 1024; // 64 KiB
        originalData = new byte[originalLength];
        new Random(0xdeadbeefL).nextBytes(originalData);

        // Single frame compression
        ByteArrayOutputStream baosSingle = new ByteArrayOutputStream();
        try (FramedLZ4CompressorOutputStream out = new FramedLZ4CompressorOutputStream(baosSingle)) {
            out.write(originalData);
        }
        compressedSingle = baosSingle.toByteArray();

        // Concatenated frames compression (two identical frames)
        ByteArrayOutputStream baosFirst = new ByteArrayOutputStream();
        try (FramedLZ4CompressorOutputStream out = new FramedLZ4CompressorOutputStream(baosFirst)) {
            out.write(originalData);
        }
        byte[] first = baosFirst.toByteArray();

        ByteArrayOutputStream baosSecond = new ByteArrayOutputStream();
        try (FramedLZ4CompressorOutputStream out = new FramedLZ4CompressorOutputStream(baosSecond)) {
            out.write(originalData);
        }
        byte[] second = baosSecond.toByteArray();

        compressedConcat = new byte[first.length + second.length];
        System.arraycopy(first, 0, compressedConcat, 0, first.length);
        System.arraycopy(second, 0, compressedConcat, first.length, second.length);
    }

    @Benchmark
    public int readSingleByte() throws IOException {
        FramedLZ4CompressorInputStream in = new FramedLZ4CompressorInputStream(
                new ByteArrayInputStream(compressedSingle));
        return in.read();
    }

    @Benchmark
    public int readByteArrayFull() throws IOException {
        FramedLZ4CompressorInputStream in = new FramedLZ4CompressorInputStream(
                new ByteArrayInputStream(compressedSingle));
        byte[] buf = new byte[originalLength];
        return in.read(buf, 0, buf.length);
    }

    @Benchmark
    public int readByteArrayPartial() throws IOException {
        FramedLZ4CompressorInputStream in = new FramedLZ4CompressorInputStream(
                new ByteArrayInputStream(compressedSingle));
        byte[] buf = new byte[256];
        return in.read(buf, 0, buf.length);
    }

    @Benchmark
    public int readConcatenatedFull() throws IOException {
        FramedLZ4CompressorInputStream in = new FramedLZ4CompressorInputStream(
                new ByteArrayInputStream(compressedConcat), true);
        byte[] buf = new byte[originalLength * 2];
        return in.read(buf, 0, buf.length);
    }
}
