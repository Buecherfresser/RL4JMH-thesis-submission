package bench.generated.c000;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.Random;
import com.carrotsearch.hppc.BitSet;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class BitSetBenchmark {

    @State(Scope.Benchmark)
    public static class BenchmarkState {
        BitSet base;
        BitSet other;
        int testIntIndex;
        long testLongIndex;
        long rangeStart;
        long rangeEnd;

        @Setup(Level.Trial)
        public void setUp() {
            final int numBits = 1 << 14; // 16384 bits
            Random rnd = new Random(0x1234ABCDL);
            base = new BitSet(numBits);
            other = new BitSet(numBits);
            for (int i = 0; i < numBits; i++) {
                if (rnd.nextInt(100) < 10) { // ~10% density
                    base.set(i);
                }
                if (rnd.nextInt(100) < 10) {
                    other.set(i);
                }
            }
            testIntIndex = rnd.nextInt(numBits);
            testLongIndex = Math.abs(rnd.nextLong()) % numBits;
            rangeStart = Math.abs(rnd.nextLong()) % (numBits / 2);
            rangeEnd = rangeStart + (Math.abs(rnd.nextLong()) % (numBits / 2) + 1);
        }
    }

    @Benchmark
    public boolean getInt(BenchmarkState s) {
        return s.base.get(s.testIntIndex);
    }

    @Benchmark
    public boolean getLong(BenchmarkState s) {
        return s.base.get(s.testLongIndex);
    }

    @Benchmark
    public void setLong(BenchmarkState s, Blackhole bh) {
        BitSet b = (BitSet) s.base.clone();
        b.set(s.testLongIndex);
        bh.consume(b);
    }

    @Benchmark
    public void setRange(BenchmarkState s, Blackhole bh) {
        BitSet b = (BitSet) s.base.clone();
        b.set(s.rangeStart, s.rangeEnd);
        bh.consume(b);
    }

    @Benchmark
    public void clearLong(BenchmarkState s, Blackhole bh) {
        BitSet b = (BitSet) s.base.clone();
        b.clear(s.testLongIndex);
        bh.consume(b);
    }

    @Benchmark
    public void clearRange(BenchmarkState s, Blackhole bh) {
        BitSet b = (BitSet) s.base.clone();
        b.clear(s.rangeStart, s.rangeEnd);
        bh.consume(b);
    }

    @Benchmark
    public void flipLong(BenchmarkState s, Blackhole bh) {
        BitSet b = (BitSet) s.base.clone();
        b.flip(s.testLongIndex);
        bh.consume(b);
    }

    @Benchmark
    public boolean flipAndGetInt(BenchmarkState s) {
        BitSet b = (BitSet) s.base.clone();
        return b.flipAndGet(s.testIntIndex);
    }

    @Benchmark
    public boolean flipAndGetLong(BenchmarkState s) {
        BitSet b = (BitSet) s.base.clone();
        return b.flipAndGet(s.testLongIndex);
    }

    @Benchmark
    public boolean getAndSetInt(BenchmarkState s) {
        BitSet b = (BitSet) s.base.clone();
        return b.getAndSet(s.testIntIndex);
    }

    @Benchmark
    public boolean getAndSetLong(BenchmarkState s) {
        BitSet b = (BitSet) s.base.clone();
        return b.getAndSet(s.testLongIndex);
    }

    @Benchmark
    public long cardinality(BenchmarkState s) {
        return s.base.cardinality();
    }

    @Benchmark
    public long capacity(BenchmarkState s) {
        return s.base.capacity();
    }

    @Benchmark
    public long length(BenchmarkState s) {
        return s.base.length();
    }

    @Benchmark
    public boolean isEmpty(BenchmarkState s) {
        return s.base.isEmpty();
    }

    @Benchmark
    public int nextSetBitInt(BenchmarkState s) {
        return s.base.nextSetBit(s.testIntIndex);
    }

    @Benchmark
    public long nextSetBitLong(BenchmarkState s) {
        return s.base.nextSetBit(s.testLongIndex);
    }

    @Benchmark
    public int iteratorNext(BenchmarkState s) {
        return s.base.iterator().nextSetBit();
    }

    @Benchmark
    public void intersect(BenchmarkState s, Blackhole bh) {
        BitSet b = (BitSet) s.base.clone();
        b.intersect(s.other);
        bh.consume(b);
    }

    @Benchmark
    public void union(BenchmarkState s, Blackhole bh) {
        BitSet b = (BitSet) s.base.clone();
        b.union(s.other);
        bh.consume(b);
    }

    @Benchmark
    public void xor(BenchmarkState s, Blackhole bh) {
        BitSet b = (BitSet) s.base.clone();
        b.xor(s.other);
        bh.consume(b);
    }

    @Benchmark
    public void remove(BenchmarkState s, Blackhole bh) {
        BitSet b = (BitSet) s.base.clone();
        b.remove(s.other);
        bh.consume(b);
    }

    @Benchmark
    public long intersectionCount(BenchmarkState s) {
        return BitSet.intersectionCount(s.base, s.other);
    }

    @Benchmark
    public long unionCount(BenchmarkState s) {
        return BitSet.unionCount(s.base, s.other);
    }

    @Benchmark
    public long andNotCount(BenchmarkState s) {
        return BitSet.andNotCount(s.base, s.other);
    }

    @Benchmark
    public long xorCount(BenchmarkState s) {
        return BitSet.xorCount(s.base, s.other);
    }

    @Benchmark
    public int intLookupSize(BenchmarkState s) {
        return s.base.asIntLookupContainer().size();
    }

    @Benchmark
    public int longLookupSize(BenchmarkState s) {
        return s.base.asLongLookupContainer().size();
    }
}
