package bench.generated.c071;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.compressors.xz.XZCompressorOutputStream;
import org.tukaani.xz.LZMA2Options;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class XZCompressorOutputStreamBenchmark {

    private byte[] smallPayload;
    private byte[] largePayload;
    private LZMA2Options preset9Options;

    @Setup(Level.Trial)
    public void setUp() throws Exception {
        Random rnd = new Random(0x1234);
        smallPayload = new byte[1024]; // 1 KiB
        rnd.nextBytes(smallPayload);
        largePayload = new byte[256 * 1024]; // 256 KiB
        rnd.nextBytes(largePayload);

        preset9Options = new LZMA2Options();
        preset9Options.setPreset(9);
    }

    @Benchmark
    public int compressSmallDefault() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (XZCompressorOutputStream xz = new XZCompressorOutputStream(baos)) {
            xz.write(smallPayload, 0, smallPayload.length);
            xz.finish();
        }
        return baos.size();
    }

    @Benchmark
    public int compressLargeDefault() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (XZCompressorOutputStream xz = new XZCompressorOutputStream(baos)) {
            xz.write(largePayload, 0, largePayload.length);
            xz.finish();
        }
        return baos.size();
    }

    @Benchmark
    public int compressSmallPreset() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (XZCompressorOutputStream xz = new XZCompressorOutputStream(baos, 6)) {
            xz.write(smallPayload, 0, smallPayload.length);
            xz.finish();
        }
        return baos.size();
    }

    @Benchmark
    public int compressLargePreset() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (XZCompressorOutputStream xz = new XZCompressorOutputStream(baos, 6)) {
            xz.write(largePayload, 0, largePayload.length);
            xz.finish();
        }
        return baos.size();
    }

    @Benchmark
    public int compressSmallCustomOptions() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        XZCompressorOutputStream xz = XZCompressorOutputStream.builder()
                .setOutputStream(baos)
                .setLzma2Options(preset9Options)
                .get();
        try {
            xz.write(smallPayload, 0, smallPayload.length);
            xz.finish();
        } finally {
            xz.close();
        }
        return baos.size();
    }

    @Benchmark
    public int compressLargeCustomOptions() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        XZCompressorOutputStream xz = XZCompressorOutputStream.builder()
                .setOutputStream(baos)
                .setLzma2Options(preset9Options)
                .get();
        try {
            xz.write(largePayload, 0, largePayload.length);
            xz.finish();
        } finally {
            xz.close();
        }
        return baos.size();
    }
}
