package bench.generated.c079;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.apache.commons.compress.utils.BitInputStream;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class BitInputStreamBenchmark {

    private byte[] payload;

    @Setup(Level.Trial)
    public void setup() {
        payload = new byte[64 * 1024];
        new Random(0x12345678L).nextBytes(payload);
    }

    private BitInputStream newStream(ByteOrder order) {
        return new BitInputStream(new ByteArrayInputStream(payload), order);
    }

    // ===== readBits, BIG_ENDIAN =====

    @Benchmark
    public long readBits1BigEndian() throws IOException {
        return newStream(ByteOrder.BIG_ENDIAN).readBits(1);
    }

    @Benchmark
    public long readBits8BigEndian() throws IOException {
        return newStream(ByteOrder.BIG_ENDIAN).readBits(8);
    }

    @Benchmark
    public long readBits16BigEndian() throws IOException {
        return newStream(ByteOrder.BIG_ENDIAN).readBits(16);
    }

    @Benchmark
    public long readBits32BigEndian() throws IOException {
        return newStream(ByteOrder.BIG_ENDIAN).readBits(32);
    }

    @Benchmark
    public long readBits63BigEndian() throws IOException {
        return newStream(ByteOrder.BIG_ENDIAN).readBits(63);
    }

    // ===== readBits, LITTLE_ENDIAN =====

    @Benchmark
    public long readBits1LittleEndian() throws IOException {
        return newStream(ByteOrder.LITTLE_ENDIAN).readBits(1);
    }

    @Benchmark
    public long readBits8LittleEndian() throws IOException {
        return newStream(ByteOrder.LITTLE_ENDIAN).readBits(8);
    }

    @Benchmark
    public long readBits16LittleEndian() throws IOException {
        return newStream(ByteOrder.LITTLE_ENDIAN).readBits(16);
    }

    @Benchmark
    public long readBits32LittleEndian() throws IOException {
        return newStream(ByteOrder.LITTLE_ENDIAN).readBits(32);
    }

    @Benchmark
    public long readBits63LittleEndian() throws IOException {
        return newStream(ByteOrder.LITTLE_ENDIAN).readBits(63);
    }

    // ===== readBit =====

    @Benchmark
    public int readBitBigEndian() throws IOException {
        return newStream(ByteOrder.BIG_ENDIAN).readBit();
    }

    @Benchmark
    public int readBitLittleEndian() throws IOException {
        return newStream(ByteOrder.LITTLE_ENDIAN).readBit();
    }

    // ===== alignWithByteBoundary =====

    @Benchmark
    public long alignWithByteBoundaryBigEndian() throws IOException {
        BitInputStream in = newStream(ByteOrder.BIG_ENDIAN);
        in.readBits(5);
        in.alignWithByteBoundary();
        return in.readBits(8);
    }

    @Benchmark
    public long alignWithByteBoundaryLittleEndian() throws IOException {
        BitInputStream in = newStream(ByteOrder.LITTLE_ENDIAN);
        in.readBits(5);
        in.alignWithByteBoundary();
        return in.readBits(8);
    }

    // ===== bitsAvailable =====

    @Benchmark
    public long bitsAvailableBigEndian() throws IOException {
        BitInputStream in = newStream(ByteOrder.BIG_ENDIAN);
        in.readBits(8);
        return in.bitsAvailable();
    }

    // ===== bitsCached =====

    @Benchmark
    public int bitsCachedBigEndian() throws IOException {
        BitInputStream in = newStream(ByteOrder.BIG_ENDIAN);
        in.readBits(8);
        return in.bitsCached();
    }

    // ===== getBytesRead =====

    @Benchmark
    public long getBytesReadBigEndian() throws IOException {
        BitInputStream in = newStream(ByteOrder.BIG_ENDIAN);
        in.readBits(8);
        return in.getBytesRead();
    }

    // ===== clearBitCache =====

    @Benchmark
    public long clearBitCacheBigEndian() throws IOException {
        BitInputStream in = newStream(ByteOrder.BIG_ENDIAN);
        in.readBits(8);
        in.clearBitCache();
        return in.readBits(8);
    }
}
