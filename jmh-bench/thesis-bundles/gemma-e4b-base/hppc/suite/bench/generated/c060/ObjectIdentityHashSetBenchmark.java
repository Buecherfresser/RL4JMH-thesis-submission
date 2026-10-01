package bench.generated.c060;

import com.carrotsearch.hppc.ObjectIdentityHashSet;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.ArrayList;
import java.util.List;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ObjectIdentityHashSetBenchmark {

    private ObjectIdentityHashSet<Object> set;
    private List<Object> presentKeys;
    private List<Object> absentKeys;
    private static final int SET_SIZE = 10000;

    @Setup(Level.Trial)
    public void setup() {
        // Initialize the set with a fixed size
        set = new ObjectIdentityHashSet<>(SET_SIZE);
        presentKeys = new ArrayList<>(SET_SIZE);
        absentKeys = new ArrayList<>(SET_SIZE / 10);

        // 1. Populate the set and collect present keys
        for (int i = 0; i < SET_SIZE; i++) {
            final int index = i; // Capture i into a final variable to resolve compilation error
            // Create a unique object instance for identity comparison
            Object key = new Object() {
                @Override
                public String toString() {
                    return "Key-" + index;
                }
            };
            set.add(key);
            presentKeys.add(key);
        }

        // 2. Generate keys that are guaranteed not to be in the set
        for (int i = 0; i < SET_SIZE / 10; i++) {
            final int index = i; // Capture i into a final variable to resolve compilation error
            // Create a new, unique object instance
            Object key = new Object() {
                @Override
                public String toString() {
                    return "AbsentKey-" + index;
                }
            };
            absentKeys.add(key);
        }
    }

    @Benchmark
    public void benchmarkContainsPresentKey(Blackhole bh) {
        // Test lookup for an element known to be in the set
        Object key = presentKeys.get(SET_SIZE / 2);
        boolean contains = set.contains(key);
        bh.consume(contains);
    }

    @Benchmark
    public void benchmarkContainsAbsentKey(Blackhole bh) {
        // Test lookup for an element known not to be in the set
        Object key = absentKeys.get(absentKeys.size() / 2);
        boolean contains = set.contains(key);
        bh.consume(contains);
    }

    @Benchmark
    public void benchmarkRemovePresentKey(Blackhole bh) {
        // Test removal of an element known to be in the set
        Object key = presentKeys.get(SET_SIZE / 4);
        boolean removed = set.remove(key);
        bh.consume(removed);
    }

    @Benchmark
    public void benchmarkSize(Blackhole bh) {
        // Test size retrieval
        int size = set.size();
        bh.consume(size);
    }

    @Benchmark
    public void benchmarkAdd(Blackhole bh) {
        // Test adding a new element. Since this mutates the set, we create a fresh set
        // for each invocation to ensure consistent timing for a single addition operation.
        ObjectIdentityHashSet<Object> freshSet = new ObjectIdentityHashSet<>(1);
        Object newKey = new Object();
        
        boolean added = freshSet.add(newKey);
        bh.consume(added);
    }
}
