package bench.generated.c044;

import org.openjdk.jmh.annotations.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class BZip2CompressorInputStreamBenchmark {

    private byte[] compressedData;

    @Setup(Level.Trial)
    public void setup() throws IOException {
        // Generate a representative payload (repeated text, highly compressible)
        StringBuilder sb = new StringBuilder();
        String line = "The quick brown fox jumps over the lazy dog. ";
        for (int i = 0; i < 20000; i++) {
            sb.append(line);
        }
        byte[] payload = sb.toString().getBytes(StandardCharsets.UTF_8);

        // Compress it once; this becomes the fixed input for all benchmarks
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (BZip2CompressorOutputStream bzOut = new BZip2CompressorOutputStream(baos)) {
            bzOut.write(payload);
        }
        compressedData = baos.toByteArray();
    }

    @Benchmark
    public byte[] readByteByByte() throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(compressedData);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (BZip2CompressorInputStream bzIn = new BZip2CompressorInputStream(bais)) {
            int b;
            while ((b = bzIn.read()) != -1) {
                baos.write(b);
            }
        }
        return baos.toByteArray();
    }

    @Benchmark
    public byte[] readChunked() throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(compressedData);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[8192];
        try (BZip2CompressorInputStream bzIn = new BZip2CompressorInputStream(bais)) {
            int n;
            while ((n = bzIn.read(buffer, 0, buffer.length)) != -1) {
                baos.write(buffer, 0, n);
            }
        }
        return baos.toByteArray();
    }
}
