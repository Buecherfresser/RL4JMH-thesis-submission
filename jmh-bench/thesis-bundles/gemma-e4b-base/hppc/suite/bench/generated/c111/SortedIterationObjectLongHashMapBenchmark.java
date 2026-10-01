package bench.generated.c111;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.Comparator;
import com.carrotsearch.hppc.SortedIterationObjectLongHashMap;
import com.carrotsearch.hppc.ObjectLongHashMap;
import com.carrotsearch.hppc.comparators.ObjectLongComparator;
import com.carrotsearch.hppc.cursors.ObjectLongCursor;
import com.carrotsearch.hppc.procedures.ObjectLongProcedure;
import com.carrotsearch.hppc.predicates.ObjectLongPredicate;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class SortedIterationObjectLongHashMapBenchmark {

    private ObjectLongHashMap<String> delegateMap;
    private SortedIterationObjectLongHashMap<String> sortedViewKeyComparator;
    private SortedIterationObjectLongHashMap<String> sortedViewValueComparator;

    private String[] keysToTest;
    private String keyForGet;
    private String keyForContains;

    @Setup(Level.Trial)
    public void setup() {
        // 1. Setup Delegate Map
        int mapSize = 10000;
        delegateMap = new ObjectLongHashMap<>(mapSize);

        // Populate the map
        for (int i = 0; i < mapSize; i++) {
            String key = "Key_" + i;
            long value = i * 10L;
            delegateMap.put(key, value);
        }

        // 2. Setup Views
        // Key Comparator (String natural order)
        Comparator<String> keyComparator = Comparator.naturalOrder();
        sortedViewKeyComparator = new SortedIterationObjectLongHashMap<>(delegateMap, keyComparator);

        // Key/Value Comparator (Sort by Key, then by Value)
        ObjectLongComparator<String> valueComparator = new ObjectLongComparator<String>() {
            @Override
            public int compare(String key1, long value1, String key2, long value2) {
                int keyComparison = key1.compareTo(key2);
                if (keyComparison != 0) {
                    return keyComparison;
                }
                return Long.compare(value1, value2);
            }
        };
        sortedViewValueComparator = new SortedIterationObjectLongHashMap<>(delegateMap, valueComparator);

        // 3. Setup Test Inputs
        keysToTest = new String[mapSize];
        for (int i = 0; i < mapSize; i++) {
            keysToTest[i] = "Key_" + (mapSize - 1 - i); // Test reverse order access
        }
        keyForGet = "Key_5000";
        keyForContains = "Key_9999";
    }

    @Benchmark
    public void testContainsKey_KeyComparator(Blackhole bh) {
        bh.consume(sortedViewKeyComparator.containsKey(keyForContains));
    }

    @Benchmark
    public void testContainsKey_ValueComparator(Blackhole bh) {
        bh.consume(sortedViewValueComparator.containsKey(keyForContains));
    }

    @Benchmark
    public void testGet_KeyComparator(Blackhole bh) {
        bh.consume(sortedViewKeyComparator.get(keyForGet));
    }

    @Benchmark
    public void testGet_ValueComparator(Blackhole bh) {
        bh.consume(sortedViewValueComparator.get(keyForGet));
    }

    @Benchmark
    public void testSize(Blackhole bh) {
        bh.consume(sortedViewKeyComparator.size());
    }

    @Benchmark
    public void testIteration_KeyComparator(Blackhole bh) {
        ObjectLongProcedure<String> procedure = (k, v) -> {};
        bh.consume(sortedViewKeyComparator.forEach(procedure));
    }

    @Benchmark
    public void testIteration_ValueComparator(Blackhole bh) {
        ObjectLongProcedure<String> procedure = (k, v) -> {};
        bh.consume(sortedViewValueComparator.forEach(procedure));
    }

    @Benchmark
    public void testKeysContainer_Iteration(Blackhole bh) {
        // Test iteration over keys view
        sortedViewKeyComparator.keys().iterator();
        bh.consume(true);
    }

    @Benchmark
    public void testValuesContainer_Iteration(Blackhole bh) {
        // Test iteration over values view
        sortedViewKeyComparator.values().iterator();
        bh.consume(true);
    }
}
