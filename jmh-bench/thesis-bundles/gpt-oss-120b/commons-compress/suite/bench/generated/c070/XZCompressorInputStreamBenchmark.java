package bench.generated.c070;

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
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.compress.compressors.xz.XZCompressorInputStream;
import org.apache.commons.compress.compressors.xz.XZCompressorOutputStream;
import org.tukaani.xz.XZ;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class XZCompressorInputStreamBenchmark {

    private byte[] originalData;
    private byte[] compressedData;
    private byte[] concatenatedCompressedData;
    private byte[] xzHeader;

    @Setup
    public void setUp() throws IOException {
        // Prepare a deterministic payload
        originalData = new byte[64 * 1024];
        for (int i = 0; i < originalData.length; i++) {
            originalData[i] = (byte) (i & 0xFF);
        }

        // Single XZ compressed payload
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (XZCompressorOutputStream out = new XZCompressorOutputStream(baos)) {
            out.write(originalData);
        }
        compressedData = baos.toByteArray();

        // Concatenated XZ payload (two streams back‑to‑back)
        ByteArrayOutputStream first = new ByteArrayOutputStream();
        try (XZCompressorOutputStream out = new XZCompressorOutputStream(first)) {
            out.write(originalData);
        }
        byte[] firstBytes = first.toByteArray();

        ByteArrayOutputStream second = new ByteArrayOutputStream();
        try (XZCompressorOutputStream out = new XZCompressorOutputStream(second)) {
            out.write(originalData);
        }
        byte[] secondBytes = second.toByteArray();

        concatenatedCompressedData = new byte[firstBytes.length + secondBytes.length];
        System.arraycopy(firstBytes, 0, concatenatedCompressedData, 0, firstBytes.length);
        System.arraycopy(secondBytes, 0, concatenatedCompressedData, firstBytes.length, secondBytes.length);

        // Header bytes for matches() benchmark
        xzHeader = XZ.HEADER_MAGIC.clone();
    }

    private int drain(InputStream in) throws IOException {
        byte[] buf = new byte[8192];
        int total = 0;
        int n;
        while ((n = in.read(buf)) != -1) {
            total += n;
        }
        return total;
    }

    @Benchmark
    public int decompressSingleStream() throws IOException {
        try (XZCompressorInputStream xzIn = new XZCompressorInputStream(new ByteArrayInputStream(compressedData))) {
            return drain(xzIn);
        }
    }

    @Benchmark
    public int decompressConcatenated() throws IOException {
        XZCompressorInputStream.Builder builder = XZCompressorInputStream.builder()
                .setInputStream(new ByteArrayInputStream(concatenatedCompressedData))
                .setDecompressConcatenated(true);
        try (XZCompressorInputStream xzIn = builder.get()) {
            return drain(xzIn);
        }
    }

    @Benchmark
    public int decompressWithMemoryLimit() throws IOException {
        XZCompressorInputStream.Builder builder = XZCompressorInputStream.builder()
                .setInputStream(new ByteArrayInputStream(compressedData))
                .setMemoryLimitKiB(64);
        try (XZCompressorInputStream xzIn = builder.get()) {
            return drain(xzIn);
        }
    }

    @Benchmark
    public int readSingleByte() throws IOException {
        try (XZCompressorInputStream xzIn = new XZCompressorInputStream(new ByteArrayInputStream(compressedData))) {
            return xzIn.read();
        }
    }

    @Benchmark
    public int readIntoBuffer() throws IOException {
        try (XZCompressorInputStream xzIn = new XZCompressorInputStream(new ByteArrayInputStream(compressedData))) {
            byte[] buf = new byte[1024];
            return xzIn.read(buf, 0, buf.length);
        }
    }

    @Benchmark
    public int availableBytes() throws IOException {
        try (XZCompressorInputStream xzIn = new XZCompressorInputStream(new ByteArrayInputStream(compressedData))) {
            return xzIn.available();
        }
    }

    @Benchmark
    public long getCompressedCount() throws IOException {
        try (XZCompressorInputStream xzIn = new XZCompressorInputStream(new ByteArrayInputStream(compressedData))) {
            drain(xzIn);
            return xzIn.getCompressedCount();
        }
    }

    @Benchmark
    public long skipHalf() throws IOException {
        try (XZCompressorInputStream xzIn = new XZCompressorInputStream(new ByteArrayInputStream(compressedData))) {
            long toSkip = originalData.length / 2L;
            return xzIn.skip(toSkip);
        }
    }

    @Benchmark
    public int matchesSignature() {
        return XZCompressorInputStream.matches(xzHeader, xzHeader.length) ? 1 : 0;
    }
}
