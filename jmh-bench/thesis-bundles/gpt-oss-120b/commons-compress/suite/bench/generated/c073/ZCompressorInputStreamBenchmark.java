package bench.generated.c073;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.apache.commons.compress.compressors.z.ZCompressorInputStream;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ZCompressorInputStreamBenchmark {

    private byte[] compressedData;

    @Setup(Level.Trial)
    public void setup() {
        // Minimal valid .Z header (magic bytes + third byte with block mode = 0, max code size = 0)
        compressedData = new byte[] { (byte) 0x1f, (byte) 0x9d, 0x00 };
    }

    @Benchmark
    public ZCompressorInputStream constructAndClose() throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(compressedData);
        ZCompressorInputStream zis = new ZCompressorInputStream(bais);
        zis.close();
        return zis;
    }

    @Benchmark
    public int readSingleByte() throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(compressedData);
        try (ZCompressorInputStream zis = new ZCompressorInputStream(bais)) {
            int total = 0;
            while (zis.read() != -1) {
                total++;
            }
            return total;
        }
    }

    @Benchmark
    public int readByteArray() throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(compressedData);
        try (ZCompressorInputStream zis = new ZCompressorInputStream(bais)) {
            byte[] buffer = new byte[8192];
            int total = 0;
            int n;
            while ((n = zis.read(buffer, 0, buffer.length)) != -1) {
                total += n;
            }
            return total;
        }
    }

    @Benchmark
    public boolean matchesSignature() {
        return ZCompressorInputStream.matches(compressedData, compressedData.length);
    }
}
