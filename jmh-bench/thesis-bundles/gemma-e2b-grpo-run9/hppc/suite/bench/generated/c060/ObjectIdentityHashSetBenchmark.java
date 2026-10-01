package bench.generated.c060;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import com.carrotsearch.hppc.ObjectIdentityHashSet;
import java.util.HashSet;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ObjectIdentityHashSetBenchmark {

    // Since we are using Mode.AverageTime and want independent tests,
    // we avoid @State fields that hold mutable state, relying on local instantiation
    // or static factory calls within the benchmark methods.

    @Benchmark
    public void testConstructionAndSize(Blackhole bh) {
        // Test default constructor
        ObjectIdentityHashSet<Object> set1 = new ObjectIdentityHashSet<>();
        bh.consume(set1.size());

        // Test static factory method
        ObjectIdentityHashSet<Object> set2 = ObjectIdentityHashSet.from(new Object[0]);
        bh.consume(set2.size());
    }

    @Benchmark
    public void testContainsOnEmptySet(Blackhole bh) {
        ObjectIdentityHashSet<Object> set = new ObjectIdentityHashSet<>();
        // Test contains on an empty set (should be fast)
        boolean result = set.contains(null);
        bh.consume(result);
    }

    @Benchmark
    public void testAddAndSize(Blackhole bh) {
        // Test adding an element. Since we are in AverageTime mode,
        // we measure the cost of one operation on a fresh instance.
        ObjectIdentityHashSet<Object> set = new ObjectIdentityHashSet<>();
        
        // We call add, but since we don't consume the return value, 
        // we rely on the side effect and check size afterwards.
        set.add(null);
        bh.consume(set.size());
    }

    @Benchmark
    public void testContainsOnPopulatedSet(Blackhole bh) {
        // Setup a set with a known element (null)
        ObjectIdentityHashSet<Object> set = ObjectIdentityHashSet.from(null);
        
        // Test contains (should be fast)
        boolean result = set.contains(null);
        bh.consume(result);
    }

    @Benchmark
    public void testClear(Blackhole bh) {
        // Test clear operation
        ObjectIdentityHashSet<Object> set = new ObjectIdentityHashSet<>();
        set.add(null);
        set.clear();
        bh.consume(set.size());
    }
}
