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

    private BitSet bitSet;
    private BitSet otherBitSet;
    private static final long INITIAL_CAPACITY_BITS = 1024L * 1024L; // 1 Million bits

    @Setup
    public void setup() {
        // Initialize the main BitSet with a large number of bits
        this.bitSet = new BitSet(INITIAL_CAPACITY_BITS);

        // Initialize a second BitSet for comparison operations
        this.otherBitSet = new BitSet(INITIAL_CAPACITY_BITS);

        // Populate the main BitSet with some random bits for meaningful tests
        for (long i = 0; i < INITIAL_CAPACITY_BITS / 10; i++) {
            this.bitSet.set(i);
        }
        // Populate the other BitSet with a different set of bits
        for (long i = INITIAL_CAPACITY_BITS / 2; i < INITIAL_CAPACITY_BITS / 2 + INITIAL_CAPACITY_BITS / 10; i++) {
            this.otherBitSet.set(i);
        }
    }

    // --- Basic Get/Set Operations ---

    @Benchmark
    public void testGet(Blackhole bh) {
        int index = (int) (Math.random() * bitSet.capacity());
        bh.consume(bitSet.get(index));
    }

    @Benchmark
    public void testGetLong(Blackhole bh) {
        long index = (long) (Math.random() * bitSet.capacity());
        bh.consume(bitSet.get(index));
    }

    @Benchmark
    public void testSet(Blackhole bh) {
        long index = (long) (Math.random() * bitSet.capacity());
        bitSet.set(index);
        bh.consume(index);
    }

    @Benchmark
    public void testGetAndSetInt(Blackhole bh) {
        int index = (int) (Math.random() * bitSet.capacity());
        boolean previous = bitSet.getAndSet(index);
        bh.consume(previous);
    }

    @Benchmark
    public void testGetAndSetLong(Blackhole bh) {
        long index = (long) (Math.random() * bitSet.capacity());
        boolean previous = bitSet.getAndSet(index);
        bh.consume(previous);
    }

    // --- Range Operations ---

    @Benchmark
    public void testSetRange(Blackhole bh) {
        long start = (long) (Math.random() * bitSet.capacity());
        long end = start + 100;
        bitSet.set(start, end);
        bh.consume(start);
    }

    @Benchmark
    public void testClearRange(Blackhole bh) {
        long start = (long) (Math.random() * bitSet.capacity());
        long end = start + 100;
        bitSet.clear(start, end);
        bh.consume(start);
    }

    @Benchmark
    public void testFlipRange(Blackhole bh) {
        long start = (long) (Math.random() * bitSet.capacity());
        long end = start + 100;
        bitSet.flip(start, end);
        bh.consume(start);
    }

    // --- Cardinality and Query Operations ---

    @Benchmark
    public void testCardinality(Blackhole bh) {
        bh.consume(bitSet.cardinality());
    }

    @Benchmark
    public void testIsEmpty(Blackhole bh) {
        bh.consume(bitSet.isEmpty());
    }

    @Benchmark
    public void testNextSetBitLong(Blackhole bh) {
        long index = (long) (Math.random() * bitSet.capacity());
        bh.consume(bitSet.nextSetBit(index));
    }

    @Benchmark
    public void testNextSetBitInt(Blackhole bh) {
        int index = (int) (Math.random() * bitSet.capacity());
        bh.consume(bitSet.nextSetBit(index));
    }

    // --- Set Operations (Mutating) ---

    @Benchmark
    public void testUnion(Blackhole bh) {
        bitSet.union(otherBitSet);
        bh.consume(bitSet.cardinality());
    }

    @Benchmark
    public void testXor(Blackhole bh) {
        bitSet.xor(otherBitSet);
        bh.consume(bitSet.cardinality());
    }

    @Benchmark
    public void testIntersect(Blackhole bh) {
        bitSet.intersect(otherBitSet);
        bh.consume(bitSet.cardinality());
    }

    @Benchmark
    public void testRemove(Blackhole bh) {
        bitSet.remove(otherBitSet);
        bh.consume(bitSet.cardinality());
    }

    // --- View Operations ---

    @Benchmark
    public void testAsIntLookupContainerSize(Blackhole bh) {
        int size = bitSet.asIntLookupContainer().size();
        bh.consume(size);
    }

    @Benchmark
    public void testAsLongLookupContainerIterator(Blackhole bh) {
        // This tests the iterator setup and traversal logic
        bitSet.asLongLookupContainer().iterator().hasNext();
        bh.consume(true);
    }

    // --- Static Operations ---

    @Benchmark
    public void testStaticIntersectionCount(Blackhole bh) {
        long count = BitSet.intersectionCount(bitSet, otherBitSet);
        bh.consume(count);
    }

    @Benchmark
    public void testStaticUnionCount(Blackhole bh) {
        long count = BitSet.unionCount(bitSet, otherBitSet);
        bh.consume(count);
    }

    @Benchmark
    public void testStaticXorCount(Blackhole bh) {
        long count = BitSet.xorCount(bitSet, otherBitSet);
        bh.consume(count);
    }
}
