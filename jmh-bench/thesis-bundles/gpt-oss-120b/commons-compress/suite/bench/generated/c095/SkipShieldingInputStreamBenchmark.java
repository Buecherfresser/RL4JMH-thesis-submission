package bench.generated.c095;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.utils.SkipShieldingInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.IOException;

/**
 * Benchmarks for {@link SkipShieldingInputStream}.
 *
 * Two scenarios are covered:
 * <ul>
 *   <li>Underlying stream supports efficient {@code skip} (ByteArrayInputStream).</li>
 *   <li>Underlying stream throws on {@code skip} (custom BadSkipInputStream), forcing the wrapper to fall back to {@code read}.</li>
 * </ul>
 */
@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class SkipShieldingInputStreamBenchmark {

    /** Payload used for all benchmarks (64 KiB). */
    private byte[] data;

    /** Number of bytes to skip in each benchmark invocation. */
    private int skipSize;

    /** Simple InputStream that throws on {@code skip}. */
    private static final class BadSkipInputStream extends InputStream {
        private final byte[] src;
        private int pos = 0;

        BadSkipInputStream(byte[] src) {
            this.src = src;
        }

        @Override
        public int read() throws IOException {
            return (pos < src.length) ? (src[pos++] & 0xFF) : -1;
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            if (b == null) {
                throw new NullPointerException();
            }
            if (off < 0 || len < 0 || off + len > b.length) {
                throw new IndexOutOfBoundsException();
            }
            if (pos >= src.length) {
                return -1;
            }
            int toCopy = Math.min(len, src.length - pos);
            System.arraycopy(src, pos, b, off, toCopy);
            pos += toCopy;
            return toCopy;
        }

        @Override
        public long skip(long n) throws IOException {
            throw new IOException("skip not supported");
        }
    }

    @Setup(Level.Trial)
    public void setUp() {
        // 64 KiB of deterministic data
        int size = 64 * 1024;
        data = new byte[size];
        for (int i = 0; i < size; i++) {
            data[i] = (byte) (i & 0xFF);
        }
        // Skip half of the payload; value fits into int for the wrapper's buffer handling
        skipSize = size / 2;
    }

    /**
     * Benchmark where the underlying stream implements {@code skip} efficiently.
     */
    @Benchmark
    public long skipFastUnderlying() throws IOException {
        try (SkipShieldingInputStream sis = new SkipShieldingInputStream(
                new ByteArrayInputStream(data))) {
            return sis.skip(skipSize);
        }
    }

    /**
     * Benchmark where the underlying stream throws on {@code skip},
     * causing {@link SkipShieldingInputStream} to fall back to {@code read}.
     */
    @Benchmark
    public long skipBadUnderlying() throws IOException {
        try (SkipShieldingInputStream sis = new SkipShieldingInputStream(
                new BadSkipInputStream(data))) {
            return sis.skip(skipSize);
        }
    }
}
