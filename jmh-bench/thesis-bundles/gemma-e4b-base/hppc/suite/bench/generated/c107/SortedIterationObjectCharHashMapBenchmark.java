package bench.generated.c107;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import com.carrotsearch.hppc.SortedIterationObjectCharHashMap;
import com.carrotsearch.hppc.ObjectCharHashMap;
import com.carrotsearch.hppc.comparators.ObjectCharComparator;
import com.carrotsearch.hppc.cursors.ObjectCharCursor;
import com.carrotsearch.hppc.procedures.ObjectCharProcedure;
import com.carrotsearch.hppc.predicates.ObjectCharPredicate;
import java.util.Random;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class SortedIterationObjectCharHashMapBenchmark {

    private SortedIterationObjectCharHashMap<Object> sortedMap;
    private ObjectCharHashMap<Object> delegateMap;
    private ObjectCharComparator<Object> comparator;
    private Object testKey;
    private char testValue;
    private int mapSize;

    @Setup(Level.Trial)
    public void setup() {
        // 1. Setup Delegate Map
        mapSize = 10000;
        delegateMap = new ObjectCharHashMap<>(mapSize);

        // 2. Setup Comparator (using natural ordering for Object keys)
        comparator = new ObjectCharComparator<Object>() {
            @Override
            public int compare(Object k1, char v1, Object k2, char v2) {
                // Simple comparison based on key only
                return ((String) k1).compareTo((String) k2);
            }
        };

        // 3. Populate Delegate Map
        Random random = new Random(42);
        for (int i = 0; i < mapSize; i++) {
            Object key = "Key_" + i;
            char value = (char) ('A' + (i % 26));
            delegateMap.put(key, value);
        }

        // 4. Instantiate SUT (This performs the O(N log N) sorting)
        sortedMap = new SortedIterationObjectCharHashMap<>(delegateMap, comparator);

        // 5. Setup test inputs
        testKey = "Key_500";
        testValue = 'C';
    }

    @Benchmark
    public void benchmark_containsKey(Blackhole bh) {
        bh.consume(sortedMap.containsKey(testKey));
    }

    @Benchmark
    public void benchmark_get(Blackhole bh) {
        bh.consume(sortedMap.get(testKey));
    }

    @Benchmark
    public void benchmark_getOrDefault(Blackhole bh) {
        char defaultValue = 'Z';
        bh.consume(sortedMap.getOrDefault(testKey, defaultValue));
    }

    @Benchmark
    public void benchmark_indexOf(Blackhole bh) {
        bh.consume(sortedMap.indexOf(testKey));
    }

    @Benchmark
    public void benchmark_indexExists(Blackhole bh) {
        bh.consume(sortedMap.indexExists(500));
    }

    @Benchmark
    public void benchmark_indexGet(Blackhole bh) {
        bh.consume(sortedMap.indexGet(500));
    }

    @Benchmark
    public void benchmark_size(Blackhole bh) {
        bh.consume(sortedMap.size());
    }

    @Benchmark
    public void benchmark_isEmpty(Blackhole bh) {
        bh.consume(sortedMap.isEmpty());
    }

    @Benchmark
    public void benchmark_keysContainer_size(Blackhole bh) {
        bh.consume(sortedMap.keys().size());
    }

    @Benchmark
    public void benchmark_valuesContainer_size(Blackhole bh) {
        bh.consume(sortedMap.values().size());
    }

    @Benchmark
    public void benchmark_forEach_procedure(Blackhole bh) {
        ObjectCharProcedure<Object> procedure = (k, v) -> {};
        bh.consume(sortedMap.forEach(procedure));
    }

    @Benchmark
    public void benchmark_forEach_predicate(Blackhole bh) {
        ObjectCharPredicate<Object> predicate = (k, v) -> true;
        bh.consume(sortedMap.forEach(predicate));
    }

    @Benchmark
    public void benchmark_iterator_fetch(Blackhole bh) {
        // Measure the cost of fetching the first element using next()
        ObjectCharCursor<Object> cursor = sortedMap.iterator().next();
        bh.consume(cursor);
    }

    @Benchmark
    public void benchmark_visualizeKeyDistribution(Blackhole bh) {
        int characters = 10;
        bh.consume(sortedMap.visualizeKeyDistribution(characters));
    }
}
