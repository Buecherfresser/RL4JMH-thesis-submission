package bench.generated.c047;

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
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;
import org.apache.commons.compress.compressors.deflate.DeflateCompressorOutputStream;
import org.apache.commons.compress.compressors.deflate.DeflateParameters;

@State(org.openjdk.jmh.annotations.Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class DeflateCompressorOutputStreamBenchmark {

    private byte[] payload;

    @Setup(org.openjdk.jmh.annotations.Level.Trial)
    public void setUp() {
        int size = 64 * 1024; // 64 KiB payload
        payload = new byte[size];
        new Random(0).nextBytes(payload);
    }

    @Benchmark
    public byte[] writeDefault() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DeflateCompressorOutputStream out = new DeflateCompressorOutputStream(baos);
        out.write(payload, 0, payload.length);
        return baos.toByteArray();
    }

    @Benchmark
    public byte[] writeBestCompression() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DeflateParameters params = new DeflateParameters();
        params.setCompressionLevel(9);
        params.setWithZlibHeader(false);
        DeflateCompressorOutputStream out = new DeflateCompressorOutputStream(baos, params);
        out.write(payload, 0, payload.length);
        return baos.toByteArray();
    }

    @Benchmark
    public int flush() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DeflateCompressorOutputStream out = new DeflateCompressorOutputStream(baos);
        out.flush();
        return baos.size();
    }

    @Benchmark
    public int finish() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DeflateCompressorOutputStream out = new DeflateCompressorOutputStream(baos);
        out.finish();
        return baos.size();
    }

    @Benchmark
    public int close() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DeflateCompressorOutputStream out = new DeflateCompressorOutputStream(baos);
        out.close();
        return baos.size();
    }
}
