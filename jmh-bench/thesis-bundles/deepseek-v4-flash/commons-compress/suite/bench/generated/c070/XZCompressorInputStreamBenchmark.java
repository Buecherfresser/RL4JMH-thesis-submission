package bench.generated.c070;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.compress.compressors.xz.XZCompressorInputStream;
import org.apache.commons.compress.compressors.xz.XZCompressorOutputStream;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class XZCompressorInputStreamBenchmark {

    private byte[] compressedSingle;
    private byte[] compressedConcat;
    private byte[] magicBytes;
    private byte[] buffer8k;
    private byte[] buffer256;

    @Setup(Level.Trial)
    public void setup() throws IOException {
        // Create a representative payload (10 KB of patterned data)
        byte[] payload = new byte[10 * 1024];
        for (int i = 0; i < payload.length; i++) {
            payload[i] = (byte) (i % 251);
        }

        // Compress a single XZ stream
        compressedSingle = compress(payload);

        // Compress two streams and concatenate them for the concatenated test
        byte[] first = compress(payload);
        byte[] second = compress(payload);
        compressedConcat = new byte[first.length + second.length];
        System.arraycopy(first, 0, compressedConcat, 0, first.length);
        System.arraycopy(second, 0, compressedConcat, first.length, second.length);

        // Magic bytes for the matches() test
        magicBytes = new byte[] { (byte) 0xFD, '7', 'z', 'X', 'Z', 0x00 };

        // Pre-allocate read buffers
        buffer8k = new byte[8192];
        buffer256 = new byte[256];
    }

    private static byte[] compress(byte[] data) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (XZCompressorOutputStream xzOut = new XZCompressorOutputStream(baos)) {
            xzOut.write(data);
        }
        return baos.toByteArray();
    }

    // Read all bytes one at a time using read()
    @Benchmark
    public int readSingleByte() throws IOException {
        try (XZCompressorInputStream in = new XZCompressorInputStream(new ByteArrayInputStream(compressedSingle))) {
            int count = 0;
            while (in.read() != -1) {
                count++;
            }
            return count;
        }
    }

    // Read all bytes using a large buffer (8 KB)
    @Benchmark
    public int readBuffer8k() throws IOException {
        try (XZCompressorInputStream in = new XZCompressorInputStream(new ByteArrayInputStream(compressedSingle))) {
            int count = 0;
            int n;
            while ((n = in.read(buffer8k, 0, buffer8k.length)) != -1) {
                count += n;
            }
            return count;
        }
    }

    // Read all bytes using a small buffer (256 B)
    @Benchmark
    public int readBuffer256() throws IOException {
        try (XZCompressorInputStream in = new XZCompressorInputStream(new ByteArrayInputStream(compressedSingle))) {
            int count = 0;
            int n;
            while ((n = in.read(buffer256, 0, buffer256.length)) != -1) {
                count += n;
            }
            return count;
        }
    }

    // Skip a fixed number of decompressed bytes (e.g., 1000)
    @Benchmark
    public long skipFixed() throws IOException {
        try (XZCompressorInputStream in = new XZCompressorInputStream(new ByteArrayInputStream(compressedSingle))) {
            return in.skip(1000);
        }
    }

    // Static matches() check
    @Benchmark
    public boolean matches() {
        return XZCompressorInputStream.matches(magicBytes, magicBytes.length);
    }

    // Decompress a concatenated stream with decompressConcatenated = true
    @Benchmark
    public int readConcat() throws IOException {
        try (XZCompressorInputStream in = new XZCompressorInputStream(new ByteArrayInputStream(compressedConcat), true)) {
            int count = 0;
            int n;
            while ((n = in.read(buffer8k, 0, buffer8k.length)) != -1) {
                count += n;
            }
            return count;
        }
    }

    // Decompress a concatenated stream with decompressConcatenated = false (stops after first stream)
    @Benchmark
    public int readNonConcat() throws IOException {
        try (XZCompressorInputStream in = new XZCompressorInputStream(new ByteArrayInputStream(compressedConcat), false)) {
            int count = 0;
            int n;
            while ((n = in.read(buffer8k, 0, buffer8k.length)) != -1) {
                count += n;
            }
            return count;
        }
    }
}
