package bench.generated.c087;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.utils.CountingInputStream;
import java.io.IOException;
import java.io.InputStream;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class CountingInputStreamBenchmark {

    private CountingInputStream countingInputStream;
    private byte[] buffer;
    private int offset;
    private int length;

    @Setup(Level.Trial)
    public void setUp() {
        countingInputStream = new CountingInputStream(new InfiniteInputStream());
        buffer = new byte[8192];
        offset = 16;
        length = buffer.length - offset;
    }

    @Benchmark
    public int readByte() throws IOException {
        return countingInputStream.read();
    }

    @Benchmark
    public int readByteArray() throws IOException {
        return countingInputStream.read(buffer);
    }

    @Benchmark
    public int readByteArrayPortion() throws IOException {
        return countingInputStream.read(buffer, offset, length);
    }

    @Benchmark
    public int readZeroLengthPortion() throws IOException {
        return countingInputStream.read(buffer, 0, 0);
    }

    @Benchmark
    public long getBytesRead() {
        return countingInputStream.getBytesRead();
    }

    private static final class InfiniteInputStream extends InputStream {
        private final byte[] data;
        private int pos;

        InfiniteInputStream() {
            data = new byte[256];
            for (int i = 0; i < data.length; i++) {
                data[i] = (byte) (i + 1);
            }
        }

        @Override
        public int read() {
            return data[pos++ & (data.length - 1)] & 0xFF;
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            if (b == null) {
                throw new NullPointerException();
            }
            if (off < 0 || len < 0 || len > b.length - off) {
                throw new IndexOutOfBoundsException();
            }
            if (len == 0) {
                return 0;
            }
            int remaining = len;
            int localPos = pos;
            while (remaining > 0) {
                int srcPos = localPos & (data.length - 1);
                int chunk = Math.min(remaining, data.length - srcPos);
                System.arraycopy(data, srcPos, b, off, chunk);
                localPos += chunk;
                off += chunk;
                remaining -= chunk;
            }
            pos = localPos;
            return len;
        }
    }
}
