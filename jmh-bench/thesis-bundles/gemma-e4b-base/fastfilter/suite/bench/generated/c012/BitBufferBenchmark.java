package bench.generated.c012;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.fastfilter.gcs.BitBuffer;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class BitBufferBenchmark {

    private BitBuffer buffer;
    private long testValue1;
    private long testValue2;
    private int testShift;

    @Setup(Level.Trial)
    public void setupTrial() {
        // Initialize fixed inputs once per trial
        testValue1 = 0xDEADBEEFCAFEF00DL;
        testValue2 = 0x123456789ABCDEF0L;
        testShift = 10;
    }

    @Setup(Level.Invocation)
    public void setupInvocation() {
        // Initialize a fresh buffer for each invocation to ensure state isolation
        // Use a reasonably sized buffer (e.g., 1KB = 1024 bits)
        buffer = new BitBuffer(1024);
    }

    // --- Basic Bit Operations ---

    @Benchmark
    public void writeBit_singleBit(Blackhole bh) {
        buffer.writeBit(1);
        bh.consume(buffer.position());
    }

    @Benchmark
    public void readBit_singleBit(Blackhole bh) {
        // Must ensure buffer has data before reading
        buffer.writeBit(1);
        long result = buffer.readBit();
        bh.consume(result);
    }

    // --- Number Operations ---

    @Benchmark
    public void writeNumber_small(Blackhole bh) {
        // Write a small number (e.g., 32 bits)
        buffer.writeNumber(testValue1 & 0xFFFFFFFFL, 32);
        bh.consume(buffer.position());
    }

    @Benchmark
    public void readNumber_small(Blackhole bh) {
        // Pre-write data
        buffer.writeNumber(testValue1 & 0xFFFFFFFFL, 32);
        long result = buffer.readNumber(32);
        bh.consume(result);
    }

    @Benchmark
    public void writeNumber_large(Blackhole bh) {
        // Write a large number (e.g., 63 bits)
        buffer.writeNumber(testValue2, 63);
        bh.consume(buffer.position());
    }

    @Benchmark
    public void readNumber_large(Blackhole bh) {
        // Pre-write data
        buffer.writeNumber(testValue2, 63);
        long result = buffer.readNumber(63);
        bh.consume(result);
    }

    @Benchmark
    public void readNumber_atPosition(Blackhole bh) {
        // Pre-write data
        buffer.writeNumber(testValue1, 32);
        buffer.writeNumber(testValue2, 32);
        
        // Read from a specific position (e.g., position 32)
        long result = buffer.readNumber(32L, 32);
        bh.consume(result);
    }

    // --- Encoding/Decoding: Golomb Rice ---

    @Benchmark
    public void writeGolombRiceFast_small(Blackhole bh) {
        // Test small value encoding
        buffer.writeGolombRiceFast(testShift, 100L);
        bh.consume(buffer.position());
    }

    @Benchmark
    public void skipGolombRice_small(Blackhole bh) {
        // Pre-write data
        buffer.writeGolombRiceFast(testShift, 100L);
        
        // Skip the encoded value (updates internal position)
        buffer.skipGolombRice(testShift);
        
        // Consume the new position
        bh.consume(buffer.position());
    }

    @Benchmark
    public void skipGolombRice_large(Blackhole bh) {
        // Test large value encoding (forcing the bit sequence path)
        // We need a value > 2^63
        long largeValue = 0xFFFFFFFFFFFFFFFFL;
        buffer.writeGolombRiceFast(testShift, largeValue);
        
        // Skip the encoded value (updates internal position)
        buffer.skipGolombRice(testShift);
        
        // Consume the new position
        bh.consume(buffer.position());
    }

    // --- Encoding/Decoding: Elias Delta ---

    @Benchmark
    public void writeEliasDelta(Blackhole bh) {
        // Test writing a positive value
        buffer.writeEliasDelta(testValue1);
        bh.consume(buffer.position());
    }

    @Benchmark
    public void readEliasDelta(Blackhole bh) {
        // Pre-write data
        buffer.writeEliasDelta(testValue1);
        long result = buffer.readEliasDelta();
        bh.consume(result);
    }

    // --- Complex Operations ---

    @Benchmark
    public void write_copyBuffer(Blackhole bh) {
        // Create a source buffer
        BitBuffer source = new BitBuffer(512);
        source.writeNumber(testValue1, 32);
        source.writeNumber(testValue2, 32);
        
        // Copy source to target buffer
        buffer.write(source);
        bh.consume(buffer.position());
    }

    @Benchmark
    public void readUntilZero_simple(Blackhole bh) {
        // Pre-write data ending in zeros
        buffer.writeNumber(testValue1, 32);
        buffer.writeBit(0);
        buffer.writeBit(0);
        buffer.writeBit(0);
        
        // Read until zero starting from the current position
        int count = buffer.readUntilZero(buffer.position());
        bh.consume(count);
    }

    @Benchmark
    public void readUntilZero_complex(Blackhole bh) {
        // Pre-write data that spans multiple long[] elements
        // Write 64 bits of data
        buffer.writeNumber(testValue1, 64);
        
        // Force a zero sequence that spans the boundary
        // Reset buffer and write a full long of ones (0xFFFFFFFFFFFFFFFFL)
        buffer.clear();
        buffer.writeNumber(0xFFFFFFFFFFFFFFFFL, 64);
        
        // Now, read until zero starting at the next position (which is 0)
        int count = buffer.readUntilZero(buffer.position());
        bh.consume(count);
    }
    
    @Benchmark
    public void clearBuffer(Blackhole bh) {
        // Ensure buffer is non-empty before clearing
        buffer.writeBit(1);
        buffer.writeBit(0);
        buffer.clear();
        bh.consume(buffer.position());
    }

    // --- Static Methods ---

    @Benchmark
    public void foldSigned_positive(Blackhole bh) {
        long result = BitBuffer.foldSigned(testValue1);
        bh.consume(result);
    }

    @Benchmark
    public void foldSigned_negative(Blackhole bh) {
        long result = BitBuffer.foldSigned(-testValue2);
        bh.consume(result);
    }

    @Benchmark
    public void unfoldSigned_positive(Blackhole bh) {
        // Positive unsigned number (even)
        long unsigned = testValue1 * 2;
        long result = BitBuffer.unfoldSigned(unsigned);
        bh.consume(result);
    }

    @Benchmark
    public void unfoldSigned_negative(Blackhole bh) {
        // Negative unsigned number (odd)
        long unsigned = -testValue2 * 2 + 1;
        long result = BitBuffer.unfoldSigned(unsigned);
        bh.consume(result);
    }
}
