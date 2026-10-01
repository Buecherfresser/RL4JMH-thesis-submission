package bench.generated.c043;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

import com.carrotsearch.hppc.LongHashSet;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class LongHashSetBenchmark {

    // Since LongHashSet is mutable, we instantiate it locally within the benchmark
    // method to ensure a clean state for each invocation, avoiding state pollution
    // across benchmark runs.

    @Benchmark
    public void testAdd(Blackhole bh) {
        // Test adding a single element to a fresh set
        LongHashSet set = new LongHashSet();
        set.add(123456789L);
        bh.consume(set);
    }

    @Benchmark
    public void testContains(Blackhole bh) {
        // Test contains on an empty set
        LongHashSet set = new LongHashSet();
        bh.consume(set.contains(123456789L));
    }

    @Benchmark
    public void testAddExisting(Blackhole bh) {
        // Test adding an existing element (should return false, but test the path)
        LongHashSet set = new LongHashSet();
        set.add(123456789L);
        bh.consume(set.add(123456789L));
    }

    @Benchmark
    public void testSize(Blackhole bh) {
        // Test size on an empty set
        LongHashSet set = new LongHashSet();
        bh.consume(set.size());
    }

    @Benchmark
    public void testClear(Blackhole bh) {
        // Test clear on an empty set
        LongHashSet set = new LongHashSet();
        set.clear();
        bh.consume(set);
    }

    @Benchmark
    public void testToArray(Blackhole bh) {
        // Test toArray on an empty set
        LongHashSet set = new LongHashSet();
        bh.consume(set.toArray());
    }

    @Benchmark
    public void testAddAll(Blackhole bh) {
        // Test addAll on an empty set (using a small array)
        LongHashSet set = new LongHashSet();
        bh.consume(set.addAll(1L, 2L, 3L));
    }

    @Benchmark
    public void testContainsOnPopulatedSet(Blackhole bh) {
        // Test contains on a set with one element
        LongHashSet set = new LongHashSet();
        set.add(999L);
        bh.consume(set.contains(999L));
    }
}
