package bench.generated.c061;

import org.openjdk.jmh.annotations.*;
import java.util.concurrent.TimeUnit;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;
import org.apache.commons.compress.compressors.lzma.LZMACompressorOutputStream;
import org.tukaani.xz.LZMA2Options;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class LZMACompressorOutputStreamBenchmark {

    private byte[] smallPayload;
    private byte[] largePayload;
    private LZMA2Options defaultOptions;
    private LZMA2Options preset1Options;
    private LZMA2Options preset9Options;

    @Setup(Level.Trial)
    public void setup() throws Exception {
        Random random = new Random(12345);
        smallPayload = new byte[1024];
        largePayload = new byte[1024 * 1024];
        random.nextBytes(smallPayload);
        random.nextBytes(largePayload);
        defaultOptions = new LZMA2Options();
        preset1Options = new LZMA2Options(1);
        preset9Options = new LZMA2Options(9);
    }

    @Benchmark
    public byte[] writeAndFinishDefaultSmall() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (LZMACompressorOutputStream out = new LZMACompressorOutputStream(baos)) {
            out.write(smallPayload, 0, smallPayload.length);
            out.finish();
        }
        return baos.toByteArray();
    }

    @Benchmark
    public byte[] writeAndFinishDefaultLarge() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (LZMACompressorOutputStream out = new LZMACompressorOutputStream(baos)) {
            out.write(largePayload, 0, largePayload.length);
            out.finish();
        }
        return baos.toByteArray();
    }

    @Benchmark
    public byte[] writeAndFinishPreset1Small() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (LZMACompressorOutputStream out = LZMACompressorOutputStream.builder()
                .setOutputStream(baos)
                .setLzma2Options(preset1Options)
                .get()) {
            out.write(smallPayload, 0, smallPayload.length);
            out.finish();
        }
        return baos.toByteArray();
    }

    @Benchmark
    public byte[] writeAndFinishPreset9Small() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (LZMACompressorOutputStream out = LZMACompressorOutputStream.builder()
                .setOutputStream(baos)
                .setLzma2Options(preset9Options)
                .get()) {
            out.write(smallPayload, 0, smallPayload.length);
            out.finish();
        }
        return baos.toByteArray();
    }
}
