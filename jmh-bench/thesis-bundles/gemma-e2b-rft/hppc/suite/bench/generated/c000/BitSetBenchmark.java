package bench.generated.c000;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import com.carrotsearch.hppc.BitSet;
import com.carrotsearch.hppc.BitSetIterator;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class BitSetBenchmark {

    // State fields for the BitSet instance
    private BitSet bitSet;
    private final long totalBits = 1024 * 1024; // 1 Million bits
    private final long maxIndex = totalBits - 1;

    @Setup
    public void setup() {
        // Initialize a BitSet large enough to test operations
        this.bitSet = new BitSet(totalBits);
    }

    // --- Single Bit Operations ---

    @Benchmark
    public void testSetSingleBit(Blackhole bh) {
        long index = 100000L;
        bitSet.set(index);
        bh.consume(true);
    }

    @Benchmark
    public void testGetSingleBit(Blackhole bh) {
        long index = 100000L;
        boolean result = bitSet.get(index);
        bh.consume(result);
    }

    @Benchmark
    public void testFlipSingleBit(Blackhole bh) {
        long index = 100000L;
        bitSet.flip(index);
        bh.consume(true);
    }

    @Benchmark
    public void testGetAndSetSingleBit(Blackhole bh) {
        long index = 100000L;
        boolean previousValue = bitSet.getAndSet(index);
        bh.consume(previousValue);
    }

    // --- Range Operations ---

    @Benchmark
    public void testSetRange(Blackhole bh) {
        long start = 50000L;
        long end = 50000L + 1000;
        bitSet.set(start, end);
        bh.consume(true);
    }

    @Benchmark
    public void testClearRange(Blackhole bh) {
        long start = 10000L;
        long end = 20000L;
        bitSet.clear(start, end);
        bh.consume(true);
    }

    @Benchmark
    public void testClearAll(Blackhole bh) {
        bitSet.clear();
        bh.consume(true);
    }

    // --- Iteration and Search ---

    @Benchmark
    public void testNextSetBit(Blackhole bh) {
        // Test finding a bit near the middle
        long targetIndex = totalBits / 2;
        int result = bitSet.nextSetBit((int) targetIndex);
        bh.consume(result);
    }

    @Benchmark
    public void testNextSetBitNotFound(Blackhole bh) {
        // Test searching past the end
        int result = bitSet.nextSetBit((int) maxIndex + 1000000);
        bh.consume(result);
    }

    @Benchmark
    public void testIteratorCardinality(Blackhole bh) {
        // Test the iterator's ability to traverse the set
        long cardinality = bitSet.cardinality();
        bh.consume(cardinality);
    }

    // --- Set Operations (Two BitSets) ---

    @Setup
    public void setupTwoSets() {
        // Setup a second, smaller set for comparison operations
        BitSet otherSet = new BitSet(totalBits / 4);
        // Populate otherSet with some random bits
        for (long i = 0; i < totalBits / 4; i += 100) {
            otherSet.set(i);
        }
    }

    @Benchmark
    public void testIntersectionCount(Blackhole bh) {
        long count = BitSet.intersectionCount(bitSet, bitSet);
        bh.consume(count);
    }

    @Benchmark
    public void testUnionCount(Blackhole bh) {
        BitSet otherSet = new BitSet(totalBits / 4);
        for (long i = 0; i < totalBits / 4; i += 100) {
            otherSet.set(i);
        }
        long count = BitSet.unionCount(bitSet, otherSet);
        bh.consume(count);
    }

    @Benchmark
    public void testAndNotCount(Blackhole bh) {
        BitSet otherSet = new BitSet(totalBits / 4);
        for (long i = 0; i < totalBits / 4; i += 100) {
            otherSet.set(i);
        }
        long count = BitSet.andNotCount(bitSet, otherSet);
        bh.consume(count);
    }

    @Benchmark
    public void testXorCount(Blackhole bh) {
        BitSet otherSet = new BitSet(totalBits / 4);
        for (long i = 0; i < totalBits / 4; i += 100) {
            otherSet.set(i);
        }
        long count = BitSet.xorCount(bitSet, otherSet);
        bh.consume(count);
    }

    // --- Mutating Operations (Two BitSets) ---

    @Benchmark
    public void testIntersect(Blackhole bh) {
        BitSet otherSet = new BitSet(totalBits / 4);
        for (long i = 0; i < totalBits / 4; i += 100) {
            otherSet.set(i);
        }
        bitSet.intersect(otherSet);
        bh.consume(true);
    }

    @Benchmark
    public void testUnion(Blackhole bh) {
        BitSet otherSet = new BitSet(totalBits / 4);
        for (long i = 0; i < totalBits / 4; i += 100) {
            otherSet.set(i);
        }
        bitSet.union(otherSet);
        bh.consume(true);
    }

    @Benchmark
    public void testRemove(Blackhole bh) {
        BitSet otherSet = new BitSet(totalBits / 4);
        for (long i = 0; i < totalBits / 4; i += 100) {
            otherSet.set(i);
        }
        bitSet.remove(otherSet);
        bh.consume(true);
    }

    @Benchmark
    public void testXor(Blackhole bh) {
        BitSet otherSet = new BitSet(totalBits / 4);
        for (long i = 0; i < totalBits / 4; i += 100) {
            otherSet.set(i);
        }
        bitSet.xor(otherSet);
        bh.consume(true);
    }

    // --- Utility Operations ---

    @Benchmark
    public void testClone(Blackhole bh) {
        BitSet clonedSet = (BitSet) bitSet.clone();
        bh.consume(clonedSet);
    }
}
