package bench.generated.c031;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import com.carrotsearch.hppc.IntHashSet;
import com.carrotsearch.hppc.IntContainer;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class IntHashSetBenchmark {

    // Since IntHashSet mutates its internal state (keys array),
    // we create a new instance inside each benchmark method to ensure
    // that the measured operation is isolated and not polluted by previous trials.

    @Benchmark
    public void testAdd(Blackhole bh) {
        IntHashSet set = new IntHashSet();
        set.add(10);
        set.add(20);
        bh.consume(set);
    }

    @Benchmark
    public void testContains(Blackhole bh) {
        IntHashSet set = new IntHashSet();
        set.add(100);
        set.add(200);
        boolean result = set.contains(100);
        bh.consume(result);
    }

    @Benchmark
    public void testRemove(Blackhole bh) {
        IntHashSet set = new IntHashSet();
        set.add(10);
        set.add(20);
        set.remove(10);
        bh.consume(set);
    }

    @Benchmark
    public void testRemoveNonExistent(Blackhole bh) {
        IntHashSet set = new IntHashSet();
        set.add(10);
        // Attempt to remove a key that doesn't exist
        set.remove(999);
        bh.consume(set);
    }

    @Benchmark
    public void testAddAll(Blackhole bh) {
        IntHashSet set = new IntHashSet();
        // Add a small number of elements to ensure the operation runs
        int addedCount = set.addAll(1, 2, 3, 4, 5);
        bh.consume(addedCount);
    }

    @Benchmark
    public void testSize(Blackhole bh) {
        IntHashSet set = new IntHashSet();
        // Ensure the set is populated enough to trigger some internal state
        set.addAll(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        int size = set.size();
        bh.consume(size);
    }

    @Benchmark
    public void testIndexGet(Blackhole bh) {
        IntHashSet set = new IntHashSet();
        set.add(10);
        set.add(20);
        // We rely on the internal structure for this test, assuming the index is valid
        int result = set.indexGet(0);
        bh.consume(result);
    }
}
