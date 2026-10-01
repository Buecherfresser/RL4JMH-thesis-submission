package bench.generated.c079;

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
import java.io.IOException;
import java.nio.ByteOrder;
import org.apache.commons.compress.utils.BitInputStream;

@State(org.openjdk.jmh.annotations.Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class BitInputStreamBenchmark {

    private byte[] payload;

    @Setup
    public void setUp() {
        // 1 KiB of pseudo‑random data
        payload = new byte[1024];
        for (int i = 0; i < payload.length; i++) {
            payload[i] = (byte) (i * 31);
        }
    }

    private BitInputStream newStream(ByteOrder order) {
        return new BitInputStream(new ByteArrayInputStream(payload), order);
    }

    @Benchmark
    public int benchmarkReadBit() throws IOException {
        BitInputStream bis = newStream(ByteOrder.BIG_ENDIAN);
        return bis.readBit();
    }

    @Benchmark
    public long benchmarkReadBits8() throws IOException {
        BitInputStream bis = newStream(ByteOrder.BIG_ENDIAN);
        return bis.readBits(8);
    }

    @Benchmark
    public long benchmarkReadBits16() throws IOException {
        BitInputStream bis = newStream(ByteOrder.BIG_ENDIAN);
        return bis.readBits(16);
    }

    @Benchmark
    public long benchmarkReadBits32() throws IOException {
        BitInputStream bis = newStream(ByteOrder.BIG_ENDIAN);
        return bis.readBits(32);
    }

    @Benchmark
    public long benchmarkReadBits63() throws IOException {
        BitInputStream bis = newStream(ByteOrder.BIG_ENDIAN);
        return bis.readBits(63);
    }

    @Benchmark
    public long benchmarkReadBits8LittleEndian() throws IOException {
        BitInputStream bis = newStream(ByteOrder.LITTLE_ENDIAN);
        return bis.readBits(8);
    }

    @Benchmark
    public void benchmarkAlignWithByteBoundary(Blackhole bh) throws IOException {
        BitInputStream bis = newStream(ByteOrder.BIG_ENDIAN);
        bis.alignWithByteBoundary();
        bh.consume(bis);
    }

    @Benchmark
    public long benchmarkBitsAvailable() throws IOException {
        BitInputStream bis = newStream(ByteOrder.BIG_ENDIAN);
        return bis.bitsAvailable();
    }

    @Benchmark
    public int benchmarkBitsCached() throws IOException {
        BitInputStream bis = newStream(ByteOrder.BIG_ENDIAN);
        return bis.bitsCached();
    }

    @Benchmark
    public void benchmarkClearBitCache(Blackhole bh) throws IOException {
        BitInputStream bis = newStream(ByteOrder.BIG_ENDIAN);
        bis.clearBitCache();
        bh.consume(bis);
    }

    @Benchmark
    public long benchmarkGetBytesRead() throws IOException {
        BitInputStream bis = newStream(ByteOrder.BIG_ENDIAN);
        return bis.getBytesRead();
    }
}
