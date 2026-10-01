package bench.generated.c060;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.concurrent.TimeUnit;

import org.apache.commons.compress.compressors.lzma.LZMACompressorInputStream;
import org.apache.commons.compress.compressors.lzma.LZMACompressorOutputStream;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class LZMACompressorInputStreamBenchmark {

    private static final int PAYLOAD_SIZE = 100_000;

    @State(Scope.Benchmark)
    public static class DataState {
        byte[] compressed;
        byte[] buffer = new byte[8192];
        byte[] signature = new byte[] {0x5d, 0x00, 0x00, 0x00, 0x00};
        int payloadSize;

        @Setup(Level.Trial)
        public void setup() throws Exception {
            // Generate a deterministic, compressible payload.
            byte[] payload = new byte[PAYLOAD_SIZE];
            for (int i = 0; i < payload.length; i++) {
                payload[i] = (byte) (i % 256);
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try (LZMACompressorOutputStream lzmaOut = new LZMACompressorOutputStream(baos)) {
                lzmaOut.write(payload);
            }
            compressed = baos.toByteArray();
            payloadSize = payload.length;
        }
    }

    @Benchmark
    public long decompressChunk(DataState state, Blackhole bh) throws Exception {
        try (LZMACompressorInputStream lzmaIn = new LZMACompressorInputStream(
                new ByteArrayInputStream(state.compressed))) {
            int n;
            long total = 0;
            while ((n = lzmaIn.read(state.buffer, 0, state.buffer.length)) != -1) {
                total += n;
            }
            bh.consume(state.buffer); // ensure buffer isn't optimized away
            return total;
        }
    }

    @Benchmark
    public long decompressSingleByte(DataState state) throws Exception {
        try (LZMACompressorInputStream lzmaIn = new LZMACompressorInputStream(
                new ByteArrayInputStream(state.compressed))) {
            int b;
            long total = 0;
            while ((b = lzmaIn.read()) != -1) {
                total++;
            }
            return total;
        }
    }

    @Benchmark
    public long decompressSkipThenRead(DataState state) throws Exception {
        try (LZMACompressorInputStream lzmaIn = new LZMACompressorInputStream(
                new ByteArrayInputStream(state.compressed))) {
            long skipped = lzmaIn.skip(state.payloadSize / 2);
            byte[] tmp = new byte[1024];
            long total = skipped;
            while (total < state.payloadSize) {
                int n = lzmaIn.read(tmp, 0, (int) Math.min(tmp.length, state.payloadSize - total));
                if (n == -1) break;
                total += n;
            }
            return total;
        }
    }

    @Benchmark
    public long getCompressedCount(DataState state) throws Exception {
        try (LZMACompressorInputStream lzmaIn = new LZMACompressorInputStream(
                new ByteArrayInputStream(state.compressed))) {
            byte[] buf = new byte[8192];
            while (lzmaIn.read(buf) != -1) {
                // drain
            }
            return lzmaIn.getCompressedCount();
        }
    }

    @Benchmark
    public int available(DataState state) throws Exception {
        try (LZMACompressorInputStream lzmaIn = new LZMACompressorInputStream(
                new ByteArrayInputStream(state.compressed))) {
            return lzmaIn.available();
        }
    }

    @Benchmark
    public boolean matchesSignature(DataState state) {
        return LZMACompressorInputStream.matches(state.signature, state.signature.length);
    }

    @Benchmark
    public long decompressWithMemoryLimit(DataState state) throws Exception {
        try (LZMACompressorInputStream lzmaIn = new LZMACompressorInputStream(
                new ByteArrayInputStream(state.compressed), 1024 * 1024)) {
            byte[] buf = new byte[8192];
            long total = 0;
            int n;
            while ((n = lzmaIn.read(buf)) != -1) {
                total += n;
            }
            return total;
        }
    }

    @Benchmark
    public long decompressUsingBuilder(DataState state) throws Exception {
        try (LZMACompressorInputStream lzmaIn = LZMACompressorInputStream.builder()
                .setInputStream(new ByteArrayInputStream(state.compressed))
                .get()) {
            byte[] buf = new byte[8192];
            long total = 0;
            int n;
            while ((n = lzmaIn.read(buf)) != -1) {
                total += n;
            }
            return total;
        }
    }
}
