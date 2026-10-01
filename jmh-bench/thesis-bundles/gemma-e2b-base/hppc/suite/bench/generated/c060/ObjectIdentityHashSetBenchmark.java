package bench.generated.c060;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.carrotsearch.hppc.ObjectIdentityHashSet;
import com.carrotsearch.hppc.ObjectContainer;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ObjectIdentityHashSetBenchmark {

    private ObjectIdentityHashSet<Integer> set;
    private List<Integer> elementsToAdd;
    private int existingElement;

    @Setup
    public void setup() {
        // 1. Setup a base set with some elements
        set = new ObjectIdentityHashSet<>();
        set.add(10);
        set.add(20);
        set.add(30);

        // 2. Setup elements for insertion/removal tests
        elementsToAdd = new ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            elementsToAdd.add(i * 2);
        }
        
        // 3. Setup an element guaranteed to exist
        existingElement = 20;
    }

    @Benchmark
    public Integer testAdd() {
        // Test adding a new element
        int element = elementsToAdd.get(0);
        set.add(element);
        return element;
    }

    @Benchmark
    public Integer testContainsExisting() {
        // Test checking for an existing element
        return set.contains(existingElement) ? existingElement : -1;
    }

    @Benchmark
    public Integer testContainsMissing() {
        // Test checking for a missing element
        return set.contains(999) ? 999 : -1;
    }

    @Benchmark
    public Integer testRemove() {
        // Test removing an existing element
        set.remove(existingElement);
        return existingElement;
    }

    @Benchmark
    public Integer testSize() {
        // Test getting the size
        return set.size();
    }

    @Benchmark
    public Integer testAddAll() {
        // Test adding a batch of elements
        for (int element : elementsToAdd) {
            set.add(element);
        }
        return elementsToAdd.size();
    }

    @Benchmark
    public ObjectIdentityHashSet<Integer> testFromStaticFactory() {
        // Test creation via static factory method
        ObjectIdentityHashSet<Integer> newSet = ObjectIdentityHashSet.from(1, 2, 3, 4, 5);
        return newSet;
    }
}
