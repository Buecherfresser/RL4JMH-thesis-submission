package bench.generated.c094;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import com.carrotsearch.hppc.SortedIterationIntIntHashMap;
import com.carrotsearch.hppc.IntIntHashMap;
import com.carrotsearch.hppc.comparators.IntComparator;
import com.carrotsearch.hppc.comparators.IntIntComparator;
import com.carrotsearch.hppc.cursors.IntIntCursor;
import com.carrotsearch.hppc.predicates.IntPredicate;
import com.carrotsearch.hppc.predicates.IntIntPredicate;
import com.carrotsearch.hppc.procedures.IntIntProcedure;
import com.carrotsearch.hppc.IntCollection;
import com.carrotsearch.hppc.IntContainer;
import java.util.Random;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class SortedIterationIntIntHashMapBenchmark {

    private SortedIterationIntIntHashMap sortedMap;
    private IntIntHashMap delegateMap;
    private IntComparator keyComparator;
    private IntIntComparator keyValueComparator;
    private final int MAP_SIZE = 10000;
    private final int DEFAULT_VALUE = 42;

    @Setup(Level.Trial)
    public void setup() {
        // 1. Setup Delegate Map
        delegateMap = new IntIntHashMap(MAP_SIZE);
        Random random = new Random(42);

        // Populate the map
        for (int i = 0; i < MAP_SIZE; i++) {
            int key = random.nextInt(MAP_SIZE * 2);
            int value = random.nextInt(MAP_SIZE * 2);
            delegateMap.put(key, value);
        }

        // 2. Setup Comparators
        // Key Comparator (based on key only)
        keyComparator = new IntComparator() {
            @Override
            public int compare(int a, int b) {
                return Integer.compare(a, b);
            }
        };

        // Key/Value Comparator (based on key then value)
        keyValueComparator = new IntIntComparator() {
            @Override
            public int compare(int k1, int v1, int k2, int v2) {
                int keyComparison = Integer.compare(k1, k2);
                if (keyComparison != 0) {
                    return keyComparison;
                }
                return Integer.compare(v1, v2);
            }
        };

        // 3. Setup SUT instances
        // Instance 1: Sorted by Key
        sortedMap = new SortedIterationIntIntHashMap(delegateMap, keyComparator);
    }

    // --- Benchmarks for Read Operations ---

    @Benchmark
    public int testContainsKey() {
        int key = 5000;
        return sortedMap.containsKey(key) ? 1 : 0;
    }

    @Benchmark
    public int testSize() {
        return sortedMap.size();
    }

    @Benchmark
    public boolean testIsEmpty() {
        return sortedMap.isEmpty();
    }

    @Benchmark
    public int testGet() {
        int key = 5000;
        return sortedMap.get(key);
    }

    @Benchmark
    public int testGetOrDefault() {
        int key = 99999; // Key likely not present
        return sortedMap.getOrDefault(key, DEFAULT_VALUE);
    }

    @Benchmark
    public int testIndexOf() {
        int key = 5000;
        return sortedMap.indexOf(key);
    }

    @Benchmark
    public boolean testIndexExists() {
        int index = 500;
        return sortedMap.indexExists(index);
    }

    @Benchmark
    public int testIndexGet() {
        int index = 500;
        return sortedMap.indexGet(index);
    }

    @Benchmark
    public String testVisualizeKeyDistribution() {
        return sortedMap.visualizeKeyDistribution(10);
    }

    // --- Benchmarks for Iteration ---

    @Benchmark
    public void testIterator(Blackhole bh) {
        bh.consume(sortedMap.iterator());
    }

    @Benchmark
    public void testForEachProcedure(Blackhole bh) {
        IntIntProcedure procedure = (k, v) -> {};
        bh.consume(sortedMap.forEach(procedure));
    }

    @Benchmark
    public void testForEachPredicate(Blackhole bh) {
        // Predicate that always returns true to ensure full iteration
        IntIntPredicate predicate = (k, v) -> true;
        bh.consume(sortedMap.forEach(predicate));
    }

    @Benchmark
    public void testForEachPredicateEarlyExit(Blackhole bh) {
        // Predicate that exits early (e.g., when key > 100)
        IntIntPredicate predicate = (k, v) -> k <= 100;
        bh.consume(sortedMap.forEach(predicate));
    }

    // --- Benchmarks for View Accessors ---

    @Benchmark
    public IntCollection testKeysContainer() {
        return sortedMap.keys();
    }

    @Benchmark
    public IntContainer testValuesContainer() {
        return sortedMap.values();
    }
}
