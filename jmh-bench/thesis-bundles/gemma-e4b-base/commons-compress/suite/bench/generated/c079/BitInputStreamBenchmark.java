package bench.generated.c079;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.utils.BitInputStream;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class BitInputStreamBenchmark {

    private byte[] inputData;
    private static final int PAYLOAD_SIZE = 1024;

    @Setup(Level.Trial)
    public void setup() {
        // Create a fixed, representative payload
        inputData = new byte[PAYLOAD_SIZE];
        for (int i = 0; i < PAYLOAD_SIZE; i++) {
            inputData[i] = (byte) (i % 256);
        }
    }

    /**
     * Benchmarks reading a single bit using readBit() in Big Endian mode.
     */
    @Benchmark
    public void readSingleBitBigEndian(Blackhole bh) throws IOException {
        // Recreate the stream for a clean state per invocation
        ByteArrayInputStream bais = new ByteArrayInputStream(inputData);
        BitInputStream bis = new BitInputStream(bais, ByteOrder.BIG_ENDIAN);
        
        int bit = bis.readBit();
        bh.consume(bit);
    }

    /**
     * Benchmarks reading a single bit using readBits(1) in Little Endian mode.
     */
    @Benchmark
    public long readSingleBitLittleEndian(Blackhole bh) throws IOException {
        // Recreate the stream for a clean state per invocation
        ByteArrayInputStream bais = new ByteArrayInputStream(inputData);
        BitInputStream bis = new BitInputStream(bais, ByteOrder.LITTLE_ENDIAN);
        
        long bit = bis.readBits(1);
        bh.consume(bit);
        return bit;
    }

    /**
     * Benchmarks reading a small chunk of bits (10 bits) in Big Endian mode.
     */
    @Benchmark
    public long readSmallBitsBigEndian(Blackhole bh) throws IOException {
        // Recreate the stream for a clean state per invocation
        ByteArrayInputStream bais = new ByteArrayInputStream(inputData);
        BitInputStream bis = new BitInputStream(bais, ByteOrder.BIG_ENDIAN);
        
        long bits = bis.readBits(10);
        bh.consume(bits);
        return bits;
    }

    /**
     * Benchmarks reading a small chunk of bits (10 bits) in Little Endian mode.
     */
    @Benchmark
    public long readSmallBitsLittleEndian(Blackhole bh) throws IOException {
        // Recreate the stream for a clean state per invocation
        ByteArrayInputStream bais = new ByteArrayInputStream(inputData);
        BitInputStream bis = new BitInputStream(bais, ByteOrder.LITTLE_ENDIAN);
        
        long bits = bis.readBits(10);
        bh.consume(bits);
        return bits;
    }

    /**
     * Benchmarks reading the maximum allowed chunk of bits (63 bits) in Big Endian mode.
     */
    @Benchmark
    public long readMaxBitsBigEndian(Blackhole bh) throws IOException {
        // Recreate the stream for a clean state per invocation
        ByteArrayInputStream bais = new ByteArrayInputStream(inputData);
        BitInputStream bis = new BitInputStream(bais, ByteOrder.BIG_ENDIAN);
        
        long bits = bis.readBits(63);
        bh.consume(bits);
        return bits;
    }

    /**
     * Benchmarks reading the maximum allowed chunk of bits (63 bits) in Little Endian mode.
     */
    @Benchmark
    public long readMaxBitsLittleEndian(Blackhole bh) throws IOException {
        // Recreate the stream for a clean state per invocation
        ByteArrayInputStream bais = new ByteArrayInputStream(inputData);
        BitInputStream bis = new BitInputStream(bais, ByteOrder.LITTLE_ENDIAN);
        
        long bits = bis.readBits(63);
        bh.consume(bits);
        return bits;
    }

    /**
     * Benchmarks aligning the stream to the next byte boundary in Big Endian mode.
     */
    @Benchmark
    public void alignWithByteBoundaryBigEndian(Blackhole bh) throws IOException {
        // Recreate the stream for a clean state per invocation
        ByteArrayInputStream bais = new ByteArrayInputStream(inputData);
        BitInputStream bis = new BitInputStream(bais, ByteOrder.BIG_ENDIAN);
        
        bis.alignWithByteBoundary();
        // No return value to consume, operation is side-effect based
    }

    /**
     * Benchmarks aligning the stream to the next byte boundary in Little Endian mode.
     */
    @Benchmark
    public void alignWithByteBoundaryLittleEndian(Blackhole bh) throws IOException {
        // Recreate the stream for a clean state per invocation
        ByteArrayInputStream bais = new ByteArrayInputStream(inputData);
        BitInputStream bis = new BitInputStream(bais, ByteOrder.LITTLE_ENDIAN);
        
        bis.alignWithByteBoundary();
        // No return value to consume, operation is side-effect based
    }

    /**
     * Benchmarks clearing the internal bit cache in Big Endian mode.
     */
    @Benchmark
    public void clearBitCacheBigEndian(Blackhole bh) {
        // Recreate the stream for a clean state per invocation
        ByteArrayInputStream bais = new ByteArrayInputStream(inputData);
        BitInputStream bis = new BitInputStream(bais, ByteOrder.BIG_ENDIAN);
        
        bis.clearBitCache();
        // No return value to consume, operation is side-effect based
    }

    /**
     * Benchmarks clearing the internal bit cache in Little Endian mode.
     */
    @Benchmark
    public void clearBitCacheLittleEndian(Blackhole bh) {
        // Recreate the stream for a clean state per invocation
        ByteArrayInputStream bais = new ByteArrayInputStream(inputData);
        BitInputStream bis = new BitInputStream(bais, ByteOrder.LITTLE_ENDIAN);
        
        bis.clearBitCache();
        // No return value to consume, operation is side-effect based
    }

    /**
     * Benchmarks estimating available bits in Big Endian mode.
     */
    @Benchmark
    public long bitsAvailableBigEndian(Blackhole bh) throws IOException {
        // Recreate the stream for a clean state per invocation
        ByteArrayInputStream bais = new ByteArrayInputStream(inputData);
        BitInputStream bis = new BitInputStream(bais, ByteOrder.BIG_ENDIAN);
        
        long available = bis.bitsAvailable();
        bh.consume(available);
        return available;
    }

    /**
     * Benchmarks estimating available bits in Little Endian mode.
     */
    @Benchmark
    public long bitsAvailableLittleEndian(Blackhole bh) throws IOException {
        // Recreate the stream for a clean state per invocation
        ByteArrayInputStream bais = new ByteArrayInputStream(inputData);
        BitInputStream bis = new BitInputStream(bais, ByteOrder.LITTLE_ENDIAN);
        
        long available = bis.bitsAvailable();
        bh.consume(available);
        return available;
    }

    /**
     * Benchmarks retrieving the number of cached bits in Big Endian mode.
     */
    @Benchmark
    public int bitsCachedBigEndian(Blackhole bh) {
        // Recreate the stream for a clean state per invocation
        ByteArrayInputStream bais = new ByteArrayInputStream(inputData);
        BitInputStream bis = new BitInputStream(bais, ByteOrder.BIG_ENDIAN);
        
        int cached = bis.bitsCached();
        bh.consume(cached);
        return cached;
    }

    /**
     * Benchmarks retrieving the number of cached bits in Little Endian mode.
     */
    @Benchmark
    public int bitsCachedLittleEndian(Blackhole bh) {
        // Recreate the stream for a clean state per invocation
        ByteArrayInputStream bais = new ByteArrayInputStream(inputData);
        BitInputStream bis = new BitInputStream(bais, ByteOrder.LITTLE_ENDIAN);
        
        int cached = bis.bitsCached();
        bh.consume(cached);
        return cached;
    }
}
