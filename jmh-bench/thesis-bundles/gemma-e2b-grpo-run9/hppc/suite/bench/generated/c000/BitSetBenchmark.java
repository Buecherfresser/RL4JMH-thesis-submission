package bench.generated.c000;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import com.carrotsearch.hppc.BitSet;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class BitSetBenchmark {

    // We don't need a state field if we create a fresh BitSet in every benchmark,
    // which is safer for mutable objects.

    @Benchmark
    public void testGet(Blackhole bh) {
        // Create a fresh, small BitSet for each invocation.
        BitSet bs = BitSet.newInstance();
        // Test a valid index (0)
        bh.consume(bs.get(0));
    }

    @Benchmark
    public void testGetLong(Blackhole bh) {
        BitSet bs = BitSet.newInstance();
        // Test a valid long index
        bh.consume(bs.get(1000000000L));
    }

    @Benchmark
    public void testCardinality(Blackhole bh) {
        BitSet bs = BitSet.newInstance();
        // Cardinality of an empty set should be 0
        bh.consume(bs.cardinality());
    }

    @Benchmark
    public void testSet(Blackhole bh) {
        BitSet bs = BitSet.newInstance();
        // Test setting a bit (should expand capacity if necessary)
        bs.set(100);
        bh.consume(bs);
    }

    @Benchmark
    public void testClear(Blackhole bh) {
        BitSet bs = BitSet.newInstance();
        // Ensure it has some bits set first to test clearing
        bs.set(10);
        bs.clear();
        bh.consume(bs);
    }

    @Benchmark
    public void testIntersect(Blackhole bh) {
        BitSet a = BitSet.newInstance();
        BitSet b = BitSet.newInstance();

        // Set some bits in A
        a.set(10);
        a.set(11);

        // Set some bits in B (to ensure intersection logic runs)
        b.set(10);
        b.set(12);

        // Perform intersection (mutates A)
        a.intersect(b);
        bh.consume(a);
    }

    @Benchmark
    public void testUnion(Blackhole bh) {
        BitSet a = BitSet.newInstance();
        BitSet b = BitSet.newInstance();

        // Set some bits in A
        a.set(10);

        // Set some bits in B
        b.set(11);

        // Perform union (mutates A)
        a.union(b);
        bh.consume(a);
    }

    @Benchmark
    public void testXor(Blackhole bh) {
        BitSet a = BitSet.newInstance();
        BitSet b = BitSet.newInstance();

        // Set some bits in A
        a.set(10);

        // Set some bits in B
        b.set(11);

        // Perform XOR (mutates A)
        a.xor(b);
        bh.consume(a);
    }
}
