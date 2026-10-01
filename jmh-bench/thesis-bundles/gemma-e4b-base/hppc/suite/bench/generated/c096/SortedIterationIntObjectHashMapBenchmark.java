package bench.generated.c096;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import com.carrotsearch.hppc.SortedIterationIntObjectHashMap;
import com.carrotsearch.hppc.IntObjectHashMap;
import com.carrotsearch.hppc.comparators.IntComparator;
import com.carrotsearch.hppc.comparators.IntObjectComparator;
import com.carrotsearch.hppc.procedures.IntObjectProcedure;
import com.carrotsearch.hppc.predicates.IntObjectPredicate;
import java.util.Random;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class SortedIterationIntObjectHashMapBenchmark {

    private static final int MAP_SIZE = 1000;
    private static final String TEST_VALUE = "TestValue";

    // Delegate map used to build the sorted views
    private IntObjectHashMap<String> delegateMap;

    // Sorted view based on Key comparison
    private SortedIterationIntObjectHashMap<String> sortedByKeyView;

    // Sorted view based on Key and Value comparison
    private SortedIterationIntObjectHashMap<String> sortedByKeyAndValueView;

    // Test inputs
    private int[] keys;
    private int lookupKey;
    private int nonExistentKey;

    @Setup(Level.Trial)
    public void setup() {
        // 1. Setup Keys and Inputs
        Random random = new Random(42);
        keys = new int[MAP_SIZE];
        for (int i = 0; i < MAP_SIZE; i++) {
            keys[i] = random.nextInt(MAP_SIZE * 2); // Keys spread out
        }
        lookupKey = keys[MAP_SIZE / 2];
        nonExistentKey = MAP_SIZE * 2 + 1;

        // 2. Setup Delegate Map
        delegateMap = new IntObjectHashMap<>(MAP_SIZE);
        for (int i = 0; i < MAP_SIZE; i++) {
            delegateMap.put(keys[i], TEST_VALUE);
        }

        // 3. Setup Comparators
        IntComparator keyComparator = new IntComparator() {
            @Override
            public int compare(int a, int b) {
                return Integer.compare(a, b);
            }
        };

        IntObjectComparator<String> keyValueComparator = new IntObjectComparator<String>() {
            @Override
            public int compare(int key1, String value1, int key2, String value2) {
                int keyComparison = Integer.compare(key1, key2);
                if (keyComparison != 0) {
                    return keyComparison;
                }
                return value1.compareTo(value2);
            }
        };

        // 4. Setup Sorted Views (This step includes the O(N log N) sorting cost)
        sortedByKeyView = new SortedIterationIntObjectHashMap<>(delegateMap, keyComparator);
        sortedByKeyAndValueView = new SortedIterationIntObjectHashMap<>(delegateMap, keyValueComparator);
    }

    // --- Benchmarks for Key-Sorted View (sortedByKeyView) ---

    @Benchmark
    public void keySorted_containsKey(Blackhole bh) {
        bh.consume(sortedByKeyView.containsKey(lookupKey));
    }

    @Benchmark
    public void keySorted_get(Blackhole bh) {
        bh.consume(sortedByKeyView.get(lookupKey));
    }

    @Benchmark
    public void keySorted_indexOf(Blackhole bh) {
        bh.consume(sortedByKeyView.indexOf(lookupKey));
    }

    @Benchmark
    public void keySorted_size(Blackhole bh) {
        bh.consume(sortedByKeyView.size());
    }

    @Benchmark
    public void keySorted_isEmpty(Blackhole bh) {
        bh.consume(sortedByKeyView.isEmpty());
    }

    @Benchmark
    public void keySorted_forEachProcedure(Blackhole bh) {
        IntObjectProcedure<String> procedure = (k, v) -> {};
        bh.consume(sortedByKeyView.forEach(procedure));
    }

    @Benchmark
    public void keySorted_forEachPredicate(Blackhole bh) {
        // Predicate that always returns true, ensuring full iteration
        IntObjectPredicate<String> predicate = (k, v) -> true;
        bh.consume(sortedByKeyView.forEach(predicate));
    }

    @Benchmark
    public void keySorted_iterator(Blackhole bh) {
        // Consume the iterator itself to measure creation overhead
        bh.consume(sortedByKeyView.iterator());
    }

    // --- Benchmarks for Key/Value Sorted View (sortedByKeyAndValueView) ---

    @Benchmark
    public void kvSorted_containsKey(Blackhole bh) {
        bh.consume(sortedByKeyAndValueView.containsKey(lookupKey));
    }

    @Benchmark
    public void kvSorted_get(Blackhole bh) {
        bh.consume(sortedByKeyAndValueView.get(lookupKey));
    }

    @Benchmark
    public void kvSorted_indexOf(Blackhole bh) {
        bh.consume(sortedByKeyAndValueView.indexOf(lookupKey));
    }

    @Benchmark
    public void kvSorted_size(Blackhole bh) {
        bh.consume(sortedByKeyAndValueView.size());
    }

    @Benchmark
    public void kvSorted_isEmpty(Blackhole bh) {
        bh.consume(sortedByKeyAndValueView.isEmpty());
    }

    @Benchmark
    public void kvSorted_forEachProcedure(Blackhole bh) {
        IntObjectProcedure<String> procedure = (k, v) -> {};
        bh.consume(sortedByKeyAndValueView.forEach(procedure));
    }

    @Benchmark
    public void kvSorted_forEachPredicate(Blackhole bh) {
        // Predicate that always returns true, ensuring full iteration
        IntObjectPredicate<String> predicate = (k, v) -> true;
        bh.consume(sortedByKeyAndValueView.forEach(predicate));
    }

    @Benchmark
    public void kvSorted_iterator(Blackhole bh) {
        // Consume the iterator itself to measure creation overhead
        bh.consume(sortedByKeyAndValueView.iterator());
    }
}
