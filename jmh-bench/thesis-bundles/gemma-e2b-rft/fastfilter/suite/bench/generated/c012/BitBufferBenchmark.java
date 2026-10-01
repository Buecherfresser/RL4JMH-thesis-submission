package bench.generated.c012;

import org.fastfilter.gcs.BitBuffer;
import java.util.Random;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class BitBufferBenchmark {

    private BitBuffer buffer;
    private long[] testData;
    private int initialBitSize;

    // Constants for setup
    private static final int INITIAL_BIT_SIZE = 1024 * 1024; // 1MB of bits

    @Setup
    public void setup() {
        // 1. Initialize the BitBuffer
        this.buffer = new BitBuffer(INITIAL_BIT_SIZE);

        // 2. Prepare test data (a mix of 0s and 1s)
        // The BitBuffer constructor takes bits, which is the total number of bits.
        // The internal data array size is calculated as (bits + 63) / 64.
        this.testData = new long[(int)((INITIAL_BIT_SIZE + 63) / 64)];
        Random random = new Random(42); // Fixed seed for reproducibility

        // We don't strictly need to populate testData if we only use the buffer methods,
        // but keeping the setup structure similar to the original attempt.
        // Since the benchmark focuses on buffer operations, we ensure the buffer is initialized.
        
        this.initialBitSize = INITIAL_BIT_SIZE;
    }

    @Benchmark
    public void writeBit(Blackhole bh) {
        // Write a single bit (1)
        buffer.writeBit(1L);
        bh.consume(buffer.position());
    }

    @Benchmark
    public void readBit(Blackhole bh) {
        // Read a single bit
        long bit = buffer.readBit();
        bh.consume(bit);
    }

    @Benchmark
    public void writeNumber(Blackhole bh) {
        // Write a 32-bit number
        long value = 0xDEADBEEFL;
        buffer.writeNumber(value, 32);
        bh.consume(buffer.position());
    }

    @Benchmark
    public void readNumber(Blackhole bh) {
        // Read a 32-bit number starting from current position
        int bitCount = 32;
        long value = buffer.readNumber(bitCount);
        bh.consume(value);
    }

    @Benchmark
    public void writeEliasDelta(Blackhole bh) {
        // Write a large Elias Delta encoded value
        long value = 0x1234567890ABCDEFL;
        buffer.writeEliasDelta(value);
        bh.consume(buffer.position());
    }

    @Benchmark
    public void readEliasDelta(Blackhole bh) {
        // Read the Elias Delta encoded value
        long value = buffer.readEliasDelta();
        bh.consume(value);
    }

    @Benchmark
    public void skipGolombRice(Blackhole bh) {
        // Skip a fixed amount of Golomb Rice data
        int shift = 10;
        int skipAmount = 100;
        int newPos = buffer.skipGolombRice(buffer.position(), shift);
        bh.consume(newPos);
    }

    @Benchmark
    public void foldSigned(Blackhole bh) {
        // Test static utility function
        long input = -5L;
        long result = BitBuffer.foldSigned(input);
        bh.consume(result);
    }

    @Benchmark
    public void unfoldSigned(Blackhole bh) {
        // Test static utility function
        long input = 11L;
        long result = BitBuffer.unfoldSigned(input);
        bh.consume(result);
    }
}
