package bench.generated.c046;

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
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;
import java.util.Arrays;
import org.apache.commons.compress.compressors.deflate.DeflateCompressorInputStream;
import org.apache.commons.compress.compressors.deflate.DeflateCompressorOutputStream;
import org.apache.commons.compress.utils.IOUtils;

@State(org.openjdk.jmh.annotations.Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class DeflateCompressorInputStreamBenchmark {

    @State(org.openjdk.jmh.annotations.Scope.Benchmark)
    public static class BenchmarkState {
        byte[] raw;
        byte[] compressed;
        byte[] signature;

        @Setup(org.openjdk.jmh.annotations.Level.Trial)
        public void setup() throws IOException {
            Random rnd = new Random(0);
            raw = new byte[32 * 1024];
            rnd.nextBytes(raw);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try (DeflateCompressorOutputStream dcos = new DeflateCompressorOutputStream(baos)) {
                dcos.write(raw);
                dcos.finish();
            }
            compressed = baos.toByteArray();
            signature = Arrays.copyOf(compressed, Math.min(4, compressed.length));
        }
    }

    @Benchmark
    public int readSingleByte(BenchmarkState s) throws IOException {
        try (DeflateCompressorInputStream in = new DeflateCompressorInputStream(new ByteArrayInputStream(s.compressed))) {
            return in.read();
        }
    }

    @Benchmark
    public int readByteArray(BenchmarkState s) throws IOException {
        byte[] buf = new byte[s.raw.length];
        try (DeflateCompressorInputStream in = new DeflateCompressorInputStream(new ByteArrayInputStream(s.compressed))) {
            return in.read(buf, 0, buf.length);
        }
    }

    @Benchmark
    public int readAll(BenchmarkState s) throws IOException {
        try (DeflateCompressorInputStream in = new DeflateCompressorInputStream(new ByteArrayInputStream(s.compressed))) {
            byte[] out = IOUtils.toByteArray(in);
            return out.length;
        }
    }

    @Benchmark
    public long skipHalf(BenchmarkState s) throws IOException {
        try (DeflateCompressorInputStream in = new DeflateCompressorInputStream(new ByteArrayInputStream(s.compressed))) {
            return in.skip(s.raw.length / 2);
        }
    }

    @Benchmark
    public long getCompressedCountAfterRead(BenchmarkState s) throws IOException {
        try (DeflateCompressorInputStream in = new DeflateCompressorInputStream(new ByteArrayInputStream(s.compressed))) {
            byte[] buf = new byte[1024];
            in.read(buf);
            return in.getCompressedCount();
        }
    }

    @Benchmark
    public boolean matchSignature(BenchmarkState s) {
        return DeflateCompressorInputStream.matches(s.signature, s.signature.length);
    }
}
